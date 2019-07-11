package com.thedevd.javaexamples.basic;

public class ShallowAndDeepCloning {

	public static void main( String[] args ) throws CloneNotSupportedException
	{
		// In this will will try shallow and deep copy of
		// Employee object

		Address addOfE1 = new Address(803, "Antariksh", "Noida", "U.P.", 123456);
		Employee e1 = new Employee(1, "thedevd", addOfE1);

		System.out.println("this is shalow copy, means changing the address using e2 will also reflect in e1");
		Employee e2 = e1.clone();
		e2.address.flatNo = 103;
		e2.address.streetOrSocietyName = "Amrapali";

		System.out.println("e1----------------");
		System.out.println(e1);

		System.out.println("e2----------------");
		System.out.println(e2);

		System.out.println("\nthis is deep copy, means changing the address using e3 will not reflect in e4");
		Address addOfE3 = new Address(803, "Antariksh", "Noida", "U.P.", 123456);
		Employee2 e3 = new Employee2(1, "thedevd", addOfE3);
		Employee2 e4 = e3.clone();
		e4.address.flatNo = 103;
		e4.address.streetOrSocietyName = "Amrapali";

		System.out.println("e3----------------");
		System.out.println(e3);

		System.out.println("e4----------------");
		System.out.println(e4);

		// @formatter:off
 
		/*this is shalow copy, means changing the address using e2 will also reflect in e1
		e1----------------
		Employee [id=1, name=thedevd, address=Address [flatNo=103, streetOrSocietyName=Amrapali, city=Noida, state=U.P., pincode=123456, salary=10000.0]]
		e2----------------
		Employee [id=1, name=thedevd, address=Address [flatNo=103, streetOrSocietyName=Amrapali, city=Noida, state=U.P., pincode=123456, salary=10000.0]]

		this is deep copy, means changing the address using e3 will not reflect in e4
		e3----------------
		Employee2 [id=1, name=thedevd, address=Address [flatNo=803, streetOrSocietyName=Antariksh, city=Noida, state=U.P., pincode=123456]]
		e4----------------
		Employee2 [id=1, name=thedevd, address=Address [flatNo=103, streetOrSocietyName=Amrapali, city=Noida, state=U.P., pincode=123456]]*/
		// @formatter:on

	}
}

// Clonable is Marker interface
class Employee2 implements Cloneable {

	int id;
	String name;
	Address address;

	public Employee2( int id, String name, Address address )
	{
		this.id = id;
		this.name = name;
		this.address = address;
	}

	// shalow copy
	public Employee2 clone() throws CloneNotSupportedException
	{
		Employee2 clonedEmp = (Employee2) super.clone();
		clonedEmp.address = new Address(address.flatNo, address.streetOrSocietyName, address.city, address.state,
				address.pincode);
		return clonedEmp;
	}

	@Override
	public String toString()
	{
		return "Employee2 [id=" + id + ", name=" + name + ", address=" + address + "]";
	}

}

class Employee implements Cloneable {

	int id;
	String name;
	Address address;
	private double salary = 10000; // even private members gets cloned

	public Employee( int id, String name, Address address )
	{
		this.id = id;
		this.name = name;
		this.address = address;
	}

	// shalow copy
	public Employee clone() throws CloneNotSupportedException
	{
		return (Employee) super.clone();
	}

	@Override
	public String toString()
	{
		return "Employee [id=" + id + ", name=" + name + ", address=" + address + ", salary=" + salary + "]";
	}

}

class Address {

	int flatNo;
	String streetOrSocietyName;
	String city;
	String state;
	int pincode;

	public Address( int flatNo, String streetOrSocietyName, String city, String state, int pincode )
	{
		this.flatNo = flatNo;
		this.streetOrSocietyName = streetOrSocietyName;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
	}

	@Override
	public String toString()
	{
		return "Address [flatNo=" + flatNo + ", streetOrSocietyName=" + streetOrSocietyName + ", city=" + city
				+ ", state=" + state + ", pincode=" + pincode + "]";
	}

}
