package com.example.springcashier;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Random;

@Data
@RequiredArgsConstructor
class DrinkOrder {


    private String drink ;
    private String milk ;
    private String size ;
    private double total;
    private String register ;
    private String status ;

    public static String selectDrink(){
        String[] drinks = {"Caffe Latte", "Caffe Americano", "Caffe Mocha", "Espresso", "Cappuccino"};

        //return random drink
        Random rand = new Random();
        return drinks[rand.nextInt(drinks.length)];

    }

    public static String selectMilk(){
        String[] milks = {"2% Milk", "Whole Milk", "Nonfat Milk", "Almond Milk", "Soy Milk"};

        //return random drink
        Random rand = new Random();
        return milks[rand.nextInt(milks.length)];
    }

    public static String selectSize(String drinkOrder){
        String[] esspressoSizes = {"Short", "Tall"};
        String[] nonEsspressoSizes = {"Tall","Grande","Venti"};

        //return random drink
        Random rand = new Random();
        if(drinkOrder.equals("Espresso")){
            return esspressoSizes[rand.nextInt(esspressoSizes.length)];
        }
        else{
            return nonEsspressoSizes[rand.nextInt(nonEsspressoSizes.length)];
        }
    }


    public static DrinkOrder GetNewOrder() {
     	DrinkOrder o = new DrinkOrder();
        o.drink = "" + DrinkOrder.selectDrink();
        o.milk = "" + DrinkOrder.selectMilk();
        o.size = "" + DrinkOrder.selectSize(o.drink);
        o.status = "Ready for Payment" ;

    	return o ;
    }


}

/*

https://priceqube.com/menu-prices/%E2%98%95-starbucks

var DRINK_OPTIONS = [ "Caffe Latte", "Caffe Americano", "Caffe Mocha", "Espresso", "Cappuccino" ];
var MILK_OPTIONS  = [ "Whole Milk", "2% Milk", "Nonfat Milk", "Almond Milk", "Soy Milk" ];
var SIZE_OPTIONS  = [ "Short", "Tall", "Grande", "Venti", "Your Own Cup" ];

Caffè Latte
=============
tall 	$2.95
grande 	$3.65
venti 	$3.95 (Your Own Cup)

Caffè Americano
===============
tall 	$2.25
grande 	$2.65
venti 	$2.95 (Your Own Cup)

Caffè Mocha
=============
tall 	$3.45
grande 	$4.15
venti 	$4.45 (Your Own Cup)

Cappuccino
==========
tall 	$2.95
grande 	$3.65
venti 	$3.95 (Your Own Cup)

Espresso
========
short 	$1.75
tall 	$1.95

 */



