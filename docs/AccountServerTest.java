package com.edu.chat.server;

import com.edu.chat.model.Account;
import com.edu.chat.model.AccountStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for AccountServer
 */
public class AccountServerTest {
    //list to save state of account data before test
    Map<String, Account> accountsBeforeTest;

    /**
     * Save state before test
     */
    @Before
    public void before(){
        accountsBeforeTest = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AccountServer.ACCOUNT_DATA_FILE_PATH))) {
            String st;
            while ((st = br.readLine()) != null) {
                //username and password are seporated by ;
                String[] line = st.split(";");
                accountsBeforeTest.put(line[0].trim(), new Account(line[0].trim(), line[1].trim(),
                        AccountStatus.valueOf(line[2].trim().toUpperCase())));
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AccountServer.ACCOUNT_DATA_FILE_PATH))) {
            StringBuilder sb = new StringBuilder();
            for (Account value : accountsBeforeTest.values()) {
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
     * Test #loadAccounts() with not empty data file
     */
    @Test
    public void testLoadAccounts(){
        //load test data
        String username = "username", password = "password";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AccountServer.ACCOUNT_DATA_FILE_PATH, true))) {
            bw.append(username + ";" + password + ";" + AccountStatus.OFFLINE + ";\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //test
        Map<String, Account> accounts = AccountServer.loadAccounts();

        //check results
        Assert.assertNotNull(accounts);
        Assert.assertFalse(accounts.isEmpty());
        Assert.assertEquals(accountsBeforeTest.size() + 1, accounts.size());
        Assert.assertEquals(username, accounts.get(username).getUsername());
        Assert.assertEquals(password, accounts.get(username).getPassword());
        Assert.assertEquals(AccountStatus.OFFLINE, accounts.get(username).getStatus());
    }

    /**
     * Test for #saveAccount(Account)
     */
    @Test
    public void testSaveAccount(){
        //save amount of accounts before creating new one
        int accountsSizeBefore = AccountServer.loadAccounts().size();

        //test
        String username = "username", password = "password";
        Account account = new Account(username, password, AccountStatus.ONLINE);
        AccountServer.saveAccount(account);

        //check results
        Map<String, Account> accountsAfter = AccountServer.loadAccounts();
        int accountsSizeAfter = accountsAfter.size();
        Assert.assertTrue(accountsSizeAfter - accountsSizeBefore == 1);
        Assert.assertEquals(username, accountsAfter.get(username).getUsername());
        Assert.assertEquals(password, accountsAfter.get(username).getPassword());
        Assert.assertEquals(AccountStatus.ONLINE, accountsAfter.get(username).getStatus());
    }

    /**
     * Test for #updateAccounts(Map)
     */
    @Test
    public void testUpdateAccounts(){
        //save amount of accounts before creating one
        int accountsSizeBefore = AccountServer.loadAccounts().size();

        //prepare test data
        Map<String, Account> accountsToCreate = new HashMap<>();
        int newAccounts = 2;
        for(int i = 1 ; i <= newAccounts; i++){
            accountsToCreate.put("username_" + i, new Account("username_" + i, "password_" + i,
                    AccountStatus.OFFLINE));
        }
        accountsToCreate.putAll(accountsBeforeTest);

        //test
        AccountServer.updateAccounts(accountsToCreate);

        //check results
        Map<String, Account> accountsAfter = AccountServer.loadAccounts();
        int accountsSizeAfter = accountsAfter.size();
        Assert.assertTrue(accountsSizeAfter - accountsSizeBefore == newAccounts);
        for(int i = 1 ; i <= newAccounts; i++){
            Assert.assertEquals("username_" + i, accountsAfter.get("username_" + i).getUsername());
            Assert.assertEquals("password_" + i, accountsAfter.get("username_" + i).getPassword());
            Assert.assertEquals(AccountStatus.OFFLINE, accountsAfter.get("username_" + i).getStatus());
        }
    }

    /**
     * Test #logout(Account)
     */
    @Test
    public void testLogout(){
        //prepare test data
        String username = "username", password = "password";
        Account account = new Account(username, password, AccountStatus.ONLINE);
        AccountServer.saveAccount(account);

        //test
        AccountServer.logout(account);

        //check results
        Account accountAfter = AccountServer.loadAccounts().get(username);
        Assert.assertNotNull(accountAfter);
        Assert.assertEquals(account.getUsername(), accountAfter.getUsername());
        Assert.assertEquals(account.getPassword(), accountAfter.getPassword());
        Assert.assertEquals(AccountStatus.OFFLINE, accountAfter.getStatus());
    }

    /**
     * Test #getOnlineAccountsList(String)
     */
    @Test
    public void testGetOnlineAccountsList(){
        String username = "username", password = "password", anotherUsername = "anotherUsername";
        //save amount of online accounts before test
        AccountServer.loadAccounts();
        int onlineAccBefore = AccountServer.getOnlineAccountsList(anotherUsername).size();
        //prepare test data
        AccountServer.saveAccount(new Account(username, password, AccountStatus.ONLINE));

        //test
        AccountServer.loadAccounts();
        List<String> accounts = AccountServer.getOnlineAccountsList(anotherUsername);

        //check results
        Assert.assertNotNull(accounts);
        Assert.assertFalse(accounts.isEmpty());
        Assert.assertEquals(onlineAccBefore + 1, accounts.size());
        Assert.assertEquals(username, accounts.get(onlineAccBefore));
    }
}
