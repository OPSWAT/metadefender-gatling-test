Using Gatling to perform MetaDefender performance test

Documentation

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)
* [Gatling](https://gatling.io/)

To build the jar, make sure you have Maven:

	mvn clean install

The install step will copy `src/main/resources/config.ini` to `target/config.ini`.

You can modify the settings in this configuration file: `target/config.ini`

Example configuration:
```
[general]
BaseUrl: https://api.metadefender.com/v4/file
ConstantUsers: 5
TestDuration: 10
ScanWorkflow: multiscan
LocalPath: /home/user/testfiles
PollingIntervals: 500
ApiKey: 1234567890abcdefghijklmnopqrstuv
WaitBeforePolling: 1000
```

***BaseURL***

MetaDefender REST URL (e.g.: https://api.metadefender.com/v4/file). More information about file scanning: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)

***ConstantUsers***

The number of constant concurrent users for the test. Each simulated user will submit a randomly 
selected file for scanning, wait for the scan result, then select a new file for scanning.

***TestDuration***

Total test duration while the simulated users will continue to submit files. (seconds)

***ScanWorkflow***

MetaDefender workflow rule to activate. Multiple values can be sent separated by "," to combine multiple workflows. It can be `multiscan`, `sanitize` or `unarchive`. More details: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html) -> *Request* -> *rule*

***LocalPath***

The folder path where the files to be tested are located, e.g.: */home/user1/tester*

***PollingIntervals***

Sleep time between polling scan results. (milliseconds)

***ApiKey***

OPSWAT MetaDefender Cloud API key. You can find your key at [metadefender.opswat.com](https://metadefender.opswat.com/account) -> *API key information and limits* -> *API key*. (Registration required.)

***WaitBeforePolling***

Waiting time after file submission to start polling. (milliseconds)

<br>

To run the jar (in the `target` folder where the `.jar` file is generated):

	java -cp metadefender-gatling-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation
