import java.time.LocalDateTime;

public class Message {

	private String text;
	
	private String senderUsername;
	
	private String receiverName; // Can be either username if private message, or
	// room name.
	
	private LocalDateTime timestamp;

	public Message(String text, String senderUsername, String receiverName, LocalDateTime timestamp) {
		this.text = text;
		this.senderUsername = senderUsername;
		this.receiverName = receiverName;
		this.timestamp = timestamp;
	}
	
	public Message() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(getSenderUsername()).append("  ")
		.append(getTimestamp().format(Server.TIMESTAMP_FORMAT)).append("\n").append(getText()).append("\n\n");
		return output.toString();
	}
	
}
