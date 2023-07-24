package transit;

//import java.beans.DesignMode;
import java.util.ArrayList;

//import javax.print.attribute.standard.Destination;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}


	/*
	 * helper method for makeList().
	 * This method creates a linked list from the array input given.
	 * 
	 * @param a: Int array that you want to convert to a linked list
	 * @param head: The starting node of the list you want to create from the array
	 */
	private void arrayToLinkedList(int[] a, TNode head){
		TNode currNode = head;		//creating a "pointer" node used for iteration
		for (int i = 0; i < a.length; i++) {
			TNode newTrainNode = new TNode(a[i]);		//creating new node to store the location data of each index of the array
			currNode.setNext(newTrainNode);				//sets the currNode pointer to the node created
			currNode = currNode.getNext();				//moving the pointer node to the next node in the list
		}
	}


	/*
	 * helper method for makeList().
	 * This method basically finds each of the shared stops between 2 linked lists
	 * and sets the down pointer to the respective node.
	 * 
	 * @param upper: this is the upper layer of the list. (ex. train) or (ex. bus)
	 * @param lower: this is the lower layer of the list. (ex. bus)	  or (ex. walk)
	*/
	private void joinListsDown(TNode upper, TNode lower){
		TNode currUpper = upper;
		while (currUpper != null) {
			TNode currLower = lower;
			while (currLower != null) {
				if (currUpper.getLocation() == currLower.getLocation()) {
					currUpper.setDown(currLower);
				}
				currLower = currLower.getNext();
			}
			currUpper = currUpper.getNext();
		}
	}

	private TNode deepCopy(TNode head){
		if(head == null){
			return null;
		}
		
		TNode copy = new TNode(head.getLocation());
		copy.setNext(deepCopy(head.getNext()));

		return copy;
	}


	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
	    // UPDATE THIS METHOD
		trainZero = new TNode();		//setting train head location = 0
		TNode busZero = new TNode();
		TNode walkZero = new TNode();

		arrayToLinkedList(trainStations, trainZero);
		arrayToLinkedList(busStops, busZero);
		arrayToLinkedList(locations, walkZero);
		joinListsDown(busZero, walkZero);
		joinListsDown(trainZero, busZero);

	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	    // UPDATE THIS METHOD
		TNode curr = trainZero;
		while(curr != null && curr.getNext().getLocation() != station){
			curr = curr.getNext();
		}

		if(curr.getNext() != null){
			curr.setNext(curr.getNext().getNext());
		} //else curr.setNext(null);
		
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	    // UPDATE THIS METHOD
		TNode newBusStop = new TNode(busStop);
		TNode prev = trainZero.getDown();
		TNode next = prev.getNext();
		TNode walk = prev.getDown();

		while(walk != null){						//making sure the walk layer has a location to add busStop to
			if(walk.getLocation() == busStop){
				break;
			} else{
				walk = walk.getNext();
			}
		}
		//walk = prev.getDown();						//setting walk back to the begging for later use in this method
		//check if you need to reset walk node or not  I think you dont

		while(next != null && next.getLocation() < busStop){		//loops through list to find where we can insert busStop
			prev = prev.getNext();
			next = next.getNext();

		}

		if (next != null && next.getLocation() == busStop) {		//do nothing if the stop already exists.
			return;
		}

		prev.setNext(newBusStop);
		if(next != null){							//these 4 lines of code just reassigns the node references with respect to busStop
			newBusStop.setNext(next);
		}

		// while(walk != null && walk.getLocation() != busStop){	//linking the bus stop to the the walking stop
		// 	walk = walk.getNext();
		// }
		newBusStop.setDown(walk);

	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {
	    // UPDATE THIS METHOD
		ArrayList<TNode> daWay = new ArrayList<TNode>();
		TNode prevTrain = trainZero;
		TNode nextTrain = prevTrain.getNext();
		TNode nextBus;
		TNode nextWalk;

		daWay.add(prevTrain);

		while(nextTrain != null && nextTrain.getLocation() < destination){
			daWay.add(nextTrain);
			prevTrain = prevTrain.getNext();
			nextTrain = nextTrain.getNext();
		}

		if(nextTrain != null && nextTrain.getLocation() == destination){
			daWay.add(nextTrain);
			daWay.add(nextTrain.getDown());
			daWay.add(nextTrain.getDown().getDown());
		} else{
			prevTrain = prevTrain.getDown();
			daWay.add(prevTrain);
		}
		nextBus = prevTrain.getNext();

		while(nextBus != null && nextBus.getLocation() < destination){
			daWay.add(nextBus);
			prevTrain = prevTrain.getNext();
			nextBus = nextBus.getNext();
		}

		if(nextBus != null && nextBus.getLocation() == destination){
			daWay.add(nextBus);
			daWay.add(nextBus.getDown());
		} else{
			prevTrain = prevTrain.getDown();
			daWay.add(prevTrain);
		}

		nextWalk = prevTrain.getNext();

		while(nextWalk != null && nextWalk.getLocation() <= destination){
			daWay.add(nextWalk);
			nextWalk = nextWalk.getNext();
		}


	    return daWay;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {
	    // UPDATE THIS METHOD
		TNode head = trainZero;
		TNode trainLayer = deepCopy(head);
		TNode busLayer = deepCopy(head.getDown());
		TNode walkLayer = deepCopy(head.getDown().getDown());

		joinListsDown(busLayer, walkLayer);
		joinListsDown(trainLayer, busLayer);


		return trainLayer;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {
	    // UPDATE THIS METHOD
		TNode scooterHead = new TNode();
		TNode busHead = trainZero.getDown();
		TNode walkHead = busHead.getDown();
		arrayToLinkedList(scooterStops, scooterHead);
		joinListsDown(scooterHead, walkHead);
		joinListsDown(busHead, scooterHead);
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
