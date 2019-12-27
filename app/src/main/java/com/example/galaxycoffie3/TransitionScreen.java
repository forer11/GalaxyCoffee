package com.example.galaxycoffie3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;

import com.airbnb.lottie.LottieAnimationView;
import com.tomer.fadingtextview.FadingTextView;

/**
 * the screen were we are transitioning from the payment activity to the Share order activity
 */
public class TransitionScreen extends AppCompatActivity {
    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/
    /* the animation we show on the center of the page*/
    LottieAnimationView walkingCoffeeAnimation;
    /* the data singleton (our server of sorts) */
    Data data = Data.getSingleton();

    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_screen);

        // the string array we initialize the fading text view with
        String[] texts = {data.costumerName + ", Your Order is Being Prepared",
                "#GalaxyBucks\nExplore Our Galaxy"};
        FadingTextView fadingTextView = findViewById(R.id.costumerMessage);
        fadingTextView.setTexts(texts);

        // here we start the walking coffee animation
        walkingCoffeeAnimation = findViewById(R.id.animation);
        walkingCoffeeAnimation.setProgress(0);
        walkingCoffeeAnimation.playAnimation();
        walkingCoffeeAnimation.setRepeatCount(Animation.INFINITE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // after 6.5 seconds we go on to the share order activity
                Intent addIntent = new Intent(getApplicationContext(), ShareOrder.class);
                addIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(addIntent);
                finish();
            }
        }, 6500);

    }

    /**
     * here we are overriding the default behavior of the default back button to suit our needs
     */
    @Override
    public void onBackPressed() {
        // we do not wont to allow back button after payment was maid
    }


}
