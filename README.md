#### ClearCaster Examples of How to Sign GraphQL API Requests

Short code examples in several different languages that illustrate how to sign ClearCaster GraphQL API requests. ClearCaster API keys are obtained by logging into http://universal.clearcaster.wowza.com and navigating to the **Manage > Integrations** tab.

* Javascript
  * Install node JS
  * Modify **index.js**
    * Replace **[API-Key-Here]** and **[Secret-Key-Here]** with the API key data from the integrations tab
  * Run npm install, npm start
* Java
  * Install JDK 8 or greater
  * Modify **src/main/Main.java**
    * Replace **[API-Key-Here]** and **[Secret-Key-Here]** with the API key data from the integrations tab
  * Run ./compile.sh, ./run.sh
* PHP
  * Modify **graphqlRequest.php**
    * Replace **[API-Key-Here]** and **[Secret-Key-Here]** with the API key data from the integrations tab
  * Host file on WebServer that has PHP 7 or greater installed
  * Open browser and enter URL to **graphqlRequest.php**
* Python
  * Install python
  * Modify **graphqlRequest.py**
    * Replace **[API-Key-Here]** and **[Secret-Key-Here]** with the API key data from the integrations tab
  * Run ./install.sh, ./run.sh
* DotNET (C#)
  * Install Visual Studio 2019
  * Open Solution **ConsoleApp1/ConsoleApp1.sln**
  * Modify **Program.cs**
    * Replace **[API-Key-Here]** and **[Secret-Key-Here]** with the API key data from the integrations tab
  * Build and Run project
