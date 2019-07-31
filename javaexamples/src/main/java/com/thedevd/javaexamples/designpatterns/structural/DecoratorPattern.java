package com.thedevd.javaexamples.designpatterns.structural;

// @formatter:off
 
/* Decorator is one of the most widely used structural pattern
 * which provides a dynamic way of extending an object's functionality. 
 * 
 * It's different than the traditional way of adding new functionality into an object 
 * using Inheritance, instead, it uses Composition which makes it flexible and 
 * allows to add new functionalities at the run time, as opposite to Inheritance, 
 * which adds new functionality at compile time. 
 * 
 * Because of this flexibility, Decorator is one of the darling patterns 
 * for many Java developer.
 * 
 * Realworld Example-
 * Calculation cost of Pizza. 
 * Since the customer can customize a Pizza by asking extra cheese or extra topings, 
 * you also need to include the cost of those items in the final price of Pizza.
 * 
 * Since this customization can vary a lot among different customers and offer from a shop, 
 * creating classes for different types of Pizza with different topings or extras 
 * like ThinCrustPizzaWithCheese or ThinCrustPizzaWithExtraCheeseAndOlives will just 
 * clutter the code with lots of endless small classes. 
 * 
 * Now this problem looks a natural fit for applying Decorator pattern because 
 * we have a base object Pizza already created, which can be decorated with extra cheese 
 * and topping. 
 * By using the Decorator pattern, you can extend the functionality of Pizza class at runtime, 
 * based upon customer's request, which is impossible with Inheritance until you have a 
 * specific class for every possible customer request. 
 * 
 * This is also one of the reasons why Composition is preferred over Inheritance in Object-oriented design and particularly in Java. 
 * 
 * You may be wondering using the builder pattern to this as it seems similar case.
 * Builder is creational pattern which only adds functionality to object at the time of creation,
 * but decorator can change object behavior at run time post object construction. 
 * 
 * Although we can use Builder here along with decorator, i.e. 
 * Pizza is created using builder with basic optional ingredients initially ( Bread, cheese, extra cheese etc). 
 * Once the Base Pizza is constructed, we can later go and decorate it with different toppings 
 * like tomato sauce, olives, etc...
 * So here Decorator is useful for adding special feature at run-time for already created object.
 * 
 */
//@formatter:on

public class DecoratorPattern {

	public static void main( String[] args )
	{
		// Base object
		Pizza myPizza = new SimplePizza();
		
		// Now decorate pizza with extracheese and olives
		myPizza = new ExtraCheesePizzaDecorator(myPizza);
		myPizza = new OlivesToppingPizzaDecorator(myPizza);
		
		System.out.println(myPizza.getDescription());
		System.out.print(myPizza.getPrice());
		
	}
}

interface Pizza {
	
	public double getPrice();
	public String getDescription();
}

class SimplePizza implements Pizza {
	
	@Override
	public double getPrice()
	{
		return 199;
	}

	@Override
	public String getDescription()
	{
		return "Simple Pizza";
	}
}


// Decorators
interface PizzaDecorator extends Pizza {
	
}

// Decorator 1
class ExtraCheesePizzaDecorator implements PizzaDecorator {

	Pizza basePizza;
	
	public ExtraCheesePizzaDecorator(Pizza basePizza) {
		this.basePizza = basePizza;
	}
	
	@Override
	public double getPrice()
	{
		// Extending functionality to existing created object
		return basePizza.getPrice() + 50;
	}

	@Override
	public String getDescription()
	{
		return basePizza.getDescription() + ", with ExtraCheese";
	}
}


//Decorator 2
class OlivesToppingPizzaDecorator implements PizzaDecorator {

	Pizza basePizza;
	
	public OlivesToppingPizzaDecorator(Pizza basePizza) {
		this.basePizza = basePizza;
	}
	
	@Override
	public double getPrice()
	{
		return basePizza.getPrice() + 30;
	}

	@Override
	public String getDescription()
	{
		return basePizza.getDescription() + ", with Olives";
	}
}

