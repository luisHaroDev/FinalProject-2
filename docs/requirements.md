# Chat-thing Requirements Document


## Purpose
### Problem
People want to communicate with each other since they are not able to see each other in person due to work or other reasons. Other people simply want to make new friends. The best way to communicate and make friends is through a chat application that allows them to interact with many people or just one person directly.

### Goal
Our goal is to build a free, easy, and private chat room application that allows people to communicate with one another or a group of people in a chat room.

### Background
This is a group project for CS401 Software Engineering. We have been tasked with a semester-long software engineering project that requires us to implement a substantial piece of software using engineering principles and techniques that we have learned in class. We have selected a chat room application running over a network in a client-server model because it is a useful tool demonstrating network programming, information security, and object-oriented design.

### Definitions
1. Client – The software that runs on the end of the user’s machine, allowing them to connect to a chat room and send messages as well as receive messages.

2. Server – The software that runs on a remote machine and allows clients to communicate with each other in chat rooms and send direct/private messages betweeen two people only.

3. Database – The software the stores information about users, chat rooms, and message histories.

4. User – A representation of an end user within the application, including a username for identification in chat rooms and a password for authentication.

5. Message – A representation of a text-based communication within the application, including a text body, a timestamp, and a chat room that the message will be a part of.

6. Chatroom – A collection of Users who can send and receive messages that will be sent to all other Users tht are in the same collection (room).

7. DirectMessage (DM) – A special chatroom that contains only two Users who can communicate by sending direct/private messages between each other.

## Environment and system requirements
### System overview / Abstract
This application will be a simple chat room and messaging system using a client-server model. It will allow users on the client side to create an account, using a username and password, join a particular chat room, and send/reeive messages in that chat room. The server will keep track of each chat room and forward messages from users in a chat room to all other users in the same chat room. The server will use a database in order to maintain user accounts,such as userID's and password, chat rooms, and all chat room's message histories. The client application will present controls to create an account, log in, select a chat room to chat with other people, view the chat room's message history once you have entered that specific chat room, send messages, and leave a chat room. It will also be able to allow private messages between two users. The purpose of this chat application is to make a smoother communication for people, and broadcast general notifications, such as user logins and logouts.

### Environment
#### Client environment
1.	System running a modern version of the Java Runtime Environment
2.	Connected to a network with a server (local or Internet)
3.	Capable of displaying a GUI
4.	Capable of capturing user input with mouse and keyboard in order to interacting with GUI

#### Server environment
1.	System running a modern version of the Java Runtime Environment and a database system that works with Java
2.	Connected to a network that clients can also connect to (local or Internet)
3.	Capable of displaying a command line interface
4.	Capable of capturing user input with mouse and keyboard for interacting with CLI


### Functional requirements
#### Client
1.	Connect to the server over a network automatically when it first gets launched
2.	Allow the user to create a new account with a username and password
3.	Allow the user to log in with a username and password
4.	Get list of available chatrooms from server and display in the GUI
5.	Allow a logged in user to select and join an available chatroom from the list
6.	Get the chat log and users list for the specific chatroom and display it on screen when user selects a chatroom
7.	Allow the user to send text messages to the chatroom
8.	Receive messages from the server and asynchronously update the chat log displayed on screen to show the new messages
9.	Allow the user to send text messages to other users in the chatroom by selecting them from the user list (DirectMessage)
10.	Display a system notification when:
  -	A user joins the chat room
  -	A user leaves the chat room
  -	The user receives a direct message
11.	Allow the user to leave the chatroom and display the list of other available chatrooms to join
12.	Allow the user to log out and exit the application, closing the server connection

#### Server
1.	Allow connections from different clients over a network
2.	Maintain client connections until the client decides to exit and close the connection
3.	Connect to a database on the local system
4.	Allow connected clients to create new accounts
5.	Allow connected clients to log in using their proper usernames and passwords
6.	Send list of available chatrooms to clients when it gets requested by the client
7.	Manage users in each chatroom, adding and removing as needed based on client connection status
8.	Broadcast messages from users in chatrooms to all other users in the same chatroom
9.	Maintain a chat log for each chatroom and store the inforamtion in the database
10.	Allow system administrator to create and delete chatrooms via a command line interface

#### Database
1.	Store user account information – usernameID, username, and encrypted password
2.	Store chatroom message history for each chatroom

### Non-functional requirements

#### Platform and technology to be used
1.	All code will be written in Java without relying on system-dependent calls in order to keep the application usable on all platforms that support a modern version of the Java Runtime Environment.
2.	Server component will require a local database that the server interacts with using the JDBC interface. Therefore, the database system must support JDBC.

#### System-wide requirements
1.	Usernames must be unique

#### Client
1.	Client should be able to handle network errors, such as inability to connect to server, loss of connection without crashing

#### Server
1.	Server should handle network errors without crashing

#### Database
1.	Passwords must be encrypted
2.  Usernames will have a unique userID
3.	When a chatroom is deleted from the server, the chatroom message history must be deleted from the database
