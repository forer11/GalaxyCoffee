package com.example.galaxycoffie3;


/**
 * Class representing a Coffee object
 */
class Coffee {
    /* the price of the coffee*/
    private float price;
    /* enum representing the coffee type i.e. the drawing on top of it*/
    private Data.CoffeeTypes coffeeType;
    /* enum representing the milk the coffee is made of*/
    private Data.MilkTypes milkType;

    /**
     * Constructor
     *
     * @param coffeeType the coffee type
     * @param price      the price of the coffee we chose
     */
    Coffee(Data.CoffeeTypes coffeeType, int price) {
        this.coffeeType = coffeeType;
        this.price = price;
        this.milkType = Data.MilkTypes.NoMilk;
    }


    float getPrice() {
        return price;
    }

    Data.CoffeeTypes getCoffeeType() {
        return coffeeType;
    }

    Data.MilkTypes getMilkType() {
        return milkType;
    }

    void setMilkType(Data.MilkTypes milkType) {
        this.milkType = milkType;
    }
}