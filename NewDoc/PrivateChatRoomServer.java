package com.edu.chat.server;

import com.edu.chat.model.*;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;

import static com.edu.chat.client.Client.*;

/**
 * Server that works with Private Chat Rooms
 */
public class PrivateChatRoomServer {
    //path to file where all data is saved
    public static final String PRIVATE_CHATS_DATA_FILE_PATH = "resources/private_chats.txt";
    //property to save time (in time millis) when PRIVATE_CHATS_DATA_FILE_PATH was last time modified
    private static long lastFileCheck = System.currentTimeMillis();

    /**
     * Loads private chat rooms from database:
     *      gets file by the path PRIVATE_CHATS_DATA_FILE_PATH;
     *      reads each line from file;
     *      splits line by separator ';';
     *      initializes list with PrivateChatRoom;
     *      closes file.
     *
     * Note. Data in file obeys the format:
     * id;owner;companion;writerUsername;messageText;writerUsername;messageText;
     *
     * @return loaded from database list
     */
    public static List<PrivateChatRoom> loadPrivateChatRooms() {
        List<PrivateChatRoom> privateChatRooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRIVATE_CHATS_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                String[] line = st.split(";");
                List<Message> messageList = new ArrayList<>();
                for (int i = 3; i < line.length -1 ; i+=2) {
                    messageList.add(new Message(line[i], line[i+1]));
                }
                privateChatRooms.add(new PrivateChatRoom(line[0].trim(), line[1].trim(), line[2].trim(), messageList));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateChatRooms;
    }

    /**
     * Creates private chat rooms:
     *      loads all private chat rooms from database;
     *      checks that no chat with such id;
     *      saves new private chat room in database;
     *
     * @param id
     * @param owner
     * @param companion
     */
    public static void createPrivateChatRoom(String id, String owner, String companion) {
        List<PrivateChatRoom> privateChatRoomList = loadPrivateChatRooms();
        for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
            if (privateChatRoom.getId().equals(id)) {
                JOptionPane.showMessageDialog(null, "Such private chat room between " +
                                owner + " and " + companion + " already exists.",
                        "Private Chat Room Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        savePrivateChatRoom(id, owner, companion);
    }

    /**
     * Saves private chat room to database:
     *      loads the file with data;
     *      appends new private chat room to the end of the file;
     *      closes the file;
     *
     * @param id
     * @param owner
     * @param companion
     */
    public static void savePrivateChatRoom(String id, String owner, String companion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRIVATE_CHATS_DATA_FILE_PATH, true))) {
            bw.append(id + ";" + owner + ";" + companion + ";\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates private chat room list:
     *      loads file to write from database;
     *      writes to file all private chat rooms from list (replace);
     *      closes file.
     *
     * @param privateChatRoomList
     */
    public static void updatePrivateChatRoomList(List<PrivateChatRoom> privateChatRoomList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRIVATE_CHATS_DATA_FILE_PATH))) {
            StringBuffer sb = new StringBuffer();
            for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
                sb.append(privateChatRoom.toString());
            }
            bw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message.
     * Writes message to database:
     *      loads all private chat rooms from database;
     *      updates required chat room with new message;
     *      writes list of chat room to database.
     *
     * @param id id of chat to send message
     * @param writerUsername name of writer
     * @param message mm=essage the writer writes
     */
    public static void sendMessage(String id, String writerUsername, String message) {
        List<PrivateChatRoom> privateChatRoomList = loadPrivateChatRooms();
        for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
            if (privateChatRoom.getId().equals(id)) {
                privateChatRoom.getMessageList().add(new Message(writerUsername, message));
                break;
            }
        }
        updatePrivateChatRoomList(privateChatRoomList);
    }

    /**
     * Looks for private chat room updates in database.
     * Creates Runnable object checker. Sends to executes in separate thread while program is running (while true loop).
     */
    public static void lookForPrivateChatUpdate(List<PrivateChatRoom> privateChatRoomList) {
        //Checker will run starting from first call until application is shut down
        PrivateChatRoomChecker checker = new PrivateChatRoomChecker(privateChatRoomList);
        // creating thread pool to execute task which implements Callable
        Executors.newSingleThreadExecutor().execute(checker);
    }

    protected static class PrivateChatRoomChecker implements Runnable {

        private List<PrivateChatRoom> privateChatRoomList;

        public PrivateChatRoomChecker(List<PrivateChatRoom> privateChatRoomList) {
            this.privateChatRoomList = privateChatRoomList;
        }

        @Override
        public void run() {
            while (true) {
                List<PrivateChatRoom> updatedPrivateChatRoomList = null;
                while(updatedPrivateChatRoomList == null)
                {
                    File file = new File(PRIVATE_CHATS_DATA_FILE_PATH);
                    if (file.lastModified() > lastFileCheck) {
                        System.out.println("File PRIVATE CHATS updates are founded.");
                        updatedPrivateChatRoomList = loadPrivateChatRooms();
                        lastFileCheck = file.lastModified();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                privateChatRoomList.clear();
                privateChatRoomList.addAll(updatedPrivateChatRoomList);
                refreshPrivateChatRooms();
                refreshSelectedChat();
            }
        }
    }
}
