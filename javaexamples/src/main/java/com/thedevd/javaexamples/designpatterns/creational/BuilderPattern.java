package com.thedevd.javaexamples.designpatterns.creational;

// @formatter:off
/*
 * Allows you to create different flavors of an object thus avoiding multiple constructor creation. 
 * Useful when there could be several flavors of an object. 
 * Or when there are a lot of steps involved in creation of an object. 
 * 
 * For example you want a customized Subway burger, you have several options in how your 
 * burger is made 
 * e.g what bread do you want? 
 * what types of sauces would you like? 
 * What cheese would you want? etc. 
 * In such cases builder pattern comes to the rescue.
*/
// @formatter:on

public class BuilderPattern {

	public static void main( String[] args )
	{
		// Using Burger builder here to create burger

		// Dev says I like extra-cheese burger specially onion with tamoto
		Burger burger1 = new Burger(new BurgerBuilder(7).addExtraCheese().addOnion().addTamoto());
		System.out.println("Dev's order: " + burger1);

		// Ravi says I dont want extracheese and same as Dev wants
		Burger burger2 = new Burger(new BurgerBuilder(7).addOnion().addTamoto());
		System.out.println("Ravi's order: " + burger2);
	}
}

class Burger {

	int size;
	boolean extraCheese;
	boolean pepperoni;
	boolean lettuce;
	boolean tamoto;
	boolean onion;

	public Burger( BurgerBuilder builder )
	{
		this.size = builder.size;
		this.extraCheese = builder.extraCheese;
		this.pepperoni = builder.pepperoni;
		this.lettuce = builder.lettuce;
		this.tamoto = builder.tamoto;
		this.onion = builder.onion;
	}

	@Override
	public String toString()
	{
		return "Burger [size=" + size + ", extraCheese=" + extraCheese + ", pepperoni=" + pepperoni + ", lettuce="
				+ lettuce + ", tamoto=" + tamoto + ", onion=" + onion + "]";
	}

}

// This is Builder pattern where we are adding multiple flavors to object and returning the obj
class BurgerBuilder {

	int size;
	boolean extraCheese;
	boolean pepperoni;
	boolean lettuce;
	boolean tamoto;
	boolean onion;

	public BurgerBuilder( int size )
	{
		this.size = size;
	}

	public BurgerBuilder addExtraCheese()
	{
		this.extraCheese = true;
		return this;
	}

	public BurgerBuilder addPepperoni()
	{
		this.pepperoni = true;
		return this;
	}

	public BurgerBuilder addLettuce()
	{
		this.lettuce = true;
		return this;
	}

	public BurgerBuilder addTamoto()
	{
		this.tamoto = true;
		return this;
	}

	public BurgerBuilder addOnion()
	{
		this.onion = true;
		return this;
	}

}
