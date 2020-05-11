package com.edu.chat.server;

import com.edu.chat.model.ChatRoom;
import com.edu.chat.model.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for ChatRoomServer
 */
public class ChatRoomServerTest {
    //max number of attemptsChatRoomServerTest {
    public static final int MAX_ATTEMPTS = 10;
    //list to save state of chat room data before test
    List<ChatRoom> chatRoomsBeforeTest;

    /**
     * Save state before test
     */
    @Before
    public void before(){
        chatRoomsBeforeTest = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ChatRoomServer.CHATS_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                String[] line = st.split(";");
                ChatRoom chatRoom = new ChatRoom(line[0].trim(), line[1].trim());
                for (int i = 2; i < line.length - 1; i+=2) {
                    chatRoom.getMessageList().add(new Message(line[i], line[i+1]));
                }
                chatRoomsBeforeTest.add(chatRoom);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return first state of data
     */
    @After
    public void after(){
        //clean test data
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ChatRoomServer.CHATS_DATA_FILE_PATH))) {
            StringBuffer sb = new StringBuffer();
            for (ChatRoom chatRoom : chatRoomsBeforeTest) {
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
     * Test #loadChatRooms() with not empty data file
     */
    @Test
    public void testLoadChatRooms(){
        //load test data
        String id = "id", owner = "Owner_Username";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ChatRoomServer.CHATS_DATA_FILE_PATH, true))) {
            bw.append(id + ";" + owner + ";\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //test
        List<ChatRoom> chatRooms = ChatRoomServer.loadChatRooms();

        //check results
        Assert.assertNotNull(chatRooms);
        Assert.assertFalse(chatRooms.isEmpty());
        Assert.assertEquals(chatRoomsBeforeTest.size() + 1, chatRooms.size());
        Assert.assertEquals(id, chatRooms.get(chatRoomsBeforeTest.size()).getId());
        Assert.assertEquals(owner, chatRooms.get(chatRoomsBeforeTest.size()).getOwner());
        Assert.assertNotNull(chatRooms.get(chatRoomsBeforeTest.size()).getMessageList());
        Assert.assertTrue(chatRooms.get(chatRoomsBeforeTest.size()).getMessageList().isEmpty());
    }

    /**
     * Test for #createChatRoom(String, String) and
     * #saveChatRoom(String, String)
     */
    @Test
    public void createAndSaveChatRoom(){
        //save amount of chat rooms before creating new chat room
        int chatRoomsSizeBefore = ChatRoomServer.loadChatRooms().size();

        //test
        String id = "id", owner = "Owner_Username";
        ChatRoomServer.createChatRoom(id, owner);

        //check results
        List<ChatRoom> chatRoomsAfter = ChatRoomServer.loadChatRooms();
        int chatRoomsSizeAfter = chatRoomsAfter.size();
        Assert.assertTrue(chatRoomsSizeAfter - chatRoomsSizeBefore == 1);
        Assert.assertEquals(id, chatRoomsAfter.get(chatRoomsSizeAfter - 1).getId());
        Assert.assertEquals(owner, chatRoomsAfter.get(chatRoomsSizeAfter - 1).getOwner());
        Assert.assertNotNull(chatRoomsAfter.get(chatRoomsSizeAfter - 1).getMessageList());
        Assert.assertTrue(chatRoomsAfter.get(chatRoomsSizeAfter - 1).getMessageList().isEmpty());
    }

    /**
     * Test for #updateChatRoomList(List)
     */
    @Test
    public void testUpdateChatRoomList(){
        //save amount of chat rooms before creating new chat room
        int chatRoomsSizeBefore = ChatRoomServer.loadChatRooms().size();

        //prepare test data
        List<ChatRoom> chatRoomsToCreate = new ArrayList<>();
        int newRooms = 2;
        for(int i = 1 ; i <= newRooms; i++){
            chatRoomsToCreate.add(new ChatRoom("id_" + i, "Owner_" + i));
        }
        chatRoomsToCreate.addAll(chatRoomsBeforeTest);

        //test
        ChatRoomServer.updateChatRoomList(chatRoomsToCreate);

        //check results
        List<ChatRoom> chatRoomsAfter = ChatRoomServer.loadChatRooms();
        int chatRoomsSizeAfter = chatRoomsAfter.size();
        Assert.assertTrue(chatRoomsSizeAfter - chatRoomsSizeBefore == newRooms);
        Assert.assertEquals("id_1", chatRoomsAfter.get(0).getId());
        Assert.assertEquals("Owner_1", chatRoomsAfter.get(0).getOwner());
        Assert.assertNotNull(chatRoomsAfter.get(0).getMessageList());
        Assert.assertTrue(chatRoomsAfter.get(0).getMessageList().isEmpty());

        Assert.assertEquals("id_2", chatRoomsAfter.get(newRooms - 1).getId());
        Assert.assertEquals("Owner_2", chatRoomsAfter.get(newRooms - 1).getOwner());
        Assert.assertNotNull(chatRoomsAfter.get(newRooms - 1).getMessageList());
        Assert.assertTrue(chatRoomsAfter.get(newRooms - 1).getMessageList().isEmpty());
    }

    /**
     * Test for #sendMessage(String, String)
     */
    @Test
    public void testSendMessage(){
        //prepare test data
        String id = "id", owner = "Owner_Username", writer = "Writer_Username", message = "Hello!";
        ChatRoomServer.createChatRoom(id, owner);

        //save room state before
        ChatRoom roomBefore = null;
        for (ChatRoom chatRoom : ChatRoomServer.loadChatRooms()) {
            if (chatRoom.getId().equals(id)) {
                roomBefore = chatRoom;
            }
        }

        //test
        ChatRoomServer.sendMessage(id, writer, message);

        //check results
        ChatRoom roomAfter = null;
        for (ChatRoom chatRoom : ChatRoomServer.loadChatRooms()) {
            if (chatRoom.getId().equals(id)) {
                roomAfter = chatRoom;
            }
        }
        Assert.assertNotNull(roomBefore);
        Assert.assertNotNull(roomAfter);
        Assert.assertEquals(roomBefore.getId(), roomAfter.getId());
        Assert.assertEquals(roomBefore.getOwner(), roomAfter.getOwner());
        Assert.assertNotNull(roomBefore.getMessageList());
        Assert.assertTrue(roomBefore.getMessageList().isEmpty());
        Assert.assertNotNull(roomAfter.getMessageList());
        Assert.assertFalse(roomAfter.getMessageList().isEmpty());
        Assert.assertTrue(roomAfter.getMessageList().size() - roomBefore.getMessageList().size() == 1);
        Assert.assertEquals(1, roomAfter.getMessageList().size());
        Assert.assertEquals(roomAfter.getMessageList().get(0).getWriterUsername(), writer);
        Assert.assertEquals(roomAfter.getMessageList().get(0).getMessage(), message);
    }

    /**
     * Test for #lookForChatroomUpdate(List)
     * @throws InterruptedException
     */
    @Test
    public void testLookForChatUpdate() throws InterruptedException {
        //prepare test data
        List<ChatRoom> chatRooms = ChatRoomServer.loadChatRooms();
        //update text document with new room but not chatRooms
        String id = "id", owner = "Owner_Username";
        ChatRoomServer.createChatRoom(id, owner);
        //test
        ChatRoomServer.lookForChatroomUpdate(chatRooms);

        //give time to find updates in text fie and load to the list chatRooms
        int attempts = 0;
        while (chatRooms.size() == chatRoomsBeforeTest.size() || attempts <= MAX_ATTEMPTS) {
            Thread.sleep(5000);
            attempts++;
        }
        //check results
        Assert.assertFalse(chatRooms.isEmpty());
        Assert.assertEquals(chatRoomsBeforeTest.size() + 1, chatRooms.size());
        Assert.assertEquals(id, chatRooms.get(chatRoomsBeforeTest.size()).getId());
        Assert.assertEquals(owner, chatRooms.get(chatRoomsBeforeTest.size()).getOwner());
        Assert.assertNotNull(chatRooms.get(chatRoomsBeforeTest.size()).getMessageList());
        Assert.assertTrue(chatRooms.get(chatRoomsBeforeTest.size()).getMessageList().isEmpty());
    }
}
