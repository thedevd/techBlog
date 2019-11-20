package com.thedevd.javaexamples.multithreading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/*
 * java.util.concurrent.BlockingQueue has following implementations-
 * 1. ArrayBlockingQueue
 * 2. LinkedBlockingQueue
 * 3. PriorityBlockingQueue
 * 4. DelayQueue
 * 5. SynchronousQueue
 * 
 * In this, we will explain PriorityBlockingQueue.
 * 
 * What is PriorityBlockingQueue
 * ################################
 * 1. PriorityBlockingQueue is a type of unbounded BlockingQueue, which orders the elements based on their
 * priority, not on FIFO.
 * 2. To check the priority of an item, the PriorityBlockingQueue looks for java.lang.Comparable implementation, 
 * it means all items being inserted in queue must implement java.lang.Comparable Interface.
 * 3. It is to be noted that in case you obtain an Iterator from a PriorityBlockingQueue, the Iterator does not guarantee 
 * to iterate the elements in priority order.
 * 
 * Real world example would be giving priority to emergency patient first at hospital.
 * Hospital serves normal and emergency case. If any emergency patient come, he will be 
 * given a priority and treated first.
 * 
 */
public class PriorityBlockingQueueDemo {

	public static void main( String[] args ) throws InterruptedException
	{
		BlockingQueue<Patient> priorityBlockingQueue = new PriorityBlockingQueue<>();
		
		priorityBlockingQueue.put(new Patient(1, "Dev", PatientType.NORMAL));
		priorityBlockingQueue.put(new Patient(2, "ravi", PatientType.NORMAL));
		priorityBlockingQueue.put(new Patient(3, "ramesh", PatientType.EMERGENCY));
		priorityBlockingQueue.put(new Patient(4, "reema", PatientType.NORMAL));
		priorityBlockingQueue.put(new Patient(5, "monika", PatientType.EMERGENCY));
		
		Patient patient = priorityBlockingQueue.poll();
		while(patient!=null) {
			System.out.println(patient);
			patient = priorityBlockingQueue.poll();
		}
		
		/*
		 * output
		 * ##########
		 * 
		 * Patient [patientId=3, patientName=ramesh, patientType=EMERGENCY]
		 * Patient [patientId=5, patientName=monika, patientType=EMERGENCY]
		 * Patient [patientId=1, patientName=Dev, patientType=NORMAL]
		 * Patient [patientId=2, patientName=ravi, patientType=NORMAL]
		 * Patient [patientId=4, patientName=reema, patientType=NORMAL]
		 * 
		 * So you can see EMERGENCY patients are taken first, then NORMAL patient will be served.
		 */
		
	}
}

class Patient implements Comparable<Patient> {
	
	private int patientId;
	private String patientName;
	private PatientType patientType;
	
	public Patient( int patientId, String patientName, PatientType patientType )
	{
		super();
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientType = patientType;
	}

	@Override
	public int compareTo( Patient o )
	{
		// 1 bigger
		//-1 smaller
		if (this.patientType.getValue() > o.patientType.getValue()) {
			return -1;
		}
		else if(this.patientType.getValue() < o.patientType.getValue()) {
			return 1;
		}
		else { 
			// If both patient are NORMAL case then decide the order based on their id.
			// greater the id means the this.patient came after the o.patient
			if(this.patientId > o.patientId) return 1;
			else if (this.patientId < o.patientId) return -1; // smaller id means came first.
			else return 0; 
		}
	}

	@Override
	public String toString()
	{
		return "Patient [patientId=" + patientId + ", patientName=" + patientName + ", patientType=" + patientType
				+ "]";
	}
	
}

enum PatientType {
	    NORMAL(1),
		EMERGENCY(2);

	private int value;

	private PatientType( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
}

	
