package basic;

class A
{
	static int i = 100;
	//static block is executed when class is loaded
	static
	{
		i = i-- - --i; // 100 - 98 = 2
		System.out.println("First " + i);
	}
	
	// Block section will executed when objects is instantiated
	{
		i = i++ + ++i; // 0 + 2 = 2
		System.out.println("Second " + i);
	}
}

class B extends A
{
	static
	{
		i = --i - i--; // 1 - 1 = 0
		System.out.println("Third " + i);
	}
	
	{
		i = ++i + i++; // 3 + 3 = 6
		System.out.println("Fourth " + i);
	}
}

public class IntanceBlockAndStaticBlock {

	public static void main( String[] args )
	{
		B b = new B();
		System.out.println("Main " + b.i);
		
		/* output
		First 2
		Third 0
		Second 2
		Fourth 6
		Main 6
		*/
	}
}
