package proj5;

import java.util.ArrayList;

public class FriendGraph {
	
	// class members
	private int numPeople;
	private boolean[][] edgeList;
	
	// constructor for FriendGraph with numFriends
	public FriendGraph(int numPeople) {
		this.numPeople = numPeople;
		edgeList = new boolean[numPeople][numPeople];
	
		// initialize edgeList to all 0s
		for (int i = 0; i < numPeople; i++) {
			for (int j = 0; j < numPeople; j++) {
				edgeList[i][j] = false;
			}
		}
		
		// make each person friends with themselves
		for (int i = 0; i < numPeople; i++) {
			edgeList[i][i] = true;
		}
		
	}
	
	// method to add edges between friends
	void addFriend(int friend1, int friend2) {
		edgeList[friend1][friend2] = true;
		edgeList[friend2][friend1] = true;
	}
	
	/*
	 * friendsOfFriends method
	 * Given a person number, this method checks for any people who
	 * they aren't friends with yet and have at least one mutual friend
	 * returns ArrayList of the friends of friends
	 */
	ArrayList<Integer> friendsOfFriends(int friend1) {
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int i = 0; i < numPeople; i++) {
			// if they're already friends, do nothing
			if (edgeList[friend1][i]) {}
			// else check if they have at least one friend in common
			else {
				boolean found = false;
				for (int j = 0; !found && j < numPeople; j++) {
					if (edgeList[friend1][j]) {
						if (edgeList[j][i]) {
							addFriend(friend1, i);
							result.add(i);
							found = true;
						}
					}
				}
			}
		}
		
		return result;
	}
}
