package com.example.galaxycoffie3;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

/**
 * Singleton of a Data object containing all the data our app needs.
 */
class Data {

    /*=============================================================================*/
    /*                                Constants                                    */
    /*=============================================================================*/
    private static final String SPACE_UNICORN_COFFEE = "Space Unicorn";
    private static final String MILKY_WAY_COFFEE = "Milky Way";
    private static final String NEBULA_COFFEE = "Nebula";
    private static final String CELESTIAL_COFFEE = "Celestial";
    private static final int UNICORN_PRICE = 3;
    private static final int MILKYWAY_PRICE = 4;
    private static final int NEBULA_PRICE = 2;
    private static final int CELESTIAL_PRICE = 5;
    private static final String NO_MILK = "Choose Milk";
    static final int ADD_SUCCESS = 0;
    private static final int ADD_FAILURE = -1;
    static final int MAX_ITEM_AMOUNT = 4;
    static final int STATE_COFFEE = 0;
    static final int STATE_MILK = 1;
    static final int EMPTY = 0;
    static final String CART_EMPTY_MSG = "Your Cart is Empty\n" +
            "Please Choose at least one item to continue";
    static final String ONLY_4_ITEMS_MSG = "Only 4 Items Allowed Per User";
    static final String REGULAR_MILK = "Regular Milk";
    static final String SOY_MILK = "Soy Milk";
    static final String ALMOND_MILK = "Almond Milk";
    static final String RICE_MILK = "Rice Milk";
    static final String NAME_DIALOG_TITLE = "What do you need my name for?";
    static final String NAME_DIALOG_MSG = "With this name we will call you when Your order is ready";
    static final String INVALID_EXP_DATE_MSG = "Enter a valid date: MM/YY";
    static final String INVALID_CREDIT_CARD_NUM_MSG = "Enter a valid credit card number (16 digits)";
    static final String INVALID_CVV_NUM_MSG = "Enter a valid cvv number (3 digits)";
    static final String EMPTY_NAME_MSG = "Enter a Name";

    /*=============================================================================*/
    /*                                Enums                                        */
    /*=============================================================================*/

    /**
     * Enum of coffee types
     */
    enum CoffeeTypes {
        MilkyWayCoffee, SpaceUnicornCoffee, NebulaCoffee, CelestialCoffee
    }

    /**
     * Enum of Milk Types
     */
    enum MilkTypes {
        RegularMilk, SoyMilk, AlmondMik, RiceMilk, NoMilk
    }

    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /* the name of the current costumer, we use this name in the transition screen*/
    String costumerName;

    /* array list representing our shopping cart*/
    ArrayList<Coffee> shopCart = new ArrayList<>();

    /* the shopping cart icon notification badge*/
    NotificationBadge badge;

    /* the layout of the activity we open popup in*/
    ConstraintLayout layout;

    /* telling if we call the choose milk activity from the shopping cart*/
    boolean inShopCart = false;

    /* the current index we selected in the shop cart activity*/
    int currentIndex;

    /* creating the data singleton*/
    private static Data data = new Data();

    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/


    /**
     * private constructor as we do not want any instances of this class other then the 1 we made.
     */
    private Data() {
    }

    /**
     * @return the instance of our data
     */
    static Data getSingleton() {
        return data;
    }


    /**
     * get the coffee title by his type
     *
     * @param coffeeType the coffee type
     * @return the title fitting the type
     */
    static String getCoffeeName(CoffeeTypes coffeeType) {
        switch (coffeeType) {
            case SpaceUnicornCoffee:
                return SPACE_UNICORN_COFFEE;
            case MilkyWayCoffee:
                return MILKY_WAY_COFFEE;
            case NebulaCoffee:
                return NEBULA_COFFEE;
            case CelestialCoffee:
                return CELESTIAL_COFFEE;
        }
        return "";
    }

    /**
     * get the milk title by his type
     *
     * @param milkType the milk type
     * @return the title fitting the type
     */
    static String getMilkName(MilkTypes milkType) {
        switch (milkType) {
            case RegularMilk:
                return REGULAR_MILK;
            case SoyMilk:
                return SOY_MILK;
            case AlmondMik:
                return ALMOND_MILK;
            case RiceMilk:
                return RICE_MILK;
        }
        return NO_MILK;
    }

    /**
     * return the [rice of an item by his type
     *
     * @param coffeeType the coffee type
     * @return the price
     */
    static int getPriceByType(CoffeeTypes coffeeType) {
        switch (coffeeType) {
            case SpaceUnicornCoffee:
                return UNICORN_PRICE;
            case MilkyWayCoffee:
                return MILKYWAY_PRICE;
            case NebulaCoffee:
                return NEBULA_PRICE;
            case CelestialCoffee:
                return CELESTIAL_PRICE;
        }
        return 0;
    }

    /**
     * adds an item to our shopping cart, only 4 items allowed per user
     *
     * @param coffee the coffee item
     * @return ADD_SUCCESS if added successfully otherwise ADD_FAILURE
     */
    int addItem(Coffee coffee) {
        if (shopCart.size() < MAX_ITEM_AMOUNT) {
            shopCart.add(coffee);
            return ADD_SUCCESS;
        }
        return ADD_FAILURE;
    }

    /**
     * returns the image corresponding to the type
     *
     * @param coffeeType the coffee type
     * @return the image
     */
    int returnImageId(CoffeeTypes coffeeType) {
        switch (coffeeType) {
            case SpaceUnicornCoffee:
                return R.drawable.c2;
            case MilkyWayCoffee:
                return R.drawable.c1;
            case NebulaCoffee:
                return R.drawable.c3;
            case CelestialCoffee:
                return R.drawable.c4;
        }
        return 0;

    }

    /**
     * calculate the total price of the items in our shopping cart
     *
     * @return the total price
     */
    float getTotalPrice() {
        float sum = 0;
        for (int i = 0; i < shopCart.size(); i++) {
            sum += shopCart.get(i).getPrice();
        }
        return sum;
    }
}
