package com.example.galaxycoffie3;

import androidx.appcompat.app.AlertDialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This activity represent the payment activity of the app.
 * This is the activity where the user pay for the coffee he ordered.
 */
public class Payment extends Activity implements View.OnTouchListener {

    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /* we use the data object in this screen in order to get information about the user orders */
    Data data = Data.getSingleton();
    /* the fields in which the user enters his credit cart/name */
    private EditText creditCardExpField, creditCardNumberField, cvvField, nameField;
    //TODO - avriram's animation
    private Handler mHandler;
    /* indicated user's input being valid or not */
    private boolean isValidDate, isValidCard, isValidCvv, isValidName = false;
    /* indicates weather the payment is finished */
    private boolean paymentDone = false;
    /* the end of payment animation */
    private ObjectAnimator progressAnimator;


    /**
     * this function responsible to initialize all the elements in this activity, get all the
     * relevant user data from the Data object and display the order details.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ImageView endOfPaymentButton = findViewById(R.id.endOfPaymentButton);
        ImageView question_mark_button = findViewById(R.id.questionMark);
        //initialization of all fields and their corresponding watchers
        creditCardExpField = findViewById(R.id.expDateField);
        creditCardExpField.addTextChangedListener(expDateWatcher);
        creditCardNumberField = findViewById(R.id.creditCardField);
        creditCardNumberField.addTextChangedListener(creditCardWatcher);
        cvvField = findViewById(R.id.cvvField);
        cvvField.addTextChangedListener(cvvWatcher);
        nameField = findViewById(R.id.nameField);
        nameField.addTextChangedListener(nameWatcher);
        creditCardNumberField.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
        //end of payment progress bar animation
        ProgressBar prg = findViewById(R.id.progressBarAnimation);
        progressAnimator = ObjectAnimator.ofInt(prg, "progress", 0, 100);
        progressAnimator.setDuration(2000);
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationCancel(animation);
                Intent addIntent = new Intent(getApplicationContext(), TransitionScreen.class);
                addIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(addIntent);
                finish();
            }
        });
        //controls what happens when user presses the question mark button-displayed a message
        //requesting from him to enter his name and explains why
        question_mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Payment.this).create();
                alertDialog.setTitle(Data.NAME_DIALOG_TITLE);
                alertDialog.setMessage(Data.NAME_DIALOG_MSG);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        question_mark_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        endOfPaymentButton.setOnTouchListener(this);
        cartDisplay();
    }


    /**
     * a function to display all the cart elements the user ordered
     */
    private void cartDisplay() {
        TextView orderDetails = findViewById(R.id.orderDetails);
        TextView totalOrder = findViewById(R.id.totalOrder);
        float total = 0;
        ArrayList<Coffee> cart = data.shopCart;
        for (int i = 0; i < cart.size(); i++) {
            Coffee currCoffee = cart.get(i);
            total += Data.getPriceByType(currCoffee.getCoffeeType());
            orderDetails.append(Data.getCoffeeName(currCoffee.getCoffeeType()));
            orderDetails.append(",\t" + Data.getMilkName(currCoffee.getMilkType()));
            orderDetails.append(",\t" + Data.getPriceByType(currCoffee.getCoffeeType()));
            orderDetails.append("$\n");
        }
        String totalOrderString = "\nTOTAL - " + total + "$";
        totalOrder.setText(totalOrderString);
    }

    /**
     * this object checks the text inserted to the CVV box
     */
    private TextWatcher cvvWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length() < 3) {
                isValid = false;
            }

            if (!isValid) {
                cvvField.setError(Data.INVALID_CVV_NUM_MSG);
            } else {
                cvvField.setError(null);
            }
            isValidCvv = isValid;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * this object checks the text inserted to the credit card number box
     */
    private TextWatcher creditCardWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length() < 19) {
                isValid = false;
            }

            if (!isValid) {
                creditCardNumberField.setError(Data.INVALID_CREDIT_CARD_NUM_MSG);
            } else {
                creditCardNumberField.setError(null);
            }
            isValidCard = isValid;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * this object checks the text inserted to the date box
     */
    private TextWatcher expDateWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;
            if (working.length() == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                    isValid = false;
                } else {
                    working += "/";
                    creditCardExpField.setText(working);
                    creditCardExpField.setSelection(working.length());
                }
            } else if (working.length() == 5 && before == 0) {
                String enteredYear = working.substring(3);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt("20" + enteredYear) < currentYear) {
                    isValid = false;
                }
            } else if (working.length() != 5) {
                isValid = false;
            }

            if (!isValid) {
                creditCardExpField.setError(Data.INVALID_EXP_DATE_MSG);
            } else {
                creditCardExpField.setError(null);
            }
            isValidDate = isValid;

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

    };

    /**
     * this object checks the text inserted to the Name box
     */
    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String working = s.toString();
            boolean isValid = true;

            if (working.length() < 1) {
                nameField.setError(Data.EMPTY_NAME_MSG);
                isValid = false;
            } else {
                nameField.setError(null);
                data.costumerName = working;
            }
            isValidName = isValid;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * this object IS responsible to execute the progress bar around the V key
     */
    Runnable mAction = new Runnable() {
        @Override
        public void run() {
            if (!paymentDone && isValidDate && isValidCard && isValidCvv && isValidName) {
                creditCardExpField.setError(null);
                creditCardNumberField.setError(null);
                cvvField.setError(null);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressAnimator.start();
                    }
                }, 1);
                paymentDone = true;
            } else {
                if (isValidDate) {
                    creditCardExpField.setError(null);
                } else {
                    creditCardExpField.setError(Data.INVALID_EXP_DATE_MSG);
                }
                if (isValidCard) {
                    creditCardNumberField.setError(null);
                } else {
                    creditCardNumberField.setError(Data.INVALID_CREDIT_CARD_NUM_MSG);
                }
                if (isValidCvv) {
                    cvvField.setError(null);
                } else {
                    cvvField.setError(Data.INVALID_CVV_NUM_MSG);
                }
                if (isValidName) {
                    nameField.setError(null);
                } else {
                    nameField.setError(Data.EMPTY_NAME_MSG);
                }
            }
        }
    };

    /**
     * this function execute when the user tap on the screen. This functions checks on which element
     * the user pressed and continue accordingly
     * //todo
     *
     * @param view  the view object
     * @param event the event that occurred
     * @return true if handler is null else false
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent event) {
        System.out.println(event.getAction());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler = new Handler();
            mHandler.postDelayed(mAction, 10);
        } else {
            if (mHandler == null) {
                return true;
            }
            mHandler = null;
        }
        return false;
    }


    /**
     * Formatting a credit card number: #### #### #### #####
     */
    public static class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;
        }
    }
}