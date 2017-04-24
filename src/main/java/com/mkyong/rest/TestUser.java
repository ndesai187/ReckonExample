package com.mkyong.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bennettzhou on 24/04/2017.
 */
public class TestUser {

    private Map<String, ArrayList<String>> keys;
    String user_name="bennettzhou1"; //Bennett0910!
    String apiKey="bf3hvn0fxgy1ikrjplhzyljjrwpyy2egomaztsga";
    String apiSecret="fbeiz303g1xdujaghu2qou5ebstmgy4c1tlaetut";
    String oauth_token="E52YBZRPHLRV1UY2JKPKT4YTRG1ZMOZ0IV5KQSGL";
    String oauth_token_secret="TSH4M4NMCVHXKPMQHEAA33PM1YBPW3GEJVFUKOFX";
    String rawResponse="oauth_token="+oauth_token+"&oauth_token_secret="+oauth_token_secret;

    String user_name1="superhero"; //Bennett0910!
    String apiKey1="f1y3h5r2sqn02jz2mu2gljfcl5nqh3nxz0jcclte";
    String apiSecret1="khtirqdmxqu5dmtmkk1fvuoayn11c21vqokmhcyc";
    String oauth_token1="KJZMBB1NL3DV3I3QYUHFUVSS2XTUHM20KNJ3ZNRL";
    String oauth_token_secret1="M5ABGXZXNMYD4BBXEFAVPTHBRTUOFNHOJ1ONIYPY";
    String rawResponse1="oauth_token="+oauth_token1+"&oauth_token_secret="+oauth_token_secret1;

    public TestUser(){
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> list1 = new ArrayList<String>();

        list.add(apiKey);
        list.add(apiSecret);
        list.add(oauth_token);
        list.add(oauth_token_secret);
        list.add(rawResponse);

        list1.add(apiKey1);
        list1.add(apiSecret1);
        list1.add(oauth_token1);
        list1.add(oauth_token_secret1);
        list1.add(rawResponse1);

        keys = new HashMap<String, ArrayList<String>>();
        keys.put(user_name, list);
        keys.put(user_name1, list1);
    }

    public Map<String, ArrayList<String>> getKeys() {
        return keys;
    }
}
