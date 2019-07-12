package com.thedevd.javaexamples.designpatterns.creational;

public class FactoryPattern {

	public static void main( String[] args )
	{
		int x = 50, y = 5;
		CalculateFactory factory = new CalculateFactory();
		
		Calculate add = factory.getObject("add");
		add.calculate(x, y);
		
		Calculate substract = factory.getObject("substract");
		substract.calculate(x, y);
		
		Calculate multiply = factory.getObject("multiply");
		multiply.calculate(x, y);
		
		Calculate squareroot = factory.getObject("squareroot");
		squareroot.calculate(x, y);
	}
}


// ## Creation of object is done in separate class based on requirement
// given to factory
class CalculateFactory {

	public Calculate getObject( String type )
	{
		// If more new objects are to be created then only this place has to be modified
		Calculate calc = null;
		switch ( type.toLowerCase() )
		{
			case "add" :
				calc = new Add();
				break;

			case "substract" :
				calc = new Substract();
				break;
				
			case "multiply" :
				calc = new Multiply();
				break;

			default :
				calc = new NotSupportedCalculation();
				break;
		}
		return calc;
	}
}


// ## Thumb rule of FactoryPatter is Program to an interface
interface Calculate {

	public abstract void calculate( int x, int y );
}

class Add implements Calculate {

	public void calculate( int x, int y )
	{
		System.out.println("Addition of x:" + x + " and y:" + y + " is " + (x + y));
	}
}

class Substract implements Calculate {

	public void calculate( int x, int y )
	{
		System.out.println("Substraction of x:" + x + " and y:" + y + " is " + (x - y));
	}
}

class Multiply implements Calculate {

	public void calculate( int x, int y )
	{
		System.out.println("Multiplication of x:" + x + " and y:" + y + " is " + (x * y));
	}
}

class NotSupportedCalculation implements Calculate {
	
	public void calculate( int x, int y )
	{
		System.out.println("We dont support this");
	}
}