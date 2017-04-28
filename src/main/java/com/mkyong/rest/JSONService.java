package com.mkyong.rest;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import com.mkyong.rest.MyAccounts.*;
import com.mkyong.rest.MyTransactions.MyTransactions;
import com.mkyong.rest.MyTransactions.ResponseMyTransactions;
import com.mkyong.rest.OBPObjects.AccountById;
import com.mkyong.rest.OBPObjects.ResponseAccountById;
import com.mkyong.rest.Objects.DepositAccounts.CASAAccountList;
import com.mkyong.rest.Objects.DepositAccounts.DepositAccounts;
import com.mkyong.rest.Objects.DepositAccounts.GetDepositAccount;
import com.mkyong.rest.Objects.DepositAccounts.SubCategoryList;
import com.mkyong.rest.Objects.ForexRate;
import com.mkyong.rest.Objects.Forexrates;
import com.mkyong.rest.Objects.GetRate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.concurrent.ExecutionException;
@Path("/")
public class JSONService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	final static String BaseUrl = "https://apisandbox.openbankproject.com/obp/v2.0.0";
	final static String DefaultUser="bennettzhou1";
	private static INGConstant INGCONSTANT = INGConstant.getInstance();

	public JSONService() {
		log.info("Initiating JSONService...");
		if(INGCONSTANT.getBANKS() == null){
			getBanks();
		}
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
	public ResponseAccountById getAccountById(@QueryParam("user_name") String user_name, @QueryParam("bank_id") String bank_id, @QueryParam("account") String account) {
		String message = getResponse(user_name,BaseUrl+"/my/banks/"+bank_id+"/accounts/"+account+"/account");
		Gson gson = new Gson();
		AccountById obj = gson.fromJson(message, AccountById.class);
		ResponseAccountById reply = new ResponseAccountById();

		reply.setAmount(obj.getBalance().getAmount());
		reply.setBank_fullname(INGCONSTANT.getBANKS().get(obj.getBank_id()));
		reply.setBank_id(obj.getBank_id());
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
		ResponseBanks reply = new ResponseBanks();
		ArrayList<String> bankList = new ArrayList<String>();
		if(INGCONSTANT.getBANKS()== null) {
			log.info("Creating Banking map...");
			HashMap<String, String> map = new HashMap<String, String>();
			String message = getResponse(null, BaseUrl + "/banks");
			Gson gson = new Gson();
			AllBanks obj = gson.fromJson(message, AllBanks.class);

			for (Banks banks : obj.getBanks()) {
				bankList.add(banks.getId() + ":" + banks.getFull_name());
				map.put(banks.getId(), banks.getFull_name());
			}
			INGCONSTANT.setBANKS(map);
		}else{
			for(String key : INGCONSTANT.getBANKS().keySet()){
				bankList.add(key + ":" + INGCONSTANT.getBANKS().get(key));
			}
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


	@GET
	@Path("/createTransaction/{user_name}")
	@Produces("application/json")
	public ResponseMyTransactions createTransaction(@PathParam("user_name") String user_name, @QueryParam("frombank_id")
			String frombank_id, @QueryParam("fromid") String fromid, @QueryParam("amount")
			String amount, @QueryParam("tobank_id") String tobank_id, @QueryParam("toid") String toid) {

		String urlTransaction = "https://apisandbox.openbankproject.com/obp/v2.0.0/banks/%s/accounts/%s/owner/transaction-request-types/SANDBOX_TAN/transaction-requests";
		String url = String.format(urlTransaction, frombank_id, fromid);
		String sendingEntity = "{\"to\":{\"bank_id\":\""+tobank_id+"\", \"account_id\":\""+toid+"\"}, \"value\":{\"currency\":\"EUR\", \"amount\":\"" + amount+"\"},  \"description\":\"Alexa voice transaction!\"}";

		String stringMessage = postResponse(user_name, url, sendingEntity);
		Gson gson = new Gson();
		MyTransactions obj = gson.fromJson(stringMessage, MyTransactions.class);
		ResponseMyTransactions reply = new ResponseMyTransactions();
		reply.setTransaction_ids(obj.getTransaction_ids());
		reply.setAccount_id(obj.getFrom().getAccount_id());
		reply.setBank_id(obj.getFrom().getBank_id());
		reply.setBank_fullname(INGCONSTANT.getBANKS().get(obj.getFrom().getBank_id()));
		reply.setStart_date(obj.getStart_date());
		reply.setEnd_date(obj.getEnd_date());
		reply.setStatus(obj.getStatus());
		//check balance
		ResponseAccountById AccountSummary = getAccountById(user_name, frombank_id, fromid);
		reply.setBalance(AccountSummary.getAmount());
		reply.setCurrency(AccountSummary.getCurrency());

		return reply;

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

	public String postResponse(String user, String url, String sendingEntity) {


		try {
			OAuthRequest request = initiateTransaction(user, url, sendingEntity);
			com.github.scribejava.core.model.Response response = INGCONSTANT.getServiceMap().get(user).execute(request);
			final String jsonString = response.getBody();
			final int statusCode = response.getCode();

			if ((statusCode == 200) || (statusCode == 201)) {

				log.info("\nTransaction response from OBP... ");
				log.info("status code: "+response.getCode());
				log.info("json body: "+response.getBody());
				return jsonString;
			} else {
				String statusDesc = response.getMessage();
				log.error("Error: " + statusCode + " : " + statusDesc);
				return "Response Null: " + statusCode + " : " + statusDesc;
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "ERROR!!!";
	}

	public String getResponse(String user, String url) {
		String message="";
		if(user==null)
			user=DefaultUser;

		try {
			if (INGCONSTANT.getRequestMap().get(user + url) == null) {
				log.info("Initiating Request for user+url="+user+url);
				OAuthRequest request = initiateRequest(user, url);
				HashMap<String, OAuthRequest> map = INGCONSTANT.getRequestMap();
				map.put(user + url, request);
				INGCONSTANT.setRequestMap(map);
			}
			OAuthRequest request = INGCONSTANT.getRequestMap().get(user+url);
			com.github.scribejava.core.model.Response response=INGCONSTANT.getServiceMap().get(user).execute(request);
			log.info("\nGet response from OBP...");
			log.info("status code: "+response.getCode());
			log.info("json body: "+response.getBody());
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

	private OAuthRequest initiateRequest(String user, String url) throws IllegalAccessException, InstantiationException {
		OAuth1AccessToken token = getAccessToken(user);
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		INGCONSTANT.getServiceMap().get(user).signRequest(token, request);
		return(request);
	}

	private OAuthRequest initiateTransaction(String user, String url, String sendingEntity) throws IllegalAccessException, InstantiationException, InterruptedException, ExecutionException, IOException {
		OAuth1AccessToken token = getAccessToken(user);
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-Type", "application/json");
		request.setPayload(sendingEntity);

		System.out.println("\nPosting urlString: " + url);
		System.out.println("SendingEntity: '" + sendingEntity + "'");
		INGCONSTANT.getServiceMap().get(user).signRequest(token, request);
		return request;
	}

	private OAuth1AccessToken getAccessToken(String user){
		ArrayList<String> keylist = INGCONSTANT.getTestuser().getKeys().get(user);
		if(INGCONSTANT.getServiceMap().get(user) == null) {
			log.info("Initiating Service for user="+user);
			HashMap<String, OAuth10aService> map = INGCONSTANT.getServiceMap();
			map.put(user, new ServiceBuilder()
					.apiKey(keylist.get(0))
					.apiSecret(keylist.get(1))
					.callback("oob")
					.build(OBPApi.instance()));
			INGCONSTANT.setServiceMap(map);
		}
		return(new OAuth1AccessToken(keylist.get(2), keylist.get(3), keylist.get(4)));
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


}