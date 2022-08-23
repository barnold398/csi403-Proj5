package proj5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Proj5 extends HttpServlet {

	// default versionID for serialization
	private static final long serialVersionUID = 1L;

	// Standard servlet method
	public void init() throws ServletException {
		// Do any required initialization here - likely none
	}

	// Standard Servlet method
	public void destroy() {
		// Do any required tear-down here, likely nothing.
	}

	// Standard servlet method - we will handle a POST operation
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (Exception e) {
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{ 'message' : 'Malformed JSON'}");
		}
	}

	// Our main worker method
	private void doService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type to be JSON
		response.setContentType("application/json");

		PrintWriter out = response.getWriter();

		// Get received JSON data from HTTP request
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonStr = "";

		// read JSON object into jsonStr
		if (br != null) {
			jsonStr = br.readLine();
		}

		// Create JsonReader object
		StringReader strReader = new StringReader(jsonStr);
		JsonReader reader = Json.createReader(strReader);

		// Get the singular JSON object (name:value pair) in this message.
		JsonObject obj = reader.readObject();
		
		// From the object get the array named "inList"
		JsonArray jsonArray = obj.getJsonArray("inList");
		
		// create a list of people and count the number of people
		List<String> peopleList = new ArrayList<String>();
		int numPeople = countPeople(jsonArray, peopleList);
		
		FriendGraph graph = new FriendGraph(numPeople);
		
		// make friendship edges between pairs of friends
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject friendObj = jsonArray.getJsonObject(i);
			JsonArray friendPair = friendObj.getJsonArray("friends");
			String friend1 = friendPair.getString(0);
			String friend2 = friendPair.getString(1);
			graph.addFriend(peopleList.indexOf(friend1), peopleList.indexOf(friend2));
		}
		
		String foaf = "";
		
		// check for friends of friends for each person
		// ignoring the last person since they'd already have been checked
		// for everybody else
		for (int i = 0; i < peopleList.size()-1; i++) {
			ArrayList<Integer> fofs = graph.friendsOfFriends(i);
			if (!fofs.isEmpty()) {
				for (Integer fof : fofs) {
					foaf += "[ \"" + peopleList.get(i) + "\" , \"" + peopleList.get(fof) + "\" ] , ";
				}
			}
		}
		

		// Send back the response JSON message
		out.println("{ \"outlist\" : [ " + foaf + " ] }");

		out.close();

	}
	
	// method to count the number of people to create properly sized edge array
	private int countPeople(JsonArray jsonArray, List<String> peopleList) {
		
		int numPeople = 0;
		
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject obj = jsonArray.getJsonObject(i);
			JsonArray friendPair = obj.getJsonArray("friends");
			String friend1 = friendPair.getString(0);
			if (!peopleList.contains(friend1)) {
				peopleList.add(friend1);
				numPeople++;
			}
			
			String friend2 = friendPair.getString(1);
			if (!peopleList.contains(friend2)) {
				peopleList.add(friend2);
				numPeople++;
			}
		}
		
		
		return numPeople;
	}
}
