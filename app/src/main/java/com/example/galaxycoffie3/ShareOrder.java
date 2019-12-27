package com.example.galaxycoffie3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This activity is responsible for a screen which allows the user to take a picture of his order
 * using his camera. Afterwords, the user can share his photo in social media, through this screen.
 */
public class ShareOrder extends AppCompatActivity {

    public static final String PRESS_TO_START_OVER_MSG = "Press again to return to the Start Page";
    /* Represents the media file type(image/video) that were gonna share using the instegram intent */
    String typeOfMedia = "image/*";
    /* The image view which contains the photo that the user took in this screen */
    private ImageView userPicture;
    private android.net.Uri file;
    /* The file which contains the photo the user took*/
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo");
    /* indicates weather the user has pressed 2 times on the default "return" button in his phone*/
    boolean secondPressReturn = false;
    /* the data object is useful in this class for the purpose of clearing the shopping cart in
     * the case where the user pressed twice on the return button(and by that got back to the first
     * screen*/
    Data data = Data.getSingleton();
    /* the animations which appears in this screen*/
    LottieAnimationView cameraButton, finger_gesture, publishToInstegram;
    /* The layout of the screen, used for controlling the screen background */
    ConstraintLayout screenLayout;
    /* mediaPath is the path of the file which contains the photo the user took, and imName is the
     *  unique name we give to each image the user take */
    String mediaPath, imName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_order);
        //setting up the screen background with gradient animation
        screenLayout = findViewById(R.id.relativeLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) screenLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        //Build upon an existing VmPolicy
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        userPicture = findViewById(R.id.userPicture);
        cameraButton = findViewById(R.id.cameraButton);
        //Check if there's a permission to access camera and external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        publishToInstegram = findViewById(R.id.share_instegram);
        //we want this button to appear only after the picture was taken
        publishToInstegram.setVisibility(View.INVISIBLE);
        publishToInstegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInstagramIntent(typeOfMedia, mediaPath);
            }
        });
        finger_gesture = findViewById(R.id.finger_gesture);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generates a unique name and path for the each file which gonna contain the image
                // the user took
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imName = "IMG_" + timeStamp + ".jpg";
                String filename = "/" + imName;
                mediaPath = mediaStorageDir.getPath() + File.separator + filename;
                cameraButton.setProgress(0);
                cameraButton.playAnimation();
                finger_gesture.setVisibility(View.INVISIBLE);
                //v
                final Handler cameraClickedHandler = new Handler();
                //let animation run for 800 millisec and then move to the default camera app on
                // the user's phone
                cameraClickedHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = Uri.fromFile(getOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                        startActivityForResult(intent, 100);
                    }
                }, 800);
            }
        });
        finger_gesture.setProgress(0);
        finger_gesture.playAnimation();
    }

    /**
     * Callback for the result from requesting permissions.
     *
     * @param requestCode  The request code passed in requestPermissions
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraButton.setEnabled(true);
            }
        }
    }


    /**
     * This function is called When the user is done with the subsequent activity and returns. if
     * everything went well we would like to set the taken image on screen and show the publish to
     * instegram button
     *
     * @param requestCode identifies from which Intent we came back.
     * @param resultCode  indicates weather the request was successful.
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                userPicture.setImageURI(file);
                //image frame
                userPicture.setBackground(getDrawable(R.drawable.image_frame));
                publishToInstegram.setVisibility(View.VISIBLE);
                publishToInstegram.setProgress(0);
                publishToInstegram.playAnimation();
            }
        }


    }

    /**
     * This function creates a new file with given path (if the mediaStorageDir exists)
     *
     * @return the new file which is the photo the user took
     */
    private File getOutputMediaFile() {
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator +
                imName);
    }

    /**
     * This function creates the instegram(and other apps) publish intent
     *
     * @param typeOfMedia indicates weather we want to share an image or a video
     * @param mediaPath   the image file path
     */
    private void createInstagramIntent(String typeOfMedia, String mediaPath) {

        // Create the new Intent using the 'Send' action.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Set the MIME type(image/video)
        shareIntent.setType(typeOfMedia);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    /**
     * This function overrides the default "return" button behaviour. If the user pressed the return
     * button twice, the user goes back to the first screen, and the shopping cart is being
     * initialized.
     */
    @Override
    public void onBackPressed() {
        if (secondPressReturn) {
            Intent startOverIntent = new Intent(getApplicationContext(), MainActivity.class);
            startOverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startOverIntent);
            data.shopCart.clear();
            finish();
        } else {
            secondPressReturn = true;
            Toast.makeText(ShareOrder.this, PRESS_TO_START_OVER_MSG, Toast.LENGTH_SHORT).show();
        }
    }
}