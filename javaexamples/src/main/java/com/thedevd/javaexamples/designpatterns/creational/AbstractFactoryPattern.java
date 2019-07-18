package com.thedevd.javaexamples.designpatterns.creational;

import com.thedevd.javaexamples.designpatterns.creational.DoorFactoryMaker.DoorType;

/* Both Abstract Factory and Factory design pattern are creational design pattern and used to
 * decouple the clients from creating objects they need. But there is a significant difference
 * between Factory and Abstract Factory design pattern, 
 * # Factory design pattern creates
 * implementation of Concrete classes 
 * # Abstract factory on other hand creates another factory which
 * inturn creates object. 
 * */

public class AbstractFactoryPattern {

	public static void main( String[] args )
	{
		System.out.println("#Wooden Door Factory#");
		DoorFactory woodenDoorFactory = DoorFactoryMaker.makeFactory(DoorType.WOODEN);
		woodenDoorFactory.makeDoor().getDescription();
		woodenDoorFactory.assingDoorFittingExpert().getDescription();
		
		System.out.println("#Iron Door Factory#");
		DoorFactory ironDoorFactory = DoorFactoryMaker.makeFactory(DoorType.IRON);
		ironDoorFactory.makeDoor().getDescription();;
		ironDoorFactory.assingDoorFittingExpert().getDescription();
		
		// @formatter:off
		/*
		 * output-
		 * #Wooden Door Factory#
			I am wooden door..
			I can only fit wooden door...
		   #Iron Door Factory#
			I am Iron door..
			I can only fit iron door...
		 */
		// @formatter:on

	}
}

/* Real world example Based on your needs you might get a wooden door from a wooden door shop, iron
 * door from an iron shop or a PVC door from the relevant shop. Plus you might need a guy with
 * different kind of specialities to fit the door, for example a carpenter for wooden door, welder
 * for iron door etc. As you can see there is a dependency between the doors now i.e. wooden door
 * needs carpenter, iron door needs a welder etc.
 * 
 * So here we can create DoorFactory as abstract factory that would let us make group of factories
 * Wooden Factory and Iron Factory which are then used to group related objects i.e. wooden door
 * factory would create a wooden door and wooden door fitting expert and iron door factory would
 * create an iron door and iron door fitting expert */

interface DoorFactory {

	public Door makeDoor();

	public DoorFittingExpert assingDoorFittingExpert();
}

class WoodenDoorFactory implements DoorFactory {

	@Override
	public Door makeDoor()
	{
		return new WoodenDoor();
	}

	@Override
	public DoorFittingExpert assingDoorFittingExpert()
	{
		return new Carpenter();
	}
}

class IronDoorFactory implements DoorFactory {

	@Override
	public Door makeDoor()
	{
		return new IronDoor();
	}

	@Override
	public DoorFittingExpert assingDoorFittingExpert()
	{
		return new Welder();
	}
}

interface Door {

	public void getDescription();
}

interface DoorFittingExpert {

	public void getDescription();
}

class WoodenDoor implements Door {

	@Override
	public void getDescription()
	{
		System.out.println("I am wooden door..");
	}
}

class Carpenter implements DoorFittingExpert {

	@Override
	public void getDescription()
	{
		System.out.println("I can only fit wooden door...");
	}
}

class IronDoor implements Door {

	@Override
	public void getDescription()
	{
		System.out.println("I am Iron door..");
	}
}

class Welder implements DoorFittingExpert {

	@Override
	public void getDescription()
	{
		System.out.println("I can only fit iron door...");
	}
}


/* Lets create a class DoorFactoryMaker which will be responsible for returning an instance of
 * either WoodenDoorFactory or IronDoorFactory. The client can use this DoorFactoryMaker to create
 * the desired concrete factory which, in turn, will produce different concrete objects (WoodenDoor,
 * IronDoor and thier respective fitting expert). In this example, we also used an enum to
 * parameterize which type of Door factory the client will ask for. */
class DoorFactoryMaker {

	public enum DoorType {
			WOODEN, IRON
	}

	public static DoorFactory makeFactory( DoorType type )
	{
		switch ( type )
		{
			case WOODEN :
				return new WoodenDoorFactory();
			case IRON :
				return new IronDoorFactory();
			default :
				throw new IllegalArgumentException("DoorType not supported.");
		}
	}
}
