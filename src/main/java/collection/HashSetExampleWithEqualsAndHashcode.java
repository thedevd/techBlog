import java.util.*;

class Employee {

	int id;
	String name;

	public Employee( int id, String name )
	{
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return this.id + ":" + this.name;
	}

	@Override
	public boolean equals( Object obj )
	{
		Employee that = (Employee) obj;
		System.out.println("Equals called for: " + this);
		return Objects.equals(this.id, that.id) && Objects.equals(this.name.toLowerCase(), that.name.toLowerCase());
	}

	@Override
	public int hashCode()
	{
		System.out.println("Hashcode called for: " + this);
		return Objects.hash(id, name.toLowerCase());
	}
}

/* Each time when you add item to HashSet, internally it calls hashcode() method and if hashcode()
 * retunred matches with any exitent item then equals() gets called to check equality and if
 * equals() return true it means item is already there in hashset thus wont allow that duplicate
 * item. */
public class HashSetExampleWithEqualsAndHashcode {

	public static void main( String[] args )
	{
		Set<Employee> empSet = new HashSet<Employee>();
		empSet.add(new Employee(10, "Dev"));
		empSet.add(new Employee(10, "DEv"));
		System.out.println(empSet); // it outpurts only [10:Dev]. This is because we overriden hashcode and equals() method.

		Employee search = new Employee(10, "Dev");
		System.out.println(empSet.contains(search)); //true. You wont be able to achieve this until you override hashcode() and equals()
	}
}
