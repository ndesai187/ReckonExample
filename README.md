# ReckonExample

<b>Useful Info</b>

<li>Development tools: IntelliJ Ultimate</li>

<li>Server: Wildfly</li>

<li>localhost to internet: pagekite.me</li>

<li>api reference: https://apiexplorersandbox.openbankproject.com</li>

<li>json to java object: http://pojo.sodhanalibrary.com</li>

<li>JUnit test: Refer to jTest.java</li>

<br/>

<b>API Detail</b>

<br/>

0. GetBank list:
https://reckoning.pagekite.me/ReckonINGExample/getBanks

output:
{"banks":["rbs:RBS Bank","hsbc-test:HSBC BANK"]}
<br/>

1. Get all accounts for user bennettzhou1

https://reckoning.pagekite.me/ReckonINGExample/getMyAccounts?user_name=bennettzhou1

output:

{"numOfAccounts":"4","displayname":"bennettzhou1","accountList":[{"bank_id":"at.03.1465.es","bank_fullname":"ING BANK, N.V. SUCURSAL EN ESPA脩A","id":"BenAccount_ing","number":"4978916594","amount":"55000.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_id":"deutche-test","bank_fullname":"Deutche Bank Test","id":"BenAccount_db","number":"8462458777","amount":"1234.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_id":"hsbc-test","bank_fullname":"Hongkong and Shanghai Bank","id":"BenAccount_hsbc","number":"7383735652","amount":"60000.00","currency":"EUR","displayname":"bennettzhou1"},{"bank_id":"rbs","bank_fullname":"The Royal Bank of Scotland","id":"BenAccount_1","number":"1874129357","amount":"5000.00","currency":"EUR","displayname":"bennettzhou1"}]}
<br/>



2. Get all accounts for user superhero

https://reckoning.pagekite.me/ReckonINGExample/getMyAccounts?user_name=superhero

output:

{"numOfAccounts":"3","displayname":"superhero","accountList":[{"bank_id":"in-bank-y-2","bank_fullname":"The India Bank of Y","id":"superinbank","number":"1883610309","amount":"3060.00","currency":"GBP","displayname":"superhero"},{"bank_id":"at02-1465--01","bank_fullname":"ING BANK, N.V. SUCURSAL EN ESPA脩A","id":"supering","number":"5515375125","amount":"8950.00","currency":"EUR","displayname":"superhero"},{"bank_id":"hsbc-test","bank_fullname":"Hongkong and Shanghai Bank","id":"superhsbc","number":"27222436","amount":"1970.00","currency":"EUR","displayname":"superhero"}]}
<br/>



3. Get balance for user bennettzhou1, bank_id = rbs(RBS Bank), Account name=BenAccount_1

https://reckoning.pagekite.me/ReckonINGExample/getAccountById?user_name=bennettzhou1&bank_id=rbs&account=BenAccount_1

output:

{"bank_id":"rbs","bank_fullname":"The Royal Bank of Scotland","id":"BenAccount_1","number":"1874129357","amount":"5000.00","currency":"EUR","displayname":"bennettzhou1"}
<br/>



4. Get balance for user superhero, bank_id = hsbc-test(HSBC Bank), Account name=superhsbc

https://reckoning.pagekite.me/ReckonINGExample/getAccountById?user_name=superhero&bank_id=hsbc-test&account=superhsbc

output:

{"bank_id":"hsbc-test","bank_fullname":"Hongkong and Shanghai Bank","id":"superhsbc","number":"27222436","amount":"1970.00","currency":"EUR","displayname":"superhero"}
<br/>




5. Get balance for user superhero, bank_id = at02-1465–01(ING Bank), Account name=supering

https://reckoning.pagekite.me/ReckonINGExample/getAccountById?user_name=superhero&bank_id=at02-1465--01&account=supering

output:

{"bank_id":"at02-1465--01","bank_fullname":"ING BANK, N.V. SUCURSAL EN ESPA脩A","id":"supering","number":"5515375125","amount":"8950.00","currency":"EUR","displayname":"superhero"}
<br/>



6. Transfer 10 dollar from ING bank account(supering) to HSBC account(superhsbc) under user_name superhero
https://reckoning.pagekite.me/ReckonINGExample/createTransaction/superhero?frombank_id=at02-1465--01&fromid=supering&amount=10&tobank_id=hsbc-test&toid=superhsbc

output:

{"transaction_ids":"472d4905-7108-4445-b65e-5d94cf1a14c7","bank_id":"at02-1465--01","bank_fullname":"ING BANK, N.V. SUCURSAL EN ESPA脩A","account_id":"supering","status":"COMPLETED","start_date":"2017-04-28T16:42:08Z","end_date":"2017-04-28T16:42:08Z","balance":"8930.00","currency":"EUR"}
<br/>


7. Transfer 100 dollar from HSBC bank account(superhsbc) under user_name superhero,  to RBS account(BenAccount_1) under user_name bennettzhou1

https://reckoning.pagekite.me/ReckonINGExample/createTransaction/superhero?frombank_id=hsbc-test&fromid=superhsbc&amount=100&tobank_id=rbs&toid=BenAccount_1

output:

{"transaction_ids":"5220bbbc-420e-4546-abab-b144502c4480","bank_id":"hsbc-test","bank_fullname":"Hongkong and Shanghai Bank","account_id":"superhsbc","status":"COMPLETED","start_date":"2017-04-28T16:45:33Z","end_date":"2017-04-28T16:45:33Z","balance":"1890.00","currency":"EUR"}
<br/>

8. Check Transaction history for account superhsbc in HSBC bank, under user_name superhero
https://reckoning.pagekite.me/ReckonINGExample/getTransactionHistoryById?user_name=superhero&bank_id=hsbc-test&account=superhsbc
<br/>


9. Check ALL Transaction history of all bank accounts of user superhero
https://reckoning.pagekite.me/ReckonINGExample/getMyTransactionHistory?user_name=superhero
<br/>
</p>


