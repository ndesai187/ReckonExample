package com.OCBCObjects.DepositAccounts;

import java.util.ArrayList;

/**
 * Created by bennettzhou on 05/04/2017.
 */
public class DepositAccounts {

        private ArrayList<CASAAccountList> CASAAccountList;

        private String disclaimer;

    public ArrayList<com.OCBCObjects.DepositAccounts.CASAAccountList> getCASAAccountList() {
        return CASAAccountList;
    }

    public void setCASAAccountList(ArrayList<com.OCBCObjects.DepositAccounts.CASAAccountList> CASAAccountList) {
        this.CASAAccountList = CASAAccountList;
    }

    public String getDisclaimer ()
        {
            return disclaimer;
        }

        public void setDisclaimer (String disclaimer)
        {
            this.disclaimer = disclaimer;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [CASAAccountList = "+CASAAccountList+", disclaimer = "+disclaimer+"]";
        }

}
