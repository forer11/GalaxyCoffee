package com.example.galaxycoffie3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

/**
 * This activity represents the shopping cart, which contains all the user orders.
 */
public class ShoppingCart extends AppCompatActivity {

    public static final String EMPTY_SHOPPING_CART_MSG = "Shopping Cart is Empty\n" +
            " Add Items by pressing the '+' Button";
    public static final String NO_MILK_CHOOSE_MSG = "You Forgot to set Milk to Some\n" +
            "of your items.\n";
    public static final String MAX_ORDER_EXCCEEDED_MSG = "Only 4 Items Allowed Per User";
    /* The parent linear layout, used for knowing each line's location on screen */
    private LinearLayout parentLinearLayout;
    /* button animation which allows user to add another order to his cart */
    LottieAnimationView AddToCartButton;
    /* button which transfer the user to payment screen if being pressed */
    Button goToPaymentButton;
    /* the order's total price */
    TextView totalPrice;
    /* we use the data object here for information about user orders */
    Data data = Data.getSingleton();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        totalPrice = findViewById(R.id.total_price);
        parentLinearLayout = findViewById(R.id.parent_linear_layout);
        goToPaymentButton = findViewById(R.id.goToPayment);
        AddToCartButton = findViewById(R.id.returnToCoffee);
        //update user's order in cart screen
        for (int orderNum = 0; orderNum < data.shopCart.size(); orderNum++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View rowView = inflater.inflate(R.layout.field, null);
            // Add the new row before the add field button.
            parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
            TextView userOrder = rowView.findViewById(R.id.cur_order);
            TextView price = rowView.findViewById(R.id.price);
            Button changeMilkButton = rowView.findViewById(R.id.choose_milk);
            AnimationDrawable milkButtonAnimation = (AnimationDrawable) changeMilkButton.getBackground();
            milkButtonAnimation.setEnterFadeDuration(2000);
            milkButtonAnimation.setExitFadeDuration(4000);
            milkButtonAnimation.start();
            ImageView coffeeImage = rowView.findViewById(R.id.imageView);
            Coffee coffee = data.shopCart.get(orderNum);
            userOrder.setText(Data.getCoffeeName(coffee.getCoffeeType()));
            String priceStr = Data.getPriceByType(coffee.getCoffeeType()) + "$";
            price.setText(priceStr);
            changeMilkButton.setText(Data.getMilkName(coffee.getMilkType()));
            coffeeImage.setImageResource(data.returnImageId(coffee.getCoffeeType()));
        }
        String totalTextView = "Total: " + data.getTotalPrice() + "$";
        totalPrice.setText(totalTextView);
        //move to payment screen
        goToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //edge cases
                if (checkIfMilkSet()) {
                    Intent addIntent = new Intent(getApplicationContext(), Payment.class);
                    startActivity(addIntent);

                } else if (data.shopCart.size() == 0) {
                    Toast.makeText(ShoppingCart.this,
                            EMPTY_SHOPPING_CART_MSG, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShoppingCart.this, NO_MILK_CHOOSE_MSG,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        //move to chooseCoffee screen
        AddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.shopCart.size() < Data.MAX_ITEM_AMOUNT) {
                    AddToCartButton.setProgress(0);
                    AddToCartButton.playAnimation();
                    AddToCartButton.setEnabled(false);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent addIntent = new Intent(getApplicationContext(),
                                    ChooseCoffeeWindow.class);
                            startActivity(addIntent);
                            finish();
                        }
                    }, 800);
                } else {
                    Toast.makeText(ShoppingCart.this, MAX_ORDER_EXCCEEDED_MSG,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * This function is called When the user is done with the subsequent activity and returns.
     *
     * @param requestCode identifies from which Intent we came back.
     * @param resultCode  indicates weather the request was successful.
     * @param intent      An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        finish();
    }

    /*
       This function gets the current index of the current line index in order
     */
    private int getLineIdx(View v) {
        ViewParent field = v.getParent();
        ViewParent linearLayout = field.getParent();
        return ((ViewGroup) linearLayout).indexOfChild((View) field);
    }

    /*
    controls what happens when the user presses the choose milk button
     */
    public void onChooseMilk(View v) {
        v.setEnabled(false);
        data.currentIndex = getLineIdx(v);
        data.inShopCart = true;
        ConstraintLayout myLayout = findViewById(R.id.activity_shopping_cart);
        myLayout.setAlpha((float) 0.35);
        Intent addIntent = new Intent(getApplicationContext(), ChooseMilkWindow.class);
        startActivityForResult(addIntent, 1);
    }

    /*
    controls what happens when the user presses the 'x' button
    */
    public void onDelete(View v) {
        data.shopCart.remove(getLineIdx(v));
        parentLinearLayout.removeView((View) v.getParent());
        String totalTextView = "Total: " + data.getTotalPrice() + "$";
        totalPrice.setText(totalTextView);
    }

    /*
   controls what happens when the user presses the go back(->) button
    */
    @Override
    public void onBackPressed() {
        data.badge.setNumber(data.shopCart.size());
        super.onBackPressed();
    }

    /*
    This function checks weather the user chose milk for each milk he ordered
     */
    boolean checkIfMilkSet() {
        if (data.shopCart.size() == 0) {
            return false;
        }
        for (Coffee coffee : data.shopCart) {
            if (coffee.getMilkType() == Data.MilkTypes.NoMilk) {
                return false;
            }
        }
        return true;
    }
}
