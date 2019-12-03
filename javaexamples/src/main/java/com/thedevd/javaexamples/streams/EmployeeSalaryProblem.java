package com.thedevd.javaexamples.streams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * We have been given collection of employees and we want to answer these questions-
 * 1. Employee with highest salary.
 * 2. Employee with lowest salary.
 * 3. Employee with second highest salary.
 * 4. Dept wise highest salaried employee
 */
public class EmployeeSalaryProblem {

	public static void main( String[] args )
	{
		List<Employee> empList = new ArrayList<>();
		empList.add(new Employee(100, "AAA", 50000, "IT")); // second highest salary
		empList.add(new Employee(200, "ZZZ", 25000, "ADMIN"));
		empList.add(new Employee(300, "HHH", 20000, "HR")); // second lowest salary
		empList.add(new Employee(400, "BBB", 50050, "IT")); // highest salary
		empList.add(new Employee(500, "CCC", 40000, "IT"));
		empList.add(new Employee(600, "DDD", 26000, "ADMIN"));
		empList.add(new Employee(700, "EEE", 15000, "ADMIN")); // lowest salary
		empList.add(new Employee(800, "FFF", 40000, "HR"));
		empList.add(new Employee(900, "GGG", 21000, "HR")); 
		
		/*
		 * Highest salaried employee
		 */
		Employee highestPaidEmp = empList.stream()
				.max(Comparator.comparing(Employee :: getSalary))
				.get();
		System.out.println("Higest paid employee: " + highestPaidEmp);
		// --> Higest paid employee: Employee [id=400, name=BBB, salary=50050.0, deptName=IT]
		
		/*
		 * Lowest salaried employee
		 */
		Employee lowestPaidEmp = empList.stream()
				.min(Comparator.comparing(Employee :: getSalary))
				.get();
		System.out.println("Lowest paid employee: " + lowestPaidEmp);
		// --> Lowest paid employee: Employee [id=700, name=EEE, salary=15000.0, deptName=ADMIN]
		
		/*
		 * Second highest salary. This needs these steps-
		 * 1. Sort the employee in descending order of salary
		 * 2. Take first two result.
		 * 3. Skip the first one. and take the last one
		 * 
		 */
		Employee secondHighestPaidEmp = empList.stream()
				.sorted(Collections.reverseOrder(Comparator.comparing(Employee :: getSalary)))
				.limit(2)
				.skip(1)
				.findFirst()
				.get();
		System.out.println("Second highest paid employee: " + secondHighestPaidEmp);
		// --> Second highest paid employee: Employee [id=100, name=AAA, salary=50000.0, deptName=IT]
		
		/*
		 * Highest salaried employee by deptName
		 */
		Map<String, Optional<Employee>> highestPaidEmpByDeptName = empList.stream()
				.collect(
						Collectors.groupingBy(Employee::getDeptName,
						Collectors.maxBy(Comparator.comparing(Employee::getSalary))
						) // groupingBy deptName with finding maxBy salary
					); // whenever we use groupBy means return type would be of  Map type
		System.out.println("highestPaidEmpByDeptName");
		highestPaidEmpByDeptName.entrySet()
			.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue().get()));
		/*
		 * highestPaidEmpByDeptName
		 * HR: Employee [id=800, name=FFF, salary=40000.0, deptName=HR]
		 * ADMIN: Employee [id=600, name=DDD, salary=26000.0, deptName=ADMIN]
		 * IT: Employee [id=400, name=BBB, salary=50050.0, deptName=IT]
		 * 
		 */

	}

	static class Employee { // taken inner class for demo only

		private int id;
		private String name;
		private double salary;
		private String deptName;

		public Employee( int id, String name, double salary, String deptName )
		{
			super();
			this.id = id;
			this.name = name;
			this.salary = salary;
			this.deptName = deptName;
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

		public double getSalary()
		{
			return salary;
		}

		public void setSalary( double salary )
		{
			this.salary = salary;
		}

		public String getDeptName()
		{
			return deptName;
		}

		public void setDeptName( String deptName )
		{
			this.deptName = deptName;
		}

		@Override
		public String toString()
		{
			return "Employee [id=" + id + ", name=" + name + ", salary=" + salary + ", deptName=" + deptName + "]";
		}

	}
}
