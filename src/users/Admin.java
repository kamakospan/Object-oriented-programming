package users;

import database.Database;
import enumerations.*;
import menu.MenuItem;
import menu.MenuItems;
import myexceptions.InvalidManagerTypeException;
import utils.Credentials;
import utils.Order;
import utils.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author darkhan
*/
public class Admin extends Employee implements CanSendOrder, CanSendRequest {
    
    
    public Admin() {
	}
    public Admin(String firstName, String lastName) {
    	super(firstName, lastName);
    }

	/**
	 * creates and adds Dean, TechSupport
	 */
    public void addUser(String firstName, String lastName, UserType usertype) {
    	addUser( new UserFactory().getUser(firstName, lastName, usertype));
    }
    /**
     * creates and adds Manager
     */
    public void addUser(String firstName, String lastName, ManagerType managerType) throws InvalidManagerTypeException {
    	addUser( new UserFactory().getUser(firstName, lastName, managerType));
    }
    
    /**
     * creates and adds Teacher
     */
    public void addUser(String firstName, String lastName,Faculty faculty, TeacherType teacherType) {
    	addUser( new UserFactory().getUser(firstName, lastName, faculty, teacherType));
    }
    /**
     * create and adds Student, MasterStudent, Researcher or PhdStudent
     */
    public void addUser(String firstName, String lastName, UserType usertype, Faculty facultyType) {
    	addUser( new UserFactory().getUser(firstName, lastName,  usertype, facultyType));
    }
    /**
     * adds new user to the system
     */
    public void addUser(User u) {
    	String username = Credentials.generateUsername(u.getFirstName(), u.getLastName());
    	String password = Credentials.generateRandomPassword();
    	System.out.println("Autogenerated credentials: "+ username + ", " + password);
    	System.out.println("DO NOT SHARE! Save information for later.");
    	
    	Credentials newCredentials = new Credentials(username, password);
    	Database.DATA.getUsers().put(newCredentials, u);
    }

    
    /**
     * removes user from the system
     */
    public void removeUser(String username) {	
    	Database.DATA.getUsers().keySet().stream()
		.filter(n->n.getUsername().equals(username)).
		map(n->Database.DATA.getUsers().get(n)).filter(n->n instanceof CanBecomeResearcher).forEach(n->((CanBecomeResearcher)n).deleteResearcherAccount());
    	
    	Database.DATA.getUsers().keySet().removeIf(n -> n.getUsername().equals(username));

	}
    /**
    * shows recent log files
    */
    public String viewLogFiles() {
    	List<String> logs = Database.DATA.getLogs().stream().collect(Collectors.toList());
    	Collections.reverse(logs);
    	if(logs.size()>20) {
    		logs = logs.stream().limit(10).collect(Collectors.toList());
    	}
    	if(logs.size()>0) {
    		return logs.stream().collect(Collectors.joining("\n"));
    	}
        return "No recent log files";
    }
    /**
     * sends request to the Rector
     */
    public void sendRequest(String request) {
		Database.DATA.getRector().getRequests().add(new Request(request, this));
	}
    /**
     * sends order to the techSupport
     */
	public void sendOrder(String order, TechSupport techSupport) {
		techSupport.getOrders().add(new Order(order, this));		
	}
	@Override
	public void run() throws IOException {
		MenuItem menu[] = MenuItems.adminMenu;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			menu:while(true) {
				System.out.println("What do you want to do?\n" +
		                "1) View news\n" +
		                "2) Read Notifictions\n" +
		                "3) Print Papers\n" +
		                "4) Manage Journal\n" +
		                "5) Send message\n" +
		                "6) Send request\n" +
		                "7) Send order\n" +
		                "8) Add user\n" +
		                "9) Remove user\n" +
		                "10) View log files\n" +
		                "11) Exit\n" );
				int choice = Integer.parseInt(br.readLine());
				if(choice==11) {
					exit();
					break menu;
				}
				menu[choice-1].execute(this);
				
			}
			
		}catch(Exception e) {
			System.out.println("Oopsiee... \n Saving resources...");
			e.printStackTrace();
			save();
		}		
	}
       
}
