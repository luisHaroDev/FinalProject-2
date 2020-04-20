import java.util.ArrayList;

public class Room {
	
	private final String name;
	
	private final String authorUsername;
	
	private ArrayList<String> users;
	
	public Room(String name, String authorUsername) {
		this.name = name;
		this.authorUsername = authorUsername;
		users = new ArrayList<>();
	}
	
	public void addUser(String username) {
		users.add(username);
	}
	
	public void removeUser(String username) {
		users.remove(username);
	}
	
	public boolean containsUser(String username) {
		return users.contains(username);
	}

	public String getName() {
		return name;
	}

	public String getAuthorUsername() {
		return authorUsername;
	}

	public ArrayList<String> getUsers() {
		return users;
	}
}
