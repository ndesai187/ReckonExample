package com.mkyong.rest;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import com.mkyong.rest.MyAccounts.*;
import com.mkyong.rest.OBPObjects.AccountById;
import com.mkyong.rest.OBPObjects.ResponseAccountById;
import com.mkyong.rest.Objects.DepositAccounts.CASAAccountList;
import com.mkyong.rest.Objects.DepositAccounts.DepositAccounts;
import com.mkyong.rest.Objects.DepositAccounts.GetDepositAccount;
import com.mkyong.rest.Objects.DepositAccounts.SubCategoryList;
import com.mkyong.rest.Objects.ForexRate;
import com.mkyong.rest.Objects.Forexrates;
import com.mkyong.rest.Objects.GetRate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;
@Path("/")
public class JSONService {

	final static String BaseUrl = "https://apisandbox.openbankproject.com/obp/v2.0.0";
	final static String DefaultUser="bennettzhou1";
	private static TestUser testuser = new TestUser();
	private static Map<String, String> BANKS = new HashMap<String, String>();
	private static Map<String, OAuth10aService> serviceMap = new HashMap<String, OAuth10aService>();
	private static Map<String, OAuthRequest> requestMap = new HashMap<String, OAuthRequest>();

	public JSONService() {
		getBanks();
	}

	@GET
	@Path("/get")
	@Produces("application/json")
	public Product getProductInJSON() {

		Product product = new Product();
		product.setName("ING Helllo World!");
		product.setQty(12345);
		
		return product; 

	}

	@GET
	@Path("/getAccountById")
	@Produces("application/json")
	public ResponseAccountById getAccountById(@QueryParam("user_name") String user_name, @QueryParam("bank") String bank, @QueryParam("account") String account) {
		String message = getResponse(user_name,BaseUrl+"/my/banks/"+bank+"/accounts/"+account+"/account");
		Gson gson = new Gson();
		AccountById obj = gson.fromJson(message, AccountById.class);
		ResponseAccountById reply = new ResponseAccountById();

		reply.setAmount(obj.getBalance().getAmount());
		reply.setBank_fullname(BANKS.get(obj.getBank_id()));
		reply.setCurrency(obj.getBalance().getCurrency());
		reply.setDisplayname(obj.getOwners()[0].getDisplay_name());
		reply.setId(obj.getId());
		reply.setNumber(obj.getNumber());

		return reply;
	}

	@GET
	@Path("/getBanks")
	@Produces("application/json")
	public ResponseBanks getBanks() {
		String message = getResponse(null,BaseUrl+"/banks");
		Gson gson = new Gson();
		AllBanks obj = gson.fromJson(message, AllBanks.class);
		ResponseBanks reply = new ResponseBanks();
		ArrayList<String> bankList= new ArrayList<String>();
		for(Banks banks: obj.getBanks()){
			bankList.add(banks.getId()+"<===>"+banks.getFull_name());
			BANKS.put(banks.getId(), banks.getFull_name());
		}
		reply.setBanks(bankList);
		return reply;
	}

	@GET
	@Path("/getMyAccounts")
	@Produces("application/json")
	//http://localhost:8080/ReckonINGExample/getMyAccounts?user_name=bennettzhou1
	public ResponseMyAccounts getMyAccounts(@QueryParam("user_name")String user_name) {

		String message = getResponse(user_name,BaseUrl+"/my/accounts");
		Gson gson = new Gson();
		Type type = new TypeToken<List<MyAccounts>>() {}.getType();
		List<MyAccounts> myAccountsList = gson.fromJson(message, type);

		ResponseMyAccounts reply = new ResponseMyAccounts();
		int count = 0;
		ArrayList<ResponseAccountById> accounts = new ArrayList<ResponseAccountById>();
		for(MyAccounts myAccount: myAccountsList){
			count++;
			try {
				ResponseAccountById accountById = getAccountById(user_name, myAccount.getBank_id(), myAccount.getId());
				if (user_name.equals(accountById.getDisplayname())) {
					accounts.add(accountById);
				}
			}catch(Exception e){
				count--;
			}
		}
		reply.setNumOfAccounts(String.valueOf(count));
		reply.setDisplayname(user_name);
		reply.setAccountList(accounts);

		return reply;
	}


	@GET
	@Path("/getRate")
	@Produces("application/json")
	public GetRate getOcbcRateInJSON(){
		String token = "c7247669ab0e90237f1478aa8eebf4e8";

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(
					"https://api.ocbc.com:8243/Forex/1.0");
			getRequest.addHeader("accept", "application/json");
			getRequest.addHeader("Authorization", "Bearer "+token);


			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String stringMessage = "";
			String output;
			System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {

				System.out.println(output);
				stringMessage = output;
			}

			Gson gson = new Gson();
			Forexrates obj = gson.fromJson(stringMessage, Forexrates.class);
			System.out.println(obj);

			GetRate rate = new GetRate();
			for (ForexRate a:obj.getForexRates()) {
				if(a.getFromCurrency().equals("SGD") && a.getToCurrency().equals("USD")){
					   rate.setFromCurrency("SGD");
					   rate.setToCurrency("USD");
					   rate.setRate(a.getBankBuyingRateTT());
				}
			}

			httpClient.getConnectionManager().shutdown();
			return rate;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	       return null;
	}

	@POST
	@Path("/post")
	@Consumes("application/json")
	public Response createProductInJSON(Product product) {

		String result = "Product created : " + product;
		return Response.status(201).entity(result).build();
		
	}

	@GET
	@Path("/callback")
	@Produces("application/json")
	public void getCallback(@QueryParam("oauth_token") String oauth_token, @QueryParam("oauth_verifier") String oauth_verifier) {

		System.out.println("callback");
		System.out.println(oauth_token);
		System.out.println(oauth_verifier);

	}

	@GET
	@Path("/getDepositAccounts")
	@Produces("application/json")
	public GetDepositAccount getDepositAccounts(@QueryParam("subCategory") String subCategory, @QueryParam("productName") String productName, @QueryParam("benefit") String benefit ){
		String token = "c7247669ab0e90237f1478aa8eebf4e8";
		DefaultHttpClient httpClient= new DefaultHttpClient();
		try {
			HttpGet getRequest = new HttpGet(
					"https://api.ocbc.com:8243/Deposit_Accounts/1.0");
			getRequest.addHeader("accept", "application/json");
			getRequest.addHeader("Authorization", "Bearer "+token);


			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String stringMessage = "";
			String output;
			System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {

				System.out.println(output);
				stringMessage = output;
			}

			Gson gson = new Gson();
			DepositAccounts obj = gson.fromJson(stringMessage, DepositAccounts.class);
			System.out.println(obj);

			GetDepositAccount account = new GetDepositAccount();
			for (CASAAccountList a:obj.getCASAAccountList()) {
				if( a.getCategoryName()!=null){
					account.setCategoryName(a.getCategoryName());
					for(SubCategoryList s: a.getSubCategoryList()) {
						if(subCategory!=null && s.getSubCategoryName().equals(subCategory) || subCategory==null){
							account.setSubCategoryName(s.getSubCategoryName());
							for (com.mkyong.rest.Objects.DepositAccounts.Product p: s.getProduct()){
								if(productName!=null && p.getProductName().equals(productName) || productName==null) {
									account.setProductName(p.getProductName());
									if (benefit != null && p.getBenefits().equals(benefit) || benefit == null) {
										account.setBenefits(p.getBenefits());
										System.out.println(account.toString());
										return account;
									}
								}
							}
						}

					}

				}
			}


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			httpClient.getConnectionManager().shutdown();

		}

		return null;
	}

	public String getResponse(String user, String url) {
		String message="";
		if(user==null)
			user=DefaultUser;

		try {
			if (requestMap.get(user + url) == null) {
				initiateRequest(user, url);
			}

			com.github.scribejava.core.model.Response response=serviceMap.get(user).execute(requestMap.get(user+url));

			System.out.println("Got it! Lets see what we found...");
			System.out.println(response.getCode());
			System.out.println(response.getBody());
			message = response.getBody();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	private static void initiateRequest(String user, String url) throws IllegalAccessException, InstantiationException {

		ArrayList<String> keylist = testuser.getKeys().get(user);
		if(serviceMap.get(user)==null) {
			serviceMap.put(user, new ServiceBuilder()
					.apiKey(keylist.get(0))
					.apiSecret(keylist.get(1))
					.callback("oob")
					.build(OBPApi.instance()));
		}

		OAuth1AccessToken accessToken = new OAuth1AccessToken(keylist.get(2), keylist.get(3), keylist.get(4));
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		serviceMap.get(user).signRequest(accessToken, request);
		requestMap.put(user+url, request);
	}


	public static void main(String[] args) {

		//getMyAccounts("bennettzhou1");

		/*
		String message = getResponse(BaseUrl+"/my/banks/rbs/accounts/BenAccount_1/account");
		Gson gson = new Gson();
		AccountById obj = gson.fromJson(message, AccountById.class);
		System.out.println(obj.toString());
		*/
	}

/*
	public void onResume() {
		Uri uri = this.getIntent().getData();

		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			Log.d("OAuthTwitter", uri.toString());
			String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
			Log.d("OAuthTwitter", verifier);
			try {

				provider.retrieveAccessToken(consumer, verifier);
				ACCESS_KEY = consumer.getToken();
				ACCESS_SECRET = consumer.getTokenSecret();

				Log.d("OAuthTwitter", ACCESS_KEY);
				Log.d("OAuthTwitter", ACCESS_SECRET);

			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
		}


	public String auth() throws MalformedURLException, InterruptedException {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(authUrl));
		} catch (PartInitException couldNotOpenBrowser) {
			LinkedHashMap<String, String> btns = MapUtil.orderedMap();
			btns.put(Constants.Plugin_OAuth_Copy, Messages.Plugin_OAuth_Copy);
			btns.put(Constants.Plugin_OAuth_Cancel, Messages.Plugin_OAuth_Cancel);

			String opt = new SyncEclipseUtil().openCustomImageTypeWithCustomButtonsSyncly(shell, Messages.Plugin_OAuth_Title, Messages.Plugin_OAuth_DoItManually, new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(Constants.OAUTH_EVERNOTE_TRADEMARK)), btns);
			if (Constants.Plugin_OAuth_Copy.equals(opt)) {
				ClipboardUtil.copy(authUrl);
			} else {
				return StringUtils.EMPTY;
			}
		}

		// wait for callback handling
		synchronized (callback) {
			callback.wait(30 * 60 * 1000);// 30 minutes
		}

		String verifierValue = callback.getVerifier();
		if (StringUtils.isBlank(verifierValue)) {

	*/



}