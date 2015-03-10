//sun & alex


package Model;

import java.util.*;

import javax.swing.JOptionPane;

public class User {
	
	private String userName;
	private String userPin;
	private boolean isAdmin;
	private Map<String, User> userList;
	
	
	/**
	 * Empty constructor to initialize the user class and
	 * populate the list of currently registered users.
	 * @author Alex
	 */
	public User() {
		userList();
	}
	
	/**
	 * Overloaded constructor to create user objects with their related info.
	 * @param name  The user's user name.
	 * @param pin	The user's pin.
	 * @param admin	A boolean to tell if the user should be allowed administrative privileges or not.
	 * @author Alex
	 */
	public User(String name, String pin, boolean admin) {		
		userName = name;
		userPin = pin;
		isAdmin = admin;	
	}
	
	
	
	/**
	 * Private method to initialize the list of users.
	 * @author Chutiwat
	 */
	private void userList(){
		userList = new HashMap<String, User>();
		userList.put("todd", new User("todd", "0001", false));
		userList.put("karlene", new User("karlene", "0002", false));
		userList.put("scott", new User("scott", "0003", false));
		userList.put("cheryl", new User("cheryl", "0004", false));
		userList.put("terry", new User("terry", "0005", false));
		userList.put("martin", new User("martin", "0006", false));
		userList.put("bobby", new User("bobby", "0007", false));
		userList.put("brian", new User("brian", "0008", false));
		userList.put("lisa", new User("lisa", "0009", false));
		userList.put("daniel", new User("daniel", "0010", false));
		userList.put("steve", new User("steve", "0011", false));
		userList.put("john", new User("john", "0012", false));
		userList.put("admin", new User("admin", "1234", true));
	}
	
	/**
	 * Method to handle an attempted login by a user.
	 * 
	 * Checks to see if the user list contains a user name that matches what the user typed in.
	 * 		If those match, the pins are matched against each other.
	 * 		If those match, it checks if the user tried to login as admin.
	 * 			If they did and they have admin privileges, they are granted access.
	 * 			If they didn't, they are granted access to the analyst page.
	 * 		If the pin or user name does not match what is in the user list, no access is granted and
	 * 		the user is warned that the info was not correct or that there is no such user registered, respectively.
	 * 
	 * @author Alex
	 */
	public boolean loginAttempt() {
		userList();
		boolean result = false;
		
		String loginName = this.userName.toString();
		
		if(userList.containsKey(loginName)) {
			User temp = userList.get(this.userName);
			if(this.userPin.compareTo(temp.userPin) == 0) {
				if(this.isAdmin && temp.isAdmin) {
					result = true;
				}
				else if(this.isAdmin && !temp.isAdmin) {
					JOptionPane.showMessageDialog(null, "You do not have admin privileges.");
				}
				else {
					result = true;
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Incorrect User Name or PIN, Please Try Again.");
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "No such user exists, please contact administration to register.");
		}
		return result;
	}
}
