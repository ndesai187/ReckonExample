package com;

import com.JSONService;
import org.junit.Test;

/**
 * Created by bennettzhou on 05/04/2017.
 */
public class JTest {

    @Test
    /*
    public void TestRate() {

        JSONService j = new JSONService();
        GetRate rate = j.getOcbcRateInJSON();
        System.out.println(rate);

    }*/

    public void TestDepositAccounts() {

        JSONService j = new JSONService();
        j.getLoanDetails(100000, 10, 38, 2000, 8000, "ING");
        //j.getMyAccounts("superhero");
        //j.getMyTransactionHistory("superhero");
        //j.getBanks();
        //System.out.println("ING Account Balance>>>>>>>>>>>>: "+j.getAccountById("superhero", "at02-1465--01", "supering").getAmount());
        //System.out.println("HSBC Account Balance>>>>>>>>>>>>: "+j.getAccountById("superhero", "hsbc-test", "superhsbc").getAmount());
        //System.out.println("\n=>>>>>>>>>>Transmitting EUR100 from ING to HSBC<<<<<<<<<=");
        //j.createTransaction("superhero", "at02-1465--01", "supering", "10", "hsbc-test", "superhsbc");

        //System.out.println("ING Account Balance>>>>>>>>>>>>: "+j.getAccountById("superhero", "at02-1465--01", "supering").getAmount());
        //System.out.println("HSBC Account Balance>>>>>>>>>>>>: "+j.getAccountById("superhero", "hsbc-test", "superhsbc").getAmount());

        //j.getCurrentUser();

    }
}

