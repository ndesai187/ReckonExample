package com.mkyong.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bennettzhou on 24/04/2017.
 */
public class TestUser {

    private Map<String, String[]> keys;
    //Test user1
    final String user_name1="bennettzhou1";
    final String oauth_token1="E52YBZRPHLRV1UY2JKPKT4YTRG1ZMOZ0IV5KQSGL";
    final String oauth_token_secret1="TSH4M4NMCVHXKPMQHEAA33PM1YBPW3GEJVFUKOFX";
    final String rawResponse1="oauth_token="+oauth_token1+"&oauth_token_secret="+oauth_token_secret1;
    //Test user2
    final String user_name2="superhero";
    final String oauth_token2="KJZMBB1NL3DV3I3QYUHFUVSS2XTUHM20KNJ3ZNRL";
    final String oauth_token_secret2="M5ABGXZXNMYD4BBXEFAVPTHBRTUOFNHOJ1ONIYPY";
    final String rawResponse2="oauth_token="+oauth_token1+"&oauth_token_secret="+oauth_token_secret1;

    public TestUser(){
        keys = new HashMap<String, String[]>();
        keys.put(user_name1, new String[]{oauth_token1, oauth_token_secret1, rawResponse1});
        keys.put(user_name2, new String[]{oauth_token2, oauth_token_secret2, rawResponse2});
    }

    public Map<String, String[]> getKeys() {
        return keys;
    }
}
