package com.example.galaxycoffie3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.nex3z.notificationbadge.NotificationBadge;

/**
 * In ths activity we let the user choose his coffee and milk type of said coffee.
 */
public class ChooseCoffeeWindow extends AppCompatActivity {


    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /* our view Pager where we have changing coffee images */
    ViewPager viewPager;
    /* the textView, where we show the current coffee name (the one displaying on the viewPager) */
    TextView coffeeNameText;
    /* the cart button with walkingCoffeeAnimation, by pressing this button we go to the Cart activity */
    LottieAnimationView cart;
    /* the coffee type the customer choose, by default the firs one is the MilkyWay Coffee */
    Data.CoffeeTypes coffeeType = Data.CoffeeTypes.MilkyWayCoffee;
    /* the data singleton (our server of sorts)*/
    Data data = Data.getSingleton();
    /* the slider dots indicating what slide we are on at the moment */
    LinearLayout sliderDotsPanel;
    /* the number of dots we have */
    private int dotsCount;
    /* the dots images */
    private ImageView[] dots;
    /* the shopping cart icon notification badge, i.e the indicator of how many items we have
     *  in pur cart right now*/
    NotificationBadge badge;
    /* we save our layout so later on when we choose milk type we can set this layout alpha
     *  by doing it we can make it darker */
    ConstraintLayout myLayout;
    /* the button that the user adds the selected coffee with */
    LottieAnimationView addCoffeeButton;


    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coffee_window);
        // sets myLayout to this activity layout
        myLayout = findViewById(R.id.activity_window1);

        // here we are activating the background walkingCoffeeAnimation, it will switch 3 kind of backgrounds
        // with different gradient effects.
        AnimationDrawable animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        badge = findViewById(R.id.badge);
        // add the badge object to data so we can update the badge when we return from the cart
        data.badge = badge;
        badge.setNumber(data.shopCart.size());

        viewPager = findViewById(R.id.viewPager);
        cart = findViewById(R.id.shop_cart_view);
        sliderDotsPanel = findViewById(R.id.SliderDots1);

        // here we make a new ViewPagerAdapter with the photos of the coffees we offer the costumers
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,
                new int[]{R.drawable.coffee1, R.drawable.coffee2,
                        R.drawable.coffee3, R.drawable.coffee4}, Data.STATE_COFFEE);

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

        // the cart listener, by pressing the cart icon we go to the cart activity
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.shopCart.size() != Data.EMPTY) {
                    Intent addIntent = new Intent(getApplicationContext(), ShoppingCart.class);
                    startActivity(addIntent);
                } else {
                    Toast.makeText(ChooseCoffeeWindow.this, Data.CART_EMPTY_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });


        addCoffeeButton = findViewById(R.id.proceed_coffee);
        // the addCoffeeButton listener, by pressing this button we open a pop up for the
        // choose milk activity, and later if chosen successfully, we update our cart
        addCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a new coffee object
                Coffee coffee = new Coffee(coffeeType, Data.getPriceByType(coffeeType));
                if (data.addItem(coffee) == Data.ADD_SUCCESS) {
                    // here we will activate the walkingCoffeeAnimation and open the choose milk popup
                    addCoffeeButton.setEnabled(false);
                    cart.setEnabled(false);
                    addCoffeeButton.setProgress(0);
                    addCoffeeButton.playAnimation();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            data.currentIndex = data.shopCart.size() - 1;
                            data.layout = myLayout;
                            // set the background to be darker, so the popup will be better
                            myLayout.setAlpha((float) 0.35);
                            // open the choose milk popup
                            Intent addIntent = new Intent(getApplicationContext(), ChooseMilkWindow.class);
                            startActivityForResult(addIntent, 1);
                        }
                    }, 700);
                } else {
                    Toast.makeText(ChooseCoffeeWindow.this, Data.ONLY_4_ITEMS_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // a view pager listener where we can update the coffee chosen last
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.active_dot));

                // here we update the coffee name tag in the bottom of the screen to
                // show the relevant coffee name, the position is the slide we are at on the
                // view pager
                coffeeNameText = findViewById(R.id.coffee_type);
                String setToText;
                if (position == 0) {
                    coffeeType = Data.CoffeeTypes.MilkyWayCoffee;
                    setToText = Data.getCoffeeName(coffeeType) + " " +
                            Data.getPriceByType(coffeeType) + " $";
                    coffeeNameText.setText(setToText);
                } else if (position == 1) {
                    coffeeType = Data.CoffeeTypes.SpaceUnicornCoffee;
                    setToText = Data.getCoffeeName(coffeeType) + " " +
                            Data.getPriceByType(coffeeType) + " $";
                    coffeeNameText.setText(setToText);
                } else if (position == 2) {
                    coffeeType = Data.CoffeeTypes.NebulaCoffee;
                    setToText = Data.getCoffeeName(coffeeType) + " " +
                            Data.getPriceByType(coffeeType) + " $";
                    coffeeNameText.setText(setToText);

                } else if (position == 3) {
                    coffeeType = Data.CoffeeTypes.CelestialCoffee;
                    setToText = Data.getCoffeeName(coffeeType) + " " +
                            Data.getPriceByType(coffeeType) + " $";
                    coffeeNameText.setText(setToText);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * here when we return from the choose milk activity, we update our data according to the
     * user's choice.
     *
     * @param requestCode The request code passed by the sending activity
     * @param resultCode  The result code we get
     * @param intent      the intent we returned from
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            // if a choice been made
            if (resultCode == Activity.RESULT_OK) {
                Toast toast = Toast.makeText(ChooseCoffeeWindow.this, Data.getCoffeeName(coffeeType) + " Added to Cart", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 55);
                toast.show();
                // we start the cart walkingCoffeeAnimation
                cart.setProgress(0);
                cart.playAnimation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // we update the cart badge
                        badge.setNumber(data.shopCart.size());
                    }
                }, 300);
            }
            // otherwise the user used the default return button
            if (resultCode == Activity.RESULT_CANCELED) {
                data.shopCart.remove(data.shopCart.size() - 1);
            }
        }
        // restore the background and enable the buttons
        myLayout.setAlpha(1);
        addCoffeeButton.setEnabled(true);
        cart.setEnabled(true);
    }

    /**
     * here we are overriding the default behavior of the default back button to suit our needs
     * while retaining it functionality
     */
    @Override
    public void onBackPressed() {
        // when pressing back we return to the start screen, maintaining the app fluidity
        Intent addIntent = new Intent(getApplicationContext(), MainActivity.class);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(addIntent);
        finish();
    }

}