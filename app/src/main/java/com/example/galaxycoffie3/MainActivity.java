package com.example.galaxycoffie3;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;

/**
 * The main Activity Class, here we have a simple opening screen with video background
 */
public class MainActivity extends AppCompatActivity {


    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /*
    Create a VideoView variable, a MediaPlayer variable, and an int to hold the current
    video position.
    */
    private VideoView videoBackGround;
    /* the media player object, for playing the video in the background */
    MediaPlayer mMediaPlayer;
    /* the position we are at on the video */
    int mCurrentVideoPosition;
    /* a button with walkingCoffeeAnimation for entering the ordering activity*/
    LottieAnimationView orderCoffeeButton;
    /* a confetti walkingCoffeeAnimation for when we press the app logo*/
    LottieAnimationView confetti;
    /* the logo of our app*/
    ImageView logo;

    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Hook up the VideoView to our UI.
        videoBackGround = findViewById(R.id.videoView);

        // Build the video Uri
        Uri uri = Uri.parse("android.resource://"
                + getPackageName()
                + "/"
                + R.raw.coffee_video_2);
        // in the raw folder.

        // Set the new Uri to our VideoView
        videoBackGround.setVideoURI(uri);
        // Start the VideoView
        videoBackGround.start();

        // Set an OnPreparedListener for our VideoView.
        videoBackGround.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer = mediaPlayer;
                // We want our video to play over and over so we set looping to true.
                mMediaPlayer.setLooping(true);
                // We then seek to the current position if it has been set and play the video.
                if (mCurrentVideoPosition != 0) {
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();
                }
            }
        });
        // our text which will disappear when the walkingCoffeeAnimation starts
        final TextView getCoffeeText = findViewById(R.id.getCoffee);

        orderCoffeeButton = findViewById(R.id.animation);
        orderCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here we hide the text so we will fully see the walkingCoffeeAnimation
                getCoffeeText.setText(null);
                // here we set the walkingCoffeeAnimation
                orderCoffeeButton.setProgress(0);
                orderCoffeeButton.playAnimation();
                orderCoffeeButton.setEnabled(false);
                // sets a handler to do something after x time
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //go to ChooseCoffeeWindow after 900ms
                        orderCoffeeButton.setEnabled(true);
                        Intent addIntent = new Intent(getApplicationContext(), ChooseCoffeeWindow.class);
                        startActivity(addIntent);
                    }
                }, 900);
            }
        });
        confetti = findViewById(R.id.confetti);
        logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here we make a confetti walkingCoffeeAnimation when someone press the logo
                confetti.setVisibility(View.VISIBLE);
                confetti.setProgress(0);
                confetti.playAnimation();
            }
        });
    }


    /**
     * We must override onPause(), onResume(), and onDestroy() to properly handle our
     * VideoView.
     **/
    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        videoBackGround.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        videoBackGround.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
