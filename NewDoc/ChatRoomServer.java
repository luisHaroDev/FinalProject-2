package com.edu.chat.server;

import com.edu.chat.model.ChatRoom;
import com.edu.chat.model.Message;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.edu.chat.client.Client.*;

/**
 * Server that works with Chat Rooms
 */
public class ChatRoomServer {
    //path to file where all data is saved
    public static final String CHATS_DATA_FILE_PATH = "resources/chats.txt";
    //property to save time (in time millis) when ACCOUNT_DATA_FILE_PATH was last time modified
    private static long lastChatsFileCheck = System.currentTimeMillis();

    /**
     * Creates chat rooms:
     *      checks whether title not null and not empty;
     *      loads all chat rooms from database;
     *      checks that no chat with such id;
     *      saves new chat room in database;
     *
     * @param id
     * @param username
     */
    public static void createChatRoom(String id, String username) {
        if (id == null || id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Title: " + id +
                    " is not suitable for Chat Room.", "Chat Room Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<ChatRoom> chatRoomList = loadChatRooms();
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getId().equals(id)) {
                JOptionPane.showMessageDialog(null, "Chat Room with title: " + id +
                        " already exists.", "Chat Room Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        saveChatRoom(id, username);
    }

    /**
     * Saves chat room to database:
     *      loads the file with data;
     *      appends new chat room to the end of the file;
     *      closes the file;
     *
     * @param id
     * @param username
     */
    public static void saveChatRoom(String id, String username) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CHATS_DATA_FILE_PATH, true))) {
            bw.append(id + ";" + username + ";\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates chat room list:
     *      loads file to write from database;
     *      writes to file all chat rooms from list (replace);
     *      closes file.
     *
     * @param chatRoomList
     */
    public static void updateChatRoomList(List<ChatRoom> chatRoomList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CHATS_DATA_FILE_PATH))) {
            StringBuffer sb = new StringBuffer();
            for (ChatRoom chatRoom : chatRoomList) {
                sb.append(chatRoom.toString());
            }
            bw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads chat rooms from database:
     *      gets file by the path CHATS_DATA_FILE_PATH;
     *      reads each line from file;
     *      splits line by separator ';';
     *      initializes list with ChatRoom;
     *      closes file.
     *
     * Note. Data in file obeys the format:
     * id;owner;writerUsername;messageText;writerUsername;messageText;
     *
     * @return loaded from database list
     */
    public static List<ChatRoom> loadChatRooms() {
        List<ChatRoom> chatRoomList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CHATS_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                String[] line = st.split(";");
                ChatRoom chatRoom = new ChatRoom(line[0].trim(), line[1].trim());
                for (int i = 2; i < line.length - 1; i+=2) {
                    chatRoom.getMessageList().add(new Message(line[i], line[i+1]));
                }
                chatRoomList.add(chatRoom);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatRoomList;
    }

    /**
     * Sends message.
     * Writes message to database:
     *      loads all chat rooms from database;
     *      updates required chat room with new message;
     *      writes list of chat room to database.
     *
     * @param id id of chat to send message
     * @param writerName name of writer
     * @param message mm=essage the writer writes
     */
    public static void sendMessage(String id, String writerName, String message) {
        List<ChatRoom> chatRoomList = loadChatRooms();
        for (ChatRoom chatRoom : chatRoomList) {
            if (chatRoom.getId().equals(id)) {
                chatRoom.getMessageList().add(new Message(writerName, message));
                break;
            }
        }
        updateChatRoomList(chatRoomList);
    }

    /**
     * Looks for chat room updates in database.
     * Creates Runnable object checker. Sends to executes in separate thread while program is running (while true loop).
     */
    public static void lookForChatroomUpdate(List<ChatRoom> chatRoomList) {
        //Checker will run starting from first call until application is shut down
        ChatRoomChecker checker = new ChatRoomChecker(chatRoomList);
        // creating thread pool to execute task which implements Callable
        Executors.newSingleThreadExecutor().execute(checker);
    }

    protected static class ChatRoomChecker implements Runnable {

        private List<ChatRoom> chatRoomList;

        public ChatRoomChecker(List<ChatRoom> chatRoomList) {
            this.chatRoomList = chatRoomList;
        }

        @Override
        public void run() {
            while (true) {
                List<ChatRoom> updatedChatRoomList = null;
                while(updatedChatRoomList == null)
                {
                    File file = new File(CHATS_DATA_FILE_PATH);
                    if (file.lastModified() > lastChatsFileCheck) {
                        System.out.println("File CHATS updates are founded.");
                        updatedChatRoomList = loadChatRooms();
                        lastChatsFileCheck = file.lastModified();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                chatRoomList.clear();
                chatRoomList.addAll(updatedChatRoomList);
                refreshChatRooms();
                refreshSelectedChat();
            }
        }
    }
}
