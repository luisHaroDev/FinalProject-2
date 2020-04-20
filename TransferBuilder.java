import java.time.LocalDateTime;
import java.util.List;

public class TransferBuilder {

	public static Transferable build(String username, LocalDateTime timestamp, String text,
			String sender, String receiver, String password, String command, 
			String roomName, List<String> availableRooms, List<String> availableUsers) {
		
		return new Transferable() {

			private static final long serialVersionUID = 1L;

			@Override
			public String username() {
				return username;
			}
			
			@Override
			public LocalDateTime timestamp() {
				return timestamp;
			}
			
			@Override
			public String text() {
				return text;
			}
			
			@Override
			public String sender() {
				return sender;
			}
			
			@Override
			public String receiver() {
				return receiver;
			}
			
			@Override
			public String password() {
				return password;
			}
			
			@Override
			public String command() {
				return command;
			}

			@Override
			public String roomName() {
				return roomName;
			}

			@Override
			public List<String> availableRooms() {
				return availableRooms;
			}

			@Override
			public List<String> availableUsers() {
				return availableUsers;
			}
		};
	}
}
