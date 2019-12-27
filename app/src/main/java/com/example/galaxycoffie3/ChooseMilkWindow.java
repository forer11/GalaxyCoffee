package com.example.galaxycoffie3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * a popup Activity where the user chooses the milk for the chosen coffee
 */
public class ChooseMilkWindow extends AppCompatActivity {

    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /* our view Pager where we have changing milk images */
    ViewPager viewPager;
    /* the textView, where we show the current milk name (the one displaying on the viewPager) */
    TextView milkNameText;
    /* the plus button the user press the add the current milk*/
    ImageButton addMilkButton;
    /* the data singleton (our server of sorts)*/
    Data data = Data.getSingleton();
    /* the slider dots indicating what slide we are on at the moment */
    LinearLayout sliderDotsPanel;
    /* the number of dots we have */
    private int dotsCount;
    /* the dots images */
    private ImageView[] dots;
    /* the Milk type the customer choose, by default the firs one is the regular milk */
    Data.MilkTypes currMilk = Data.MilkTypes.RegularMilk;


    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_milk_window);

        // here we are transforming this activity to be a popup activity by changing the scale
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .7), (int) (height * .75));

        viewPager = findViewById(R.id.viewPager);
        sliderDotsPanel = findViewById(R.id.SliderDots1);

        // here we make a new ViewPagerAdapter with the photos of the milk we offer the costumers
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,
                new int[]{R.drawable.regular_milk, R.drawable.soy_milk,
                        R.drawable.almond_milk, R.drawable.rice_milk}, Data.STATE_MILK);

        //sets the viewpager adapter to the one we made
        viewPager.setAdapter(viewPagerAdapter);

        // update the dots
        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotsPanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        addMilkButton = findViewById(R.id.proceed_coffee);
        // the add milk listener, by pressing we will update the current milk chosen and finish the
        // activity, returning to the calling activity. we have identifier for if we opened the
        // milk activity from the coffee activity or the cart activity.
        addMilkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.shopCart.get(data.currentIndex).setMilkType(currMilk);
                // if we called from the cart activity
                if (data.inShopCart) {
                    data.inShopCart = false;
                    Intent addIntent = new Intent(getApplicationContext(), ShoppingCart.class);
                    startActivity(addIntent);
                    finish();

                }
                // if we called from the coffee activity we return with RESULT_OK flag
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });

        // a view pager listener where we can update the milk chosen last
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                milkNameText = findViewById(R.id.coffee_type);
                // here we update the milk name tag in the bottom of the screen to
                // show the relevant milk name, the position is the slide we are at on the
                // view pager
                if (position == 0) {
                    milkNameText.setText(Data.REGULAR_MILK);
                    currMilk = Data.MilkTypes.RegularMilk;
                } else if (position == 1) {
                    milkNameText.setText(Data.SOY_MILK);
                    currMilk = Data.MilkTypes.SoyMilk;
                } else if (position == 2) {
                    milkNameText.setText(Data.ALMOND_MILK);
                    currMilk = Data.MilkTypes.AlmondMik;

                } else if (position == 3) {
                    milkNameText.setText(Data.RICE_MILK);
                    currMilk = Data.MilkTypes.RiceMilk;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * here we are overriding the default behavior of the default back button to suit our needs
     * while retaining it functionality
     */
    @Override
    public void onBackPressed() {
        // if we opened this activity from the cart activity we will return there
        if (data.inShopCart) {
            data.inShopCart = false;
            Intent addIntent = new Intent(getApplicationContext(), ShoppingCart.class);
            startActivity(addIntent);
            finish();

            // otherwise we return to the coffee activity
        } else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

}