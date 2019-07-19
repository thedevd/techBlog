package com.thedevd.javaexamples.designpatterns.structural;

/* Adaptor pattern is used to make existing classes work with others without modifying their source
 * code.
 * 
 * In plane words - it provides a way to make two different incompatible interface to work
 * together.
 * 
 * Realworld examples-
 * 
 * Consider that you have some pictures in your memory card and you need to transfer them to your
 * computer. In order to transfer them you need some kind of adapter that is compatible with your
 * computer ports so that you can attach memory card to your computer. In this case card reader is
 * an adapter.
 * 
 * Another example would be the famous power adapter; a three legged plug can't be connected to a
 * two pronged outlet, it needs to use a socket adapter that makes it compatible with the two
 * pronged outlet.
 * 
 * Yet another example would be a translator translating words spoken by one person to another */

public class AdaptorPattern {

	public static void main( String[] args )
	{
		USASocket usaThreePinSocket = new USAThreePinSocket();
		IndiaToUSASocketAdaptor indiaToUsaSocketAdaptor = new IndiaToUSASocketAdaptor(usaThreePinSocket);

		// Although below charge can take only IndiaSocket to charge, but we have an adaptor now 
		// to charge USA phone also.
		Charger charger = new Charger();
		charger.charge(indiaToUsaSocketAdaptor);

		// output-
		// Producing 120V.... from USA socket
	}
}

// Consider a India socket which produces 240V.
// First we have an interface IndiaSocket that all types of India sockets have to implement
interface IndiaSocket {

	public void get240Volt();
}

class IndiaTwoPinSocket implements IndiaSocket {

	@Override
	public void get240Volt()
	{
		System.out.println("Producing 240V....");
	}
}

// And Charger expects any implementation of IndiaSocket interface to charge phones.
class Charger {

	public void charge( IndiaSocket socket )
	{
		socket.get240Volt();
	}
}

// Now let's say USA guy came to India with his phone and he wants to charge the phone using 
// Indian Charger but he can not use India Charger directly 
// because India Charger takes type IndiaSocket which is a different interface. 
// To make it compatible for Charger, 
// we will have to create an adapter IndiaToUSASocketAdaptor that will make USASocket interface
// compatible with interface IndiaSocket.

interface USASocket {

	public void get120Volt();
}

class USAThreePinSocket implements USASocket {

	@Override
	public void get120Volt()
	{
		System.out.println("Producing 120V.... from USA socket");
	}
}

class IndiaToUSASocketAdaptor implements IndiaSocket {

	USASocket usaThreePinSocket;

	public IndiaToUSASocketAdaptor( USASocket usaThreePinSocket )
	{
		this.usaThreePinSocket = usaThreePinSocket;
	}

	@Override
	public void get240Volt()
	{
		this.usaThreePinSocket.get120Volt();
	}
}
