package com.thedevd.javaexamples.algorithms;

public class ReverseSingleLinkedList {

	public static void main( String[] args )
	{
		LinkedList list = new LinkedList();
		list.insert(10);
		list.insert(20);
		list.insert(30);
		list.insert(5);

		list.print();
		//10 ----> 20 ----> 30 ----> 5 ----> null
		list.reverseList();
		list.print();
		//5 ----> 30 ----> 20 ----> 10 ----> null
	}

}

class LinkedList {

	Node head = null;
	int lenght;

	public void insert( int data )
	{
		Node newNode = new Node(data);
		newNode.nextNode = null;

		// check if list is empty
		if( head == null )
		{
			head = newNode;
			lenght = 1;
		}
		else
		{
			// Find the lastNodeInList
			Node lastNodeInList = head;
			while( lastNodeInList.nextNode != null )
			{
				lastNodeInList = lastNodeInList.nextNode;
			}

			lastNodeInList.nextNode = newNode;
			lenght++;
		}
	}

	public void print()
	{
		if( lenght == 0 )
		{
			System.out.println("list is empty");
		}
		else
		{
			Node currentNode = head;
			while( currentNode != null )
			{
				System.out.print(currentNode.data + " ----> ");
				currentNode = currentNode.nextNode;
			}
			System.out.println("null");
		}
	}

	public void reverseList()
	{
		Node current = head;
		Node next = null;
		Node previous = null;

		while( current != null )
		{
			// First save the current nextNode to next as we will be breaking current link
			next = current.nextNode;

			// Break the existing links of currentNode and establish link to previous node in the list
			current.nextNode = previous;

			// move the previous to current and current to next
			previous = current;
			current = next;
		}
		// dont forget to update the head to previous.
		// at the last step previous node will point to last node of the input list
		head = previous;
	}
}

class Node {

	int data;
	Node nextNode;

	public Node( int data )
	{
		this.data = data;
	}
}
