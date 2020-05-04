package com.edu.chat.client;

import com.edu.chat.model.*;
import com.edu.chat.server.AccountServer;
import com.edu.chat.server.PrivateChatRoomServer;
import com.edu.chat.server.ChatRoomServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Client GUI
 */
public class Client extends JFrame {

    //logged in user account
    private static Account account;
    //list of all chat rooms from database
    private static List<ChatRoom> chatRoomList;
    //list of all private chat rooms  from database
    private static List<PrivateChatRoom> privateChatRoomList;

    //panel consists of log in and log out panel (the first to appear on GUI)
    private static JPanel logInOutPnl;
    private static JPanel loginPnl;
    private static JPanel logoutPnl;
    //panel consists of chat rooms and private chat panels (the second to appear on GUI)
    private static JPanel privateChatAndRoomPnl;
    private static JPanel chatRoomPnl;
    private static JPanel privateChatPnl;
    //panel cobsists of chat message and send message panels (the third  and the last to appear on GUI)
    private static JPanel chatPnl;
    private static JPanel chatMessagePnl;
    private static JPanel sendMessagePnl;

    //buttons represent all chat rooms in database
    private static List<JButton> chatButtonList;
    //buttons reprent private chat rooms that logged in user takes place
    private static List<JButton> privateChatButtonList;
    //button that sens message (in both chats)
    private static JButton sendMessageButton;
    //button allow to open the panel with combo box to create private chat room
    private static JButton chooseAccountButton;
    //combo box represents online users and is updated automatically when other user is logged in/out
    private static JComboBox chooseAccountBox;
    //label to greet logged in user
    private static JLabel greetingLabel;

    /**
     * Main method creates CLient GUI
     * @param args
     */
    public static void main(String[] args) {
        Client frame = new Client();
        frame.setSize(600, 600);
        frame.createGUI();
        frame.setVisible(true);
    }

    /**
     * Initialise basic Components: panels, buttons, labels
     */
    private void createGUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        GridLayout gridLayout = new GridLayout(3, 1);
        window.setLayout(gridLayout);

        logInOutPnl = new JPanel();
        logInOutPnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(logInOutPnl);

        loginPnl = new JPanel();
        loginPnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        logInOutPnl.add(loginPnl);

        logoutPnl = new JPanel();
        logoutPnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        logInOutPnl.add(logoutPnl);
        logoutPnl.setVisible(false);

        privateChatAndRoomPnl = new JPanel();
        privateChatAndRoomPnl.setLayout(new BoxLayout(privateChatAndRoomPnl, BoxLayout.Y_AXIS));
        privateChatAndRoomPnl.setVisible(false);
        add(privateChatAndRoomPnl);

        chatRoomPnl = new JPanel();
        chatRoomPnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        chatRoomPnl.setVisible(false);
        chatRoomPnl.add(new JLabel("Available Chat Rooms:"));
        privateChatAndRoomPnl.add(chatRoomPnl);

        privateChatPnl = new JPanel();
        privateChatPnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        privateChatPnl.setVisible(false);
        privateChatPnl.add(new JLabel("Private User's Chats:"));
        privateChatAndRoomPnl.add(privateChatPnl);

        chatPnl = new JPanel();
        chatPnl.setVisible(false);
        chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
        add(chatPnl);

        greetingLabel = new JLabel();
        logoutPnl.add(greetingLabel);
        JButton logoutButton = new JButton("Logout");
        logoutPnl.add(logoutButton);
        JButton loginButton = new JButton("Login");
        loginPnl.add(loginButton);
        JButton createAccountButton = new JButton("Create Account");
        loginPnl.add(createAccountButton);
        //Set action listeners
        logoutButton.addActionListener(new LogoutActionListener());
        loginButton.addActionListener(new LoginActionListener());
        createAccountButton.addActionListener(new CreateAccountActionListener());
    }

    /**
     * Loads all chats (private and common) from database via Server,
     * calls for initialization for each of them (see privateChatAndRoomPnl),
     * calls Servers to start checking:
     *      if any other user is logged in/out (AccountServer),
     *      if any chat room was created/deleted/message written (ChatRoomService),
     *      if any private chat room was created/deleted/message written (PrivateChatRoomService)
     */
    private void startChat () {
        chatRoomList = ChatRoomServer.loadChatRooms();
        privateChatRoomList = PrivateChatRoomServer.loadPrivateChatRooms();
        createChatRoomUI();
        createPrivateChatRoomUI();
        ChatRoomServer.lookForChatroomUpdate(chatRoomList);
        AccountServer.lookForAccountsUpdate();
        PrivateChatRoomServer.lookForPrivateChatUpdate(privateChatRoomList);
    }

    /**
     * Initializes privateChatAndRoomPnl (the second panel on GUI) with all chat rooms from database and
     * adds button createChatRoomButton to create new chat from GUI,
     * adds ActionListeners for button
     * (all ActionListeners are listed as separated inner classes below).
     */
    private void createChatRoomUI() {
        privateChatAndRoomPnl.setVisible(true);
        chatButtonList = new ArrayList<>();
        chatRoomPnl.setVisible(true);
        for (ChatRoom chatRoom : chatRoomList) {
            JButton chatButton = new JButton(chatRoom.getId());
            chatButtonList.add(chatButton);
            chatRoomPnl.add(chatButton);
            JoinChatRoomActionListener joinChatRoomActionListener = new JoinChatRoomActionListener(chatRoom);
            chatButton.addActionListener(joinChatRoomActionListener);
        }
        JButton createChatRoomButton = new JButton("+");
        createChatRoomButton.setBackground(Color.WHITE);
        chatRoomPnl.add(createChatRoomButton);
        createChatRoomButton.addActionListener(new AddChatActionListener());
    }

    /**
     * Initializes privateChatAndRoomPnl (the second panel on GUI) with PRIVATE chat rooms from database (NOT ALL)
     * in which logged in user takes place and adds button createPrivateChatRoomButton to create new private chat
     * from GUI with user that should be chosen from chooseAccountBox (press "+" button to see),
     * adds ActionListeners for button
     * (all ActionListeners are listed as separated inner classes below).
     */
    private void createPrivateChatRoomUI() {
        //Private Chat part
        privateChatButtonList = new ArrayList<>();
        privateChatPnl.setVisible(true);
        for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
            //If logged in user has private chat room then display it
            if (privateChatRoom.getOwner().equals(account.getUsername()) ||
                    privateChatRoom.getCompanion().equals(account.getUsername())) {
                JButton privateChatButton = new JButton(privateChatRoom.getId());
                privateChatButtonList.add(privateChatButton);
                privateChatPnl.add(privateChatButton);
                StartPrivateChatActionListener startPrivateChatActionListener = new StartPrivateChatActionListener(privateChatRoom);
                privateChatButton.addActionListener(startPrivateChatActionListener);
            }
        }
        JButton createPrivateChatRoomButton = new JButton("+");
        createPrivateChatRoomButton.setBackground(Color.WHITE);
        privateChatPnl.add(createPrivateChatRoomButton);
        createPrivateChatRoomButton.addActionListener(new AddPrivateChatRoomActionListener());
    }

    /**
     * Initializes chatPnl (place where User starts communication).
     *
     * @param ichat - chat that chould be init
     */
    private static void createChatUI(IChat ichat) {
        //Clear chat panel
        chatPnl.removeAll();
        chatPnl.setVisible(true);

        JPanel ownerInfoPanel = new JPanel();
        ownerInfoPanel.setLayout(new FlowLayout());
        chatPnl.add(ownerInfoPanel);
        //Load info about chat in format "Title [Owner: Username]"
        ownerInfoPanel.add(new JLabel(ichat.getId() + " [ Owner: " + ichat.getOwner() + "]"));
        //If owner is current user then allow to delete chat
        if (ichat.getOwner().equals(account.getUsername())) {
            JButton deleteChatRoomButton = new JButton("Delete Chat");
            ownerInfoPanel.add(deleteChatRoomButton);
            deleteChatRoomButton.addActionListener(new DeleteChatActionListener(ichat.getId()));
        }

        //panel to hold messages
        chatMessagePnl = new JPanel();
        chatMessagePnl.setLayout(new FlowLayout(FlowLayout.LEFT));
        chatPnl.add(chatMessagePnl);

        //Load messages from database in format "[Message Writer]: Message"
        for (Message message : ichat.getMessageList()) {
            JPanel pnl = new JPanel();
            pnl.setLayout(new FlowLayout());
            JLabel owner = new JLabel("[" + message.getWriterUsername() + "] : ");
            pnl.add(owner);
            JLabel mess = new JLabel(message.getMessage());
            pnl.add(mess);
            chatMessagePnl.add(pnl);
        }

        //Construct text field and button to send Client message
        sendMessagePnl = new JPanel();
        chatPnl.add(sendMessagePnl);
        JTextField yourMessage = new JTextField("", 10);
        sendMessagePnl.add(yourMessage);
        sendMessageButton = new JButton("Send");
        sendMessageButton.setEnabled(false);
        sendMessagePnl.add(sendMessageButton);
        //Disable button "Send" until some text will be typed. It will prevent of sending empty messages
        yourMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessageButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(!yourMessage.getText().isEmpty()){
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }
        });
        sendMessageButton.addActionListener(new SendMessageActionListener(ichat, yourMessage));
        //Add button to close chat
        JButton closeChatButton = new JButton("Close Chat");
        sendMessagePnl.add(closeChatButton);
        closeChatButton.addActionListener(new CloseChatActionListener());

        //Refresh
        chatMessagePnl.validate();
        chatMessagePnl.repaint();
        sendMessagePnl.validate();
        sendMessagePnl.repaint();
        chatPnl.validate();
        chatPnl.repaint();
    }

    /**
     * Refreshes chatRoomPnl.
     * It is called by ChatRoomServer when any related updates are founded and needed to be loaded on UI.
     * We have to check any updates and inform user (using JOption) here because it is the only way when one user will
     * see updates from another one.
     *
     */
    public static void refreshChatRooms() {
        //Do some check to understand whether something was deleted or created
        String selectedChatTitle = null;
        int foundedChatsAmount = 0;
        for (JButton jButton : chatButtonList) {
            //Remember what chat was selected.
            if (selectedChatTitle == null && !jButton.isEnabled()) {
                selectedChatTitle = jButton.getText();
            }
            boolean isChatExist = false;
            //Check whether any chat was deleted and notify user
            for (ChatRoom chatRoom : chatRoomList) {
                if (chatRoom.getId().equals(jButton.getText())) {
                    foundedChatsAmount++;
                    isChatExist = true;
                }
            }
            if (!isChatExist) {
                JOptionPane.showMessageDialog(null, "Chat '" + jButton.getText() +
                        "' was deleted." , "Chat Room Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        //If new chat rooms were created notify user
        if (foundedChatsAmount < chatRoomList.size()) {
            JOptionPane.showMessageDialog(null, "New Chat Rooms are created." ,
                    "Chat Room Info", JOptionPane.INFORMATION_MESSAGE);
        }

        //Start refreshing of chatRoomPnl
        chatRoomPnl.removeAll();
        chatRoomPnl.setVisible(true);
        chatRoomPnl.add(new JLabel("Available Chat Rooms:"));
        chatButtonList.clear();
        for (ChatRoom chatRoom : chatRoomList) {
            JButton chatButton = new JButton(chatRoom.getId());
            if (chatRoom.getId().equals(selectedChatTitle)) {
                chatButton.setEnabled(false);
            }
            chatButtonList.add(chatButton);
            chatRoomPnl.add(chatButton);
            JoinChatRoomActionListener joinChatRoomActionListener = new JoinChatRoomActionListener(chatRoom);
            chatButton.addActionListener(joinChatRoomActionListener);
        }
        JButton createChatRoomButton = new JButton("+");
        createChatRoomButton.setBackground(Color.WHITE);
        chatRoomPnl.add(createChatRoomButton);
        createChatRoomButton.addActionListener(new AddChatActionListener());
        chatRoomPnl.validate();
        chatRoomPnl.repaint();
    }

    /**
     * Refreshes privateChatPnl.
     * It is called by PrivateChatRoomServer when any related updates are founded and needed to be loaded on UI.
     * We have to check any updates and inform user (using JOption) here because it is the only way when one user will
     * see updates from another one.
     *
     */
    public static void refreshPrivateChatRooms() {
        //Do some check to understand whether something was deleted or created
        String selectedPrivateChatTitle = null;
        int foundedPrivateChatsAmount = 0;
        //count amount of private chats before update
        int beforeUpdatePrivateChatsAmount = 0;
        for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
            if(privateChatRoom.getOwner().equals(account.getUsername()) ||
                    privateChatRoom.getCompanion().equals(account.getUsername())) {
                beforeUpdatePrivateChatsAmount++;
            }
        }
        for (JButton jButton : privateChatButtonList) {
            //Remember what private chat was selected.
            if (selectedPrivateChatTitle == null && !jButton.isEnabled()) {
                selectedPrivateChatTitle = jButton.getText();
            }
            boolean isPrivateChatExist = false;
            //Check whether any private chat was deleted and notify user
            for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
                if (privateChatRoom.getId().equals(jButton.getText())) {
                    foundedPrivateChatsAmount++;
                    isPrivateChatExist = true;
                    break;
                }
            }
            if (!isPrivateChatExist) {
                JOptionPane.showMessageDialog(null, "Private Chat " + jButton.getText() +
                        " was deleted." , "Private  Chat Room Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        //If new private chat rooms were created notify user
        if (foundedPrivateChatsAmount < beforeUpdatePrivateChatsAmount) {
            JOptionPane.showMessageDialog(null, "New Private Chat Rooms are created." ,
                    "Private Chat Room Info", JOptionPane.INFORMATION_MESSAGE);
        }

        //Rebuild the private chat room panel
        privateChatPnl.removeAll();
        privateChatPnl.setVisible(true);
        privateChatPnl.add(new JLabel("Private User's Chats:"));
        privateChatButtonList.clear();
        for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
            if (privateChatRoom.getOwner().equals(account.getUsername()) ||
                    privateChatRoom.getCompanion().equals(account.getUsername())) {
                JButton privateChatButton = new JButton(privateChatRoom.getId());
                if (privateChatRoom.getId().equals(selectedPrivateChatTitle)) {
                    privateChatButton.setEnabled(false);
                }
                privateChatButtonList.add(privateChatButton);
                privateChatPnl.add(privateChatButton);
                StartPrivateChatActionListener startPrivateChatActionListener = new StartPrivateChatActionListener(privateChatRoom);
                privateChatButton.addActionListener(startPrivateChatActionListener);
            }
        }
        JButton createPrivateChatRoomButton = new JButton("+");
        createPrivateChatRoomButton.setBackground(Color.WHITE);
        privateChatPnl.add(createPrivateChatRoomButton);
        createPrivateChatRoomButton.addActionListener(new AddPrivateChatRoomActionListener());
        privateChatPnl.validate();
        privateChatPnl.repaint();
    }

    /**
     * Refreshes chatPnl.
     * It is called by ChatRoomServer and PrivateChatRoomServer when any new message appears and needed to be loaded on UI.
     *
     */
    public static void refreshSelectedChat() {
        //Find and remember what chat was opened (selected) by user as every thing will be removed from panel
        //and it is important to save last state.
        String selectedChatTitle = null;
        boolean isPrivate = false;
        for (JButton jButton : chatButtonList) {
            if (!jButton.isEnabled()) {
                selectedChatTitle = jButton.getText();
                break;
            }
        }
        if(selectedChatTitle == null) {
            for (JButton jButton : privateChatButtonList) {
                if (!jButton.isEnabled()) {
                    selectedChatTitle = jButton.getText();
                    break;
                }
            }
            isPrivate = true;
        }

        //If chat was opened (selected) then rebuild (refresh) it with new messages
        if(selectedChatTitle != null) {
            chatMessagePnl.removeAll();
            IChat selectedChatRoom = null;
            if (isPrivate) {
                for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
                    if (privateChatRoom.getId().equals(selectedChatTitle)) {
                        selectedChatRoom = privateChatRoom;
                        break;
                    }
                }
            } else {
                for (ChatRoom chatRoom : chatRoomList) {
                    if (chatRoom.getId().equals(selectedChatTitle)) {
                        selectedChatRoom = chatRoom;
                        break;
                    }
                }
            }
            //Load messages from database in format "[Message Writer]: Message"
            for (Message message : selectedChatRoom.getMessageList()) {
                JPanel pnl = new JPanel();
                pnl.setLayout(new FlowLayout());
                JLabel owner = new JLabel("[" + message.getWriterUsername() + "] : ");
                pnl.add(owner);
                JLabel mess = new JLabel(message.getMessage());
                pnl.add(mess);
                chatMessagePnl.add(pnl);
            }
            chatMessagePnl.validate();
            chatMessagePnl.repaint();
            chatPnl.validate();
            chatPnl.repaint();
        }

    }

    /**
     * Refreshes chooseAccountBox.
     * It is called by AccountServer when any user is logged in or logged out.
     * It is needed for make possible to create private chat rooms from UI.
     *
     */
    public static void refreshAccountComboBox() {
        if (chooseAccountBox != null) {
            privateChatPnl.remove(chooseAccountBox);
            String[] onlineAccounts = AccountServer.getOnlineAccountsArray(account.getUsername());
            chooseAccountBox = new JComboBox<>(onlineAccounts);
            privateChatPnl.add(chooseAccountBox);
            privateChatPnl.validate();
            privateChatPnl.repaint();
        }
    }

    /**
     * Returns username of current user
     */
    public static String getCurrentAccountUsername() {
        return account.getUsername();
    }

    ////////////////////////////////////////////////////////////////////////
    ///////////////////////////ACTION LISTENERS////////////////////////////
    //////////The purpose of action listener is in it's name///////////////
    ///////////////////////////////////////////////////////////////////////
    protected class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = JOptionPane.showInputDialog("Enter Username:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            account = AccountServer.login(username, password);
            if (account != null) {
                greetingLabel.setText("Hello, " + username + "!");
                logoutPnl.setVisible(true);
                loginPnl.setVisible(false);
                startChat();
            }
        }
    }

    protected class CreateAccountActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = JOptionPane.showInputDialog("Enter Username:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            account = AccountServer.createAccount(username, password);
            if (account != null) {
                greetingLabel.setText("Hello, " + username + "!");
                logoutPnl.setVisible(true);
                loginPnl.setVisible(false);
                startChat();
            }
        }
    }

    protected class LogoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AccountServer.logout(account);
            logoutPnl.setVisible(false);
            chatRoomPnl.removeAll();
            chatRoomPnl.setVisible(false);
            privateChatPnl.removeAll();
            privateChatPnl.setVisible(false);
            privateChatAndRoomPnl.setVisible(false);
            chatMessagePnl.removeAll();
            chatPnl.removeAll();
            chatPnl.setVisible(false);
            loginPnl.setVisible(true);
            account = null;
            chatRoomList = null;
            chatButtonList = null;
            privateChatButtonList = null;
            JOptionPane.showMessageDialog(null, "You are logged out.", "Logout Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected static class JoinChatRoomActionListener implements ActionListener {

        private ChatRoom chatRoom;

        public JoinChatRoomActionListener(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Once chat was chosen then it's button should be disabled
            for (JButton jButton : chatButtonList) {
                jButton.setEnabled(true);
                if (jButton.getText().equals(chatRoom.getId())) {
                    jButton.setEnabled(false);
                }
            }
            for (JButton jButton : privateChatButtonList) {
                jButton.setEnabled(true);
            }
            createChatUI(chatRoom);
        }
    }

    protected static class SendMessageActionListener implements ActionListener {

        private IChat chat;
        private JTextField yourMessage;

        public SendMessageActionListener(IChat chat, JTextField yourMessage) {
            this.chat = chat;
            this.yourMessage = yourMessage;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (chat instanceof ChatRoom) {
                ChatRoomServer.sendMessage(chat.getId(), account.getUsername(), yourMessage.getText());
            } else {
                PrivateChatRoomServer.sendMessage(chat.getId(), account.getUsername(), yourMessage.getText());
            }
            yourMessage.setText("");
            sendMessageButton.setEnabled(false);
        }
    }

    protected static class CloseChatActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            chatPnl.removeAll();
            chatPnl.setVisible(false);
            for (JButton jButton : chatButtonList) {
                jButton.setEnabled(true);
            }
            for (JButton jButton : privateChatButtonList) {
                jButton.setEnabled(true);
            }
        }
    }

    protected static class AddChatActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = JOptionPane.showInputDialog("Enter title of the new Chat.");
            ChatRoomServer.createChatRoom(title, account.getUsername());
        }
    }

    protected static class DeleteChatActionListener implements ActionListener {
        private String title;

        public DeleteChatActionListener(String title) {
            this.title = title;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int indexToDelete = -1;
            boolean isPrivate = false;
            for (ChatRoom chatRoom : chatRoomList) {
                if (chatRoom.getId().equals(title)) {
                    indexToDelete = chatRoomList.indexOf(chatRoom);
                    break;
                }
            }
            if (indexToDelete == -1) {
                for (PrivateChatRoom privateChatRoom : privateChatRoomList) {
                    if (privateChatRoom.getId().equals(title)) {
                        indexToDelete = privateChatRoomList.indexOf(privateChatRoom);
                        break;
                    }
                }
                isPrivate = true;
            }
            if (indexToDelete >= 0) {
                if (isPrivate) {
                    privateChatRoomList.remove(indexToDelete);
                    PrivateChatRoomServer.updatePrivateChatRoomList(privateChatRoomList);
                } else {
                    chatRoomList.remove(indexToDelete);
                    ChatRoomServer.updateChatRoomList(chatRoomList);
                }
                chatPnl.setVisible(false);
            }
        }
    }

    protected static class StartPrivateChatActionListener implements ActionListener {
        private PrivateChatRoom privateChatRoom;

        public StartPrivateChatActionListener(PrivateChatRoom privateChatRoom) {
            this.privateChatRoom = privateChatRoom;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Once private chat room was chosen then it's button should be disabled
            for (JButton jButton : privateChatButtonList) {
                jButton.setEnabled(true);
                if (jButton.getText().equals(privateChatRoom.getId())) {
                    jButton.setEnabled(false);
                }
            }
            for (JButton jButton : chatButtonList) {
                jButton.setEnabled(true);
            }
            createChatUI(privateChatRoom);
        }
    }

    protected static class AddPrivateChatRoomActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] onlineAccounts = AccountServer.getOnlineAccountsArray(account.getUsername());
            chooseAccountBox = new JComboBox<>(onlineAccounts);
            privateChatPnl.add(chooseAccountBox);
            chooseAccountButton = new JButton("Ok");
            privateChatPnl.add(chooseAccountButton);
            chooseAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PrivateChatRoomServer.createPrivateChatRoom(account.getUsername() + "-"
                                    + chooseAccountBox.getSelectedItem().toString(), account.getUsername(),
                            chooseAccountBox.getSelectedItem().toString());
                }
            });
            privateChatPnl.validate();
            privateChatPnl.repaint();
        }
    }
}
