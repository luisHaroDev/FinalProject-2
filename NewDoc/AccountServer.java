package com.edu.chat.server;

import com.edu.chat.model.Account;
import com.edu.chat.model.AccountStatus;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.edu.chat.client.Client.*;

/**
 * Server that works with Account
 */
public class AccountServer {

    //path to file where all data is saved
    public static final String ACCOUNT_DATA_FILE_PATH = "resources/accounts.txt";
    //map with key Username and value AccountStatus, it is useful while searching for online/offline users
    public static Map<String, AccountStatus> accountStatusMap = new HashMap<>();
    //property to save time (in time millis) when ACCOUNT_DATA_FILE_PATH was last time modified
    private static long lastAccountsFileCheck = System.currentTimeMillis();

    /**
     * Get Online accounts ignoring logged in user
     * @param username that should be ignored
     * @return list of Online accounts ignoring logged in user
     */
    public static List<String> getOnlineAccountsList(String username) {
        List<String> onlineAccounts = new ArrayList<>();
        for (Map.Entry<String, AccountStatus> accountEntry : accountStatusMap.entrySet()) {
            if (!accountEntry.getKey().equals(username) && accountEntry.getValue().equals(AccountStatus.ONLINE)) {
                onlineAccounts.add(accountEntry.getKey());
            }
        }
        return onlineAccounts;
    }

    /**
     * Get Online accounts ignoring logged in user
     * @param username that should be ignored
     * @return array of Online accounts ignoring logged in user
     */
    public static String[] getOnlineAccountsArray(String username) {
        List<String> onlineAccounts = getOnlineAccountsList(username);
        String[] array = new String[onlineAccounts.size()];
        return onlineAccounts.toArray(array);
    }


    /**
     * Processes login operation:
     *      loads all available accounts from database;
     *      checks whether account with such username exists (if no then notify user nad stop login);
     *      checks whether password is correct (if no then notify user nad stop login);
     *      updates Account from loaded list with ONLINE status;
     *      updates database with new Account data;
     *      return Account;
     *
     * @param username entered username
     * @param password entered password
     * @return account with ONLINE status or null
     */
    public static Account login(String username, String password) {
        Map<String, Account> accounts = loadAccounts();
        if (accounts.keySet().contains(username)) {
            if (!accounts.get(username).getPassword().equals(password)) {
                JOptionPane.showMessageDialog(null, "Password is not correct.", "Login Error", JOptionPane.WARNING_MESSAGE);
            }
            else {
                accounts.get(username).setStatus(AccountStatus.ONLINE);
                updateAccounts(accounts);
                JOptionPane.showMessageDialog(null, "You are logged in." , "Login Success", JOptionPane.INFORMATION_MESSAGE);
                return accounts.get(username);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Account with username: " + username +
                    " does not exist.", "Login Error", JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }

    /**
     * Processes create account operation:
     *      loads all available accounts from database;
     *      checks whether account with such username exists (if yes then notify user nad stop login);
     *      creates account with input data and status ONLINE;
     *      save Account to databse;
     *      return Account;
     *
     * @param username entered username
     * @param password entered password
     * @return created account with ONLINE status or null
     */
    public static Account createAccount(String username, String password) {
        Map<String, Account> accounts = loadAccounts();
        if (!accounts.keySet().contains(username)) {
            Account account = new Account(username, password, AccountStatus.ONLINE);
            saveAccount(account);
            JOptionPane.showMessageDialog(null, "Account is created." ,
                    "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            return account;
        } else {
            JOptionPane.showMessageDialog(null, "Account with username: " + username +
                    " already exists.", "Registration Error", JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }

    /**
     * Processes save account operation:
     *      gets file by the path ACCOUNT_DATA_FILE_PATH;
     *      appends Account from parameter to end of the file;
     *      close the file;
     * @param account to save
     */
    public static void saveAccount(Account account) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNT_DATA_FILE_PATH, true))) {
            bw.append(account.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes update accounts (al accounts) operation:
     *      gets file by the path ACCOUNT_DATA_FILE_PATH;
     *      write all Account from parameter to the file (replace);
     *      close the file;
     * @param accounts that should be use for update
     */
    public static void updateAccounts(Map<String, Account> accounts) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNT_DATA_FILE_PATH))) {
            StringBuilder sb = new StringBuilder();
            for (Account value : accounts.values()) {
                sb.append(value.toString());
            }
            bw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes load accounts operation:
     *    gets file by the path ACCOUNT_DATA_FILE_PATH;
     *    reads each line from file;
     *    splits line by separator ';';
     *    initializes map with Accounts;
     *    closes file.
     *
     * Note. Data in file obeys the format:
     * username;password;status;
     *
     * @return
     */
    public static Map<String, Account> loadAccounts() {
        Map<String, Account> accounts = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                //username and password are seporated by ;
                String[] line = st.split(";");
                accounts.put(line[0].trim(), new Account(line[0].trim(), line[1].trim(),
                        AccountStatus.valueOf(line[2].trim().toUpperCase())));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (accountStatusMap == null) {
            accountStatusMap = new HashMap<>();
        }
        for (Account value : accounts.values()) {
            accountStatusMap.put(value.getUsername(), value.getStatus());
        }
        return accounts;
    }

    /**
     * Processes logout operation:
     *      loads all accounts from database;
     *      finds necessary account;
     *      sets status OFFLINE;
     *      updates accounts in database;
     *      closes file.
     *
     * @param account to log out
     */
    public static void logout(Account account) {
        Map<String, Account> accountsMap = loadAccounts();
        accountsMap.get(account.getUsername()).setStatus(AccountStatus.OFFLINE);
        updateAccounts(accountsMap);
    }

    /**
     * Looks for accounts updates in database.
     * Creates Runnable object checker. Sends to executes in separate thread while program is running (while true loop).
     */
    public static void lookForAccountsUpdate() {
        Runnable checker = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Map<String, Account> updatedAccountList = null;
                    while(updatedAccountList == null)
                    {
                        File file = new File(ACCOUNT_DATA_FILE_PATH);
                        if (file.lastModified() > lastAccountsFileCheck) {
                            System.out.println("File ACCOUNTS updates are founded.");
                            updatedAccountList = loadAccounts();
                            lastAccountsFileCheck = file.lastModified();
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //Check whether any user is changed his status and notify
                    for (Account account : updatedAccountList.values()) {
                        if (accountStatusMap.keySet().contains(account.getUsername())) {
                            if (!accountStatusMap.get(account.getUsername()).equals(account.getStatus()) &&
                                    !account.getUsername().equals(getCurrentAccountUsername())) {
                                JOptionPane.showMessageDialog(null, "User " +
                                                account.getUsername() + " is " + account.getStatus().getValue() +
                                                " now.", "Account Info",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    //Update map with account info with fresh data
                    accountStatusMap.clear();
                    for (Account updatedAccount : updatedAccountList.values()) {
                        accountStatusMap.put(updatedAccount.getUsername(), updatedAccount.getStatus());
                    }
                    refreshAccountComboBox();
                }
            }
        };
        Executors.newSingleThreadExecutor().execute(checker);
    }

}
