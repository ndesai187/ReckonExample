package com;

import com.Auth.APIAccessHandler;
import com.JSONResponse.*;
import com.MyAccounts.*;
import com.MyAccounts.ResponseBanks;
import com.OCBCObjects.Product;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.LoanCalculator.LoanDetails;
import com.MyTransactions.MyTransactions;
import com.MyTransactions.ResponseMyTransactions;
import com.OBPObjects.AccountById;
import com.OBPObjects.ResponseAccountById;
import com.OCBCObjects.DepositAccounts.CASAAccountList;
import com.OCBCObjects.DepositAccounts.DepositAccounts;
import com.OCBCObjects.DepositAccounts.GetDepositAccount;
import com.OCBCObjects.DepositAccounts.SubCategoryList;
import com.OCBCObjects.ForexRate;
import com.OCBCObjects.Forexrates;
import com.OCBCObjects.GetRate;
import com.TransactionHistById.ResponseTransactionhistById;
import com.TransactionHistById.TransactionHistBean;
import com.TransactionHistById.TransactionHistById;
import com.TransactionHistById.Transactions;
import com.Utils.CacheUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.concurrent.ExecutionException;
@Path("/")
public class JSONService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	APIAccessHandler session = new APIAccessHandler();
	final static String BaseUrl = "https://apisandbox.openbankproject.com/obp/v2.0.0";
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
	@Path("/welcome")
	@Produces("application/json")
	public String sayHiJSON() {

		String hi = "Welcome to new Reckoning";
		return hi;

	}

	@GET
	@Path("/hello")
	@Produces("application/json")
	public MessageResponse sayHelloJSON() {

		MessageResponse hello = new MessageResponse();
		hello.setMessage("Hello, I am online!");

		return hello;

	}

	@GET
	@Path("/initiate")
	@Produces("application/json")
	public AuthRequestResponse goToLoginPage() {
		AuthRequestResponse loginPage = new AuthRequestResponse();

		loginPage.setLoginURL(session.getLoginPage());
		if (loginPage.getLoginURL().equals(null)) {
			loginPage.setStatus("Failure");
		} else {
			loginPage.setStatus("Success");
		}
		return loginPage;
	}

	@GET
	@Path("/callBack")
	@Produces("application/json")
	public MessageResponse welcomePage(
			@QueryParam("oauth_token") String auth_token,
			@QueryParam("oauth_verifier") String auth_verifier) {

		MessageResponse welcomeText = new MessageResponse();
		String status = session.fetchAccessToken(auth_token, auth_verifier);
		if (status.equals("success")) {
			welcomeText.setMessage("you have successfully logged on! but I don't have anything for you now :)");
		} else {
			welcomeText.setMessage("Something went wrong here!" + "method : welcomePage");
		}
		return welcomeText;
	}

	@GET
	@Path("/userInfo")
	@Produces("application/json")
	public MessageResponse getUserDetails(
			@QueryParam("user_name") String username) {

		MessageResponse testMessage = new MessageResponse();
		com.github.scribejava.core.model.Response message = session.getAPIResponse(username, BaseUrl + "/users/current");
		try {
			testMessage.setMessage(message.getCode() + message.getBody() + message.getHeaders() + message.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testMessage;

	}

	@GET
	@Path("/getTransactionHistoryById")
	@Produces("application/json")
	public ResponseTransactionhistById getTransactionHistoryById(@QueryParam("user_name") String user_name, @QueryParam("bank_id") String bank_id, @QueryParam("account") String account) {
		String message = getResponse(user_name,BaseUrl+"/my/banks/"+bank_id+"/accounts/"+account+"/transactions");
		Gson gson = new Gson();
		TransactionHistById obj = gson.fromJson(message, TransactionHistById.class);
		ResponseTransactionhistById reply = new ResponseTransactionhistById();
		ArrayList<TransactionHistBean> transactionList = new ArrayList<TransactionHistBean>();

		for(Transactions tranx : obj.getTransactions()){
			transactionList.add(new TransactionHistBean(tranx));
		}
		reply.setNumOfTranx(String.valueOf(transactionList.size()));
		reply.setDisplayname(user_name);
		reply.setTransactionList(transactionList);
		return reply;
	}

	@GET
	@Path("/getMyTransactionHistory")
	@Produces("application/json")
	public ResponseTransactionhistById getMyTransactionHistory(@QueryParam("user_name")String user_name) {

		String message = getResponse(user_name,BaseUrl+"/my/accounts");
		Gson gson = new Gson();
		Type type = new TypeToken<List<MyAccounts>>() {}.getType();
		List<MyAccounts> myAccountsList = gson.fromJson(message, type);

		ResponseTransactionhistById reply = new ResponseTransactionhistById();
		Map<String, TransactionHistBean> AllMyHistory = new HashMap<String, TransactionHistBean>();
		for(MyAccounts myAccount: myAccountsList){
			try {
				ResponseTransactionhistById transactionHist =
						getTransactionHistoryById(user_name, myAccount.getBank_id(), myAccount.getId());
				for(TransactionHistBean bean : transactionHist.getTransactionList()){
					AllMyHistory.put(bean.getTransactionId() + ":" +bean.getCompletedDateTime(), bean);
				}
			}catch(Exception e){
				log.error(e.toString());
			}
		}

		List<String> datelist = new ArrayList<String>(AllMyHistory.keySet());
		Collections.sort(datelist, new Comparator<String>() {
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
			@Override
			public int compare(String o1, String o2) {
				try {
					return f.parse(o2.substring(o2.indexOf(":")+1)).compareTo(f.parse(o1.substring(o1.indexOf(":")+1)));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});

		ArrayList<TransactionHistBean> AllMyHistoryList = new ArrayList<TransactionHistBean>();
		for(String a: datelist){
			AllMyHistoryList.add(AllMyHistory.get(a));
		}

		reply.setNumOfTranx(String.valueOf(AllMyHistoryList.size()));
		reply.setDisplayname(user_name);
		reply.setTransactionList(AllMyHistoryList);
		return reply;
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
		reply.setBank_shortname(INGCONSTANT.getBanksShortName().get(obj.getBank_id()));
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
			//String message = getResponse(null, BaseUrl + "/banks");
			com.github.scribejava.core.model.Response message = session.getAPIResponse("", BaseUrl + "/banks");
			Gson gson = new Gson();
			AllBanks obj = null;
			try {
				obj = gson.fromJson(message.getBody(), AllBanks.class);
			} catch (IOException e) {
				e.printStackTrace();
			}

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
		ArrayList<ResponseAccountById> accounts = new ArrayList<ResponseAccountById>();
		for(MyAccounts myAccount: myAccountsList){
			try {
				ResponseAccountById accountById = getAccountById(user_name, myAccount.getBank_id(), myAccount.getId());
				if(INGCONSTANT.getAccountMap().get(accountById.getNumber()) == null){
					HashMap<String, String> map = INGCONSTANT.getAccountMap();
					map.put(accountById.getNumber(), accountById.getId());
					INGCONSTANT.setAccountMap(map);
				}
				if (user_name.equals(accountById.getDisplayname())) {
					accounts.add(accountById);
				}
			}catch(Exception e){
				log.error(e.toString());
			}
		}
		reply.setNumOfAccounts(String.valueOf(accounts.size()));
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
		reply.setBank_shortname(INGCONSTANT.getBanksShortName().get(obj.getFrom().getBank_id()));
		reply.setStart_date(obj.getStart_date());
		reply.setEnd_date(obj.getEnd_date());
		reply.setStatus(obj.getStatus());
		//check balance
		ResponseAccountById AccountSummary = getAccountById(user_name, frombank_id, fromid);
		reply.setBalance(AccountSummary.getAmount());
		reply.setCurrency(AccountSummary.getCurrency());

		return reply;

	}

	@GET
	@Path("/loan-calculator")
	@Produces("application/json")
	public LoanDetails getLoanDetails(@QueryParam("loanAmount") double loanAmount, @QueryParam("numYears") int numYears,
									  @QueryParam("currentAge") int currentAge, @QueryParam("totalMonthlyDebt") double totalMonthlyDebt,
									  @QueryParam("totalMonthlyIncome") double totalMonthlyIncome, @QueryParam("LoanType") String LoanType) {
		String url = "https://myloan-calculator.herokuapp.com/loan-calculator";
		String payload = "{" +
				"\"loanAmount\":"+loanAmount+"," +
				"\"numYears\":"+numYears+"," +
				"\"currentAge\":"+currentAge+"," +
				"\"totalMonthlyDebt\":"+totalMonthlyDebt+"," +
				"\"totalMonthlyIncome\":"+totalMonthlyIncome+"," +
				"\"type\":\""+LoanType+"\"" +
				"}";
		Gson gson = new Gson();
		LoanDetails loanDetail = gson.fromJson(postJsonRequest(payload, url), LoanDetails.class);
		return loanDetail;
	}

	public String postJsonRequest(String jsonString, String url)
	{
		HttpClient httpClient = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		try {
			HttpPost postRequest = new HttpPost(url);
			postRequest.setHeader("Content-type", "application/json");
			StringEntity entity = new StringEntity(jsonString);

			postRequest.setEntity(entity);

			long startTime = System.currentTimeMillis();
			HttpResponse response = httpClient.execute(postRequest);
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time taken : "+elapsedTime+"ms");

			InputStream is = response.getEntity().getContent();
			Reader reader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(reader);
			while (true) {
				try {
					String line = bufferedReader.readLine();
					if (line != null) {
						builder.append(line);
					} else {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(builder.toString());
			System.out.println("****************");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return builder.toString();
	}


	@POST
	@Path("/post")
	@Consumes("application/json")
	public Response createProductInJSON(Product product) {

		String result = "Product created : " + product;
		return Response.status(201).entity(result).build();
		
	}

	/*
	* Commented by Nirav during merger - with original call Back Method
	@GET
	@Path("/callback")
	@Produces("application/json")
	public void getCallback(@QueryParam("oauth_token") String oauth_token, @QueryParam("oauth_verifier") String oauth_verifier) {

		System.out.println("callback");
		System.out.println(oauth_token);
		System.out.println(oauth_verifier);

	}
	*/

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
							for (com.OCBCObjects.DepositAccounts.Product p: s.getProduct()){
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
			OAuthRequest request = CacheUtils.getRequestMap(user, url+"_"+sendingEntity);
			if (request == null) {
				log.info("Initiating Request for user+url="+user+"_"+url);
				request = initiateTransaction(user, url, sendingEntity);
				CacheUtils.putRequestMap(user, url+"_"+sendingEntity, request);
			}
			com.github.scribejava.core.model.Response response = INGCONSTANT.getService().execute(request);
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
			user=INGCONSTANT.getDefaultUser();

		try {
			OAuthRequest request = CacheUtils.getRequestMap(user, url);
			if (request == null) {
				log.info("Initiating Request for user+url="+user+"_"+url);
				request = initiateRequest(user, url);
				CacheUtils.putRequestMap(user, url, request);
			}
			com.github.scribejava.core.model.Response response=INGCONSTANT.getService().execute(request);
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
		INGCONSTANT.getService().signRequest(token, request);
		return(request);
	}

	private OAuthRequest initiateTransaction(String user, String url, String sendingEntity) throws IllegalAccessException, InstantiationException, InterruptedException, ExecutionException, IOException {
		OAuth1AccessToken token = getAccessToken(user);
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-Type", "application/json");
		request.setPayload(sendingEntity);

		System.out.println("\nPosting urlString: " + url);
		System.out.println("SendingEntity: '" + sendingEntity + "'");
		INGCONSTANT.getService().signRequest(token, request);
		return request;
	}

	private OAuth1AccessToken getAccessToken(String user){
		String[] keys = INGCONSTANT.getTestuser().getKeys().get(user);
		return(new OAuth1AccessToken(keys[0], keys[1], keys[2]));
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