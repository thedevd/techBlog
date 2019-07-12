package com.thedevd.javaexamples.designpatterns.creational;

public class FactoryPatternExp2 {

	public static void main( String[] args )
	{
		KitchenFactory kitchen = new KitchenFactory();
		
		kitchen.prepare("pizza").prepareFood();
		kitchen.prepare("sandwitch").prepareFood();
		kitchen.prepare("maggie").prepareFood();
		kitchen.prepare("chickentikka").prepareFood();
		
		// @formatter:off
		// output--
		/*Preparing Pizza....
		Preparing Sandwitch....
		Preparing Maggie...
		Sorry sold out...*/
		// @formatter:on

	}
}

class KitchenFactory {

	Food food = null;

	public Food prepare( String reciepeName )
	{
		switch ( reciepeName.toLowerCase() )
		{
			case "pizza" :
				food = new Pizza();
				break;
			case "sandwitch" :
				food = new Sandwitch();
				break;
			case "maggie" :
				food = new Maggie();
				break;
			default :
				food = new SoldOutFood();
				break;
		}

		return food;
	}
}

interface Food {

	public abstract void prepareFood();
}

class Pizza implements Food {

	@Override
	public void prepareFood()
	{
		System.out.println("Preparing Pizza....");
		// recipe steps here
	}
}

class Sandwitch implements Food {

	@Override
	public void prepareFood()
	{
		System.out.println("Preparing Sandwitch....");
		// recipe steps here
	}
}

class Maggie implements Food {

	@Override
	public void prepareFood()
	{
		System.out.println("Preparing Maggie...");
		// recipe steps here
	}
}

class SoldOutFood implements Food {

	@Override
	public void prepareFood()
	{
		System.out.println("Sorry sold out...");
	}
}
