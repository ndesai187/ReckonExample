package com.mkyong.rest;

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
    private static HashMap<String, OAuth10aService> serviceMap;
    private static HashMap<String, OAuthRequest> requestMap;

    public TestUser getTestuser() {
        return testuser;
    }

    public HashMap<String, String> getBANKS() {
        return BANKS;
    }

    public void setBANKS(HashMap<String, String> BANKS) {
        INGConstant.BANKS = BANKS;
    }

    public HashMap<String, OAuth10aService> getServiceMap() {
        if(serviceMap == null){
            serviceMap = new HashMap<String, OAuth10aService>();
        }
        return serviceMap;
    }

    public void setServiceMap(HashMap<String, OAuth10aService> serviceMap) {
        INGConstant.serviceMap = serviceMap;
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
}
