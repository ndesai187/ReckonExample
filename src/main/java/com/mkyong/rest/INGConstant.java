package com.mkyong.rest;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bennettzhou on 28/04/2017.
 */
public class INGConstant {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    final static String DefaultUser="bennettzhou1";
    final static String apiKey = "bf3hvn0fxgy1ikrjplhzyljjrwpyy2egomaztsga";
    final static String apiSecret = "fbeiz303g1xdujaghu2qou5ebstmgy4c1tlaetut";

    //this field contains the single instance every initialized.
    private static INGConstant ingConstant;

    private INGConstant () {
        //initialize
        log.info("INGConstant created...");
    }

    //this is the method to obtain the single instance
    public static INGConstant getInstance () {
        if (ingConstant == null) {
            synchronized (INGConstant.class) {
                if (ingConstant == null) {
                    ingConstant = new INGConstant();
                }
            }
        }
        return ingConstant;
    }

    final private static TestUser testuser = new TestUser();
    private static HashMap<String, String> BANKS;
    private static OAuth10aService service;
    private static HashMap<String, OAuthRequest> requestMap;
    private static HashMap<String, String> accountMap;

    public String getDefaultUser() {
        return DefaultUser;
    }

    public TestUser getTestuser() {
        return testuser;
    }

    public HashMap<String, String> getBANKS() {
        return BANKS;
    }

    public void setBANKS(HashMap<String, String> BANKS) {
        INGConstant.BANKS = BANKS;
    }

    public OAuth10aService getService() {
        if(service == null){
            log.info("Initiating Service...");
            service = new ServiceBuilder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .callback("oob")
                    .build(OBPApi.instance());
        }
        return service;
    }

    public HashMap<String, OAuthRequest> getRequestMap() {
        if(requestMap == null){
            requestMap = new HashMap<String, OAuthRequest>();
        }
        return requestMap;
    }

    public void setRequestMap(HashMap<String, OAuthRequest> requestMap) {
        INGConstant.requestMap = requestMap;
    }

    public HashMap<String, String> getAccountMap() {
        if(accountMap == null){
            accountMap = new HashMap<String, String>();
        }
        return accountMap;
    }

    public void setAccountMap(HashMap<String, String> accountMap) {
        INGConstant.accountMap = accountMap;
    }
}
