package com.thedevd.javaexamples.designpatterns.creational;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* Ensures only one instance can be created of a class.
 * 
 * Realworld example - There can only be one president of a country at a time. President here is
 * singleton
 * 
 * Note- This pattern is actually considered an anti-pattern and overuse of it should be avoided
 * because change to it in one place could affect in the other areas and it could become pretty
 * difficult to debug. The other bad thing about them is it makes your code tightly coupled plus
 * mocking the singleton could be difficult. */

public class SingletonPattern {

	public static void main( String[] args ) throws CloneNotSupportedException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		President president1  = President.getInstance();
		President president2  = President.getInstance();
		
		System.out.println(president1.hashCode());
		System.out.println(president2.hashCode());
		
		President president3 = president1.clone();
		System.out.println("After cloning.....");
		System.out.println(president1.hashCode());
		System.out.println(president3.hashCode());
		
		System.out.println("Trying to use reflection...");
		Constructor<?>[] constructors =  
				President.class.getDeclaredConstructors(); 
        for (Constructor<?> constructor : constructors)  
        { 
            constructor.setAccessible(true); 
            President president4 = (President) constructor.newInstance(); // this throws exception
            System.out.println(president4.hashCode());
            break; 
        } 
	}
}

// @formatter:off
/*
 * How to make singleton in java programmatically-
 * 1. Make the constructor private.
 * 2. Disable extension by making class final 
 * 3. Create a static variable to store the single copy of instance in jvm. 
 * 4. Disable Reflection- throw instantiation exception from constructor so even if 
 * constructor is made public using reflection it will throw error.
 * 5. Disable DeSerialization -  implement method readResolve() method and return same instance.
 * 6. Disable cloning - throw CloneNotSupported exception from clone method. 
 */
// @formatter:on

@SuppressWarnings( "serial" )
final class President implements Cloneable, Serializable {

	private static President INSTANCE;

	private President()
	{
		// to prevent instantiating by Reflection call
		if( President.INSTANCE != null )
		{
			throw new InstantiationError("Creating of this object is not allowed");
		}
	}

	public static President getInstance()
	{
		if( INSTANCE == null )
		{
			// It is not initialized but we cannot be sure because some other thread might have initialized it
			// in the meanwhile. So to make sure we need to lock on an object to get mutual exclusion.
			synchronized( President.class )
			{
				if(INSTANCE == null )
				{
					 // The instance is still not initialized so we can safely (no other thread can enter this zone)
			         // create an instance and make it our singleton instance.
					INSTANCE = new President();
				}
			}
		}
		return INSTANCE;
	}

	@Override
	protected President clone() throws CloneNotSupportedException
	{
		return INSTANCE; 
		// or throw new CloneNotSupportedException("Cloning of this object is not allowed");
	}
	
	// To prevent DeSerialization
    protected Object readResolve() 
    { 
        return INSTANCE; 
    } 
    
}
