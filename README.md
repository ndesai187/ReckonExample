# ReckonExample
Development tools: IntelliJ Ultimate
Server: Wildfly 
localhost to internet: ngrok
api reference: https://apiexplorersandbox.openbankproject.com
json to java object: http://pojo.sodhanalibrary.com


TEST API:
API 1: get banks
http://localhost:8080/ReckonINGExample/getBanks

API 2: get accounts from user
http://localhost:8080/ReckonINGExample/getMyAccounts?user_name=bennettzhou1
Json Output:
{"numOfAccounts":"4","displayname":"bennettzhou1","accountList":[{"bank_fullname":"ING BANK, N.V. SUCURSAL EN ESPAÃ‘A","id":"BenAccount_ing","number":"4978916594","amount":"55000.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_fullname":"Deutche Bank Test","id":"BenAccount_db","number":"8462458777","amount":"1234.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_fullname":"Hongkong and Shanghai Bank","id":"BenAccount_hsbc","number":"7383735652","amount":"60000.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_fullname":"The Royal Bank of Scotland","id":"BenAccount_1","number":"1874129357","amount":"5000.00","currency":"EUR","displayname":"bennettzhou1"}]}

To start testing without server
Run as application,  JSONService.main() to involve the function directly, you need to change method getMyAccounts() and getAccountbyId() to static

	public static void main(String[] args) {

		getMyAccounts("bennettzhou1");

	}
