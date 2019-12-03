package com.thedevd.javaexamples.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * Java8 Stream API provides very straight-forward api called max() or min() which are used
 * to find max or min from collection of date, number, Char, String or object. 
 * 
 * In this we will see two approaches which are -
 * 1. using Comparator.comparing().
 * 2. using lambda expression
 * 
 * In case if two objects are equally max or min, then first match will be picked, i.e.
 *    empList.add(new Employee(100, "ZZZ", 50)); // most older
 *    empList.add(new Employee(50, "HHH", 40));
 *    empList.add(new Employee(10, "AAA", 50)); // most older
 *    
 *   Here is tie with two objects, so Employee(100, "ZZZ", 50) will be picked
 */
public class Java8FindingMaxMin {

	public static void main( String[] args )
	{
		List<Employee> empList = new ArrayList<Employee>();
		
		empList.add(new Employee(100, "ZZZ", 30));
		empList.add(new Employee(50, "HHH", 40));
		empList.add(new Employee(10, "AAA", 50)); // most older
		empList.add(new Employee(70, "BBB", 20)); // most elder
		
		/*
		 * find older employee in the list using Comparator.comparing and lamba expression
		 */
		Employee olderEmp = empList.stream()
				.max(Comparator.comparing(Employee :: getAge))
				.get(); // get() is used because max return type is of Optional<T>
		System.out.println("Older employee using Comparator.comparing(): " + olderEmp);
		
		Employee olderEmp_1 = empList.stream()
				.max((emp1, emp2) -> emp1.getAge() - emp2.getAge())
				.get();
		System.out.println("Older employee using lamda expression: " + olderEmp_1);
		
		/*
		 * find elder employee in the list using Comparator.comparing and lamba expression
		 */
		Employee elderEmp = empList.stream()
				.min(Comparator.comparing(Employee:: getAge))
				.get(); // get() is used because min return type is of Optional<T>
		System.out.println("Elder employee using Comparator.comparing(): " + elderEmp);
		
		Employee elderEmp_1 = empList.stream()
				.min((emp1, emp2) -> emp1.getAge() - emp2.getAge())
				.get();
		System.out.println("Elder employee using lamda expression: " + elderEmp_1);
		
		
	}
}

class Employee {

	private int id;
	private String name;
	private int age;

	public Employee( int id, String name, int age )
	{
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge( int age )
	{
		this.age = age;
	}

	@Override
	public String toString()
	{
		return "Employee [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
}
