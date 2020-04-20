import java.io.ObjectOutputStream;

public class Account {

	private String username;
	
	private String password;
	
	private boolean loggedin;
	
	private Account joined;
	
	private ObjectOutputStream oos;
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Account() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLoggedin() {
		return loggedin;
	}

	public void setLoggedin(boolean loggedin) {
		this.loggedin = loggedin;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public Account getJoined() {
		return joined;
	}

	public void setJoined(Account joined) {
		this.joined = joined;
	}
	
	
}
