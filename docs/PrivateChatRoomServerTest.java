package com.edu.chat.server;

import com.edu.chat.model.Message;
import com.edu.chat.model.PrivateChatRoom;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for server PrivateChatRoomServer
 */
public class PrivateChatRoomServerTest {
    //max number of attempts
    public static final int MAX_ATTEMPTS = 10;
    //list to save state of private chat room data before test
    List<PrivateChatRoom> privateChatRoomsBeforeTest;

    /**
     * Save state and clean before test
     */
    @Before
    public void before(){
        privateChatRoomsBeforeTest = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PrivateChatRoomServer.PRIVATE_CHATS_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                String[] line = st.split(";");
                List<Message> messageList = new ArrayList<>();
                for (int i = 3; i < line.length -1 ; i += 2) {
                    messageList.add(new Message(line[i], line[i+1]));
                }
                privateChatRoomsBeforeTest.add(new PrivateChatRoom(line[0].trim(), line[1].trim(), line[2].trim(), messageList));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Return first state of data
     */
    @After
    public void after(){
        //clean test data
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PrivateChatRoomServer.PRIVATE_CHATS_DATA_FILE_PATH))) {
            StringBuffer sb = new StringBuffer();
            for (PrivateChatRoom privateChatRoom : privateChatRoomsBeforeTest) {
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
     * Clean data file
     */
    private void clean(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PrivateChatRoomServer.PRIVATE_CHATS_DATA_FILE_PATH))) {
            bw.write("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for #loadPrivateChatRooms() with empty data file
     */
    @Test
    public void testLoadEmptyPrivateChatRooms(){
        List<PrivateChatRoom> privateChatRooms = PrivateChatRoomServer.loadPrivateChatRooms();
        Assert.assertNotNull(privateChatRooms);
        Assert.assertTrue(privateChatRooms.isEmpty());
    }

    /**
     * Test #loadPrivateChatRooms() with not empty data file
     */
    @Test
    public void testLoadPrivateChatRooms(){
        //load test data
        String id = "id", owner = "Owner_Username", companion = "Companion_Username";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PrivateChatRoomServer.PRIVATE_CHATS_DATA_FILE_PATH, true))) {
            bw.append(id + ";" + owner + ";" + companion + ";\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //test
        List<PrivateChatRoom> privateChatRooms = PrivateChatRoomServer.loadPrivateChatRooms();

        //check results
        Assert.assertNotNull(privateChatRooms);
        Assert.assertFalse(privateChatRooms.isEmpty());
        Assert.assertEquals(1, privateChatRooms.size());
        Assert.assertEquals(id, privateChatRooms.get(0).getId());
        Assert.assertEquals(owner, privateChatRooms.get(0).getOwner());
        Assert.assertEquals(companion, privateChatRooms.get(0).getCompanion());
        Assert.assertNotNull(privateChatRooms.get(0).getMessageList());
        Assert.assertTrue(privateChatRooms.get(0).getMessageList().isEmpty());
    }

    /**
     * Test for #createPrivateChatRoom(String, String, String) and
     * #savePrivateChatRoom(String, String, String)
     */
    @Test
    public void createAndSavePrivateChatRoom(){
        //save amount of chat rooms before creating new chat room
        int privateChatRoomsSizeBefore = PrivateChatRoomServer.loadPrivateChatRooms().size();

        //test
        String id = "id", owner = "Owner_Username", companion = "Companion_Username";
        PrivateChatRoomServer.createPrivateChatRoom(id, owner, companion);

        //check results
        List<PrivateChatRoom> privateChatRoomsAfter = PrivateChatRoomServer.loadPrivateChatRooms();
        int privateChatRoomsSizeAfter = privateChatRoomsAfter.size();
        Assert.assertTrue(privateChatRoomsSizeAfter - privateChatRoomsSizeBefore == 1);
        Assert.assertEquals(id, privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getId());
        Assert.assertEquals(owner, privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getOwner());
        Assert.assertEquals(companion, privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getCompanion());
        Assert.assertNotNull(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getMessageList());
        Assert.assertTrue(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getMessageList().isEmpty());
    }

    /**
     * Test for #updatePrivateChatRoomList(List)
     */
    @Test
    public void testUpdatePrivateChatRoomList(){
        //save amount of chat rooms before creating new chat room
        int privateChatRoomsSizeBefore = PrivateChatRoomServer.loadPrivateChatRooms().size();

        //prepare test data
        List<PrivateChatRoom> privateChatRoomsToCreate = new ArrayList<>();
        int newPrivateRooms = 2;
        for(int i = 1 ; i <= newPrivateRooms; i++){
            privateChatRoomsToCreate.add(new PrivateChatRoom("id_" + i,
                    "Owner_" + i,"Companion_" + i, new ArrayList<>()));
        }

        //test
        PrivateChatRoomServer.updatePrivateChatRoomList(privateChatRoomsToCreate);

        //check results
        List<PrivateChatRoom> privateChatRoomsAfter = PrivateChatRoomServer.loadPrivateChatRooms();
        int privateChatRoomsSizeAfter = privateChatRoomsAfter.size();
        Assert.assertTrue(privateChatRoomsSizeAfter - privateChatRoomsSizeBefore == newPrivateRooms);

        Assert.assertEquals("id_1", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - newPrivateRooms).getId());
        Assert.assertEquals("Owner_1", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - newPrivateRooms).getOwner());
        Assert.assertEquals("Companion_1", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - newPrivateRooms).getCompanion());
        Assert.assertNotNull(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - newPrivateRooms).getMessageList());
        Assert.assertTrue(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - newPrivateRooms).getMessageList().isEmpty());

        Assert.assertEquals("id_2", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getId());
        Assert.assertEquals("Owner_2", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getOwner());
        Assert.assertEquals("Companion_2", privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getCompanion());
        Assert.assertNotNull(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getMessageList());
        Assert.assertTrue(privateChatRoomsAfter.get(privateChatRoomsSizeAfter - 1).getMessageList().isEmpty());
    }

    /**
     * Test for #sendMessage(String, String, String)
     */
    @Test
    public void testSendMessage(){
        //prepare test data
        String id = "id", owner = "Owner_Username", companion = "Companion_Username", message = "Hello!";
        PrivateChatRoomServer.createPrivateChatRoom(id, owner, companion);

        //save room state before
        PrivateChatRoom roomBefore = null;
        for (PrivateChatRoom privateChatRoom : PrivateChatRoomServer.loadPrivateChatRooms()) {
            if (privateChatRoom.getId().equals(id)) {
                roomBefore = privateChatRoom;
            }
        }

        //test
        PrivateChatRoomServer.sendMessage(id, owner, message);

        //check results
        PrivateChatRoom roomAfter = null;
        for (PrivateChatRoom privateChatRoom : PrivateChatRoomServer.loadPrivateChatRooms()) {
            if (privateChatRoom.getId().equals(id)) {
                roomAfter = privateChatRoom;
            }
        }
        Assert.assertNotNull(roomBefore);
        Assert.assertNotNull(roomAfter);
        Assert.assertEquals(roomBefore.getId(), roomAfter.getId());
        Assert.assertEquals(roomBefore.getOwner(), roomAfter.getOwner());
        Assert.assertEquals(roomBefore.getCompanion(), roomAfter.getCompanion());
        Assert.assertNotNull(roomBefore.getMessageList());
        Assert.assertTrue(roomBefore.getMessageList().isEmpty());
        Assert.assertNotNull(roomAfter.getMessageList());
        Assert.assertFalse(roomAfter.getMessageList().isEmpty());
        Assert.assertTrue(roomAfter.getMessageList().size() - roomBefore.getMessageList().size() == 1);
        Assert.assertEquals(1, roomAfter.getMessageList().size());
        Assert.assertEquals(roomAfter.getMessageList().get(0).getWriterUsername(), owner);
        Assert.assertEquals(roomAfter.getMessageList().get(0).getMessage(), message);
    }

    /**
     * Test for #lookForPrivateChatUpdate(List)
     * @throws InterruptedException
     */
    /*@Test
    public void testLookForPrivateChatUpdate() throws InterruptedException {
        //prepare test data
        List<PrivateChatRoom> privateChatRooms = PrivateChatRoomServer.loadPrivateChatRooms();
        //update text document with new private room but not privateChatRooms
        String id = "id", owner = "Owner_Username", companion = "Companion_Username";
        PrivateChatRoomServer.createPrivateChatRoom(id, owner, companion);
        //test
        PrivateChatRoomServer.lookForPrivateChatUpdate(privateChatRooms);

        //give time to find updates in text fie and load to the list privateChatRooms
        int attempts = 0;
        while (privateChatRooms.isEmpty() || attempts <= MAX_ATTEMPTS) {
            Thread.sleep(5000);
            attempts++;
        }
        //check results
        Assert.assertFalse(privateChatRooms.isEmpty());
        Assert.assertEquals(1, privateChatRooms.size());
        Assert.assertEquals(id, privateChatRooms.get(0).getId());
        Assert.assertEquals(owner, privateChatRooms.get(0).getOwner());
        Assert.assertEquals(companion, privateChatRooms.get(0).getCompanion());
        Assert.assertNotNull(privateChatRooms.get(0).getMessageList());
        Assert.assertTrue(privateChatRooms.get(0).getMessageList().isEmpty());
    }*/
}
