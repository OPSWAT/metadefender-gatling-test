Using Gatling to perform MetaDefender performance test

Documentation

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)
* [Gatling](https://gatling.io/)

To build the jar, make sure you have Maven:

	mvn clean install

The install step will copy `src/main/resources/config.ini` to `target/config.ini`.

You can modify the settings in this configuration file: `target/config.ini`

Example:
```
[general]
BaseUrl: https://api.metadefender.com/v4/file
ConstantUsers: 5
TestDuration: 10
ScanWorkflow: multiscan
LocalPath: home/opswatuser/testfiles
PollingIntervals: 500
ApiKey: 1234567890abcdefghijklmnopqrstuv
WaitBeforePolling: 1000
```


***BaseURL:***
MetaDefender REST URL (e.g.: https://api.metadefender.com/v4/file). Actual endpoint information for file scanning: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)

***ConstantUsers:***
The number of constant concurrent users. The script injects users at a constant -this number- rate, defined in users per second.

***TestDuration:***
Time to run the test. (s)

***ScanWorkflow:***
MetaDefender workflow rule to activate. Multiple values can be sent separated by "," to combine multiple workflows in one. It can be ```multiscan```, ```sanitize``` or```unarchive```. More details: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html) -> *Request* -> *rule*

***LocalPath:***
The folder path where the files to be tested are located. Eg.: */home/user1/tester*

***PollingIntervals:***
Sleep time between each polling scan result. (ms)

***ApiKey:***
OPSWAT MetaDefender Cloud apikey. It can be found at [metadefender.opswat.com](https://metadefender.opswat.com/account) -> *API key information and limits* -> *API key*. (Registration required.)


***WaitBeforePolling:***
Waiting time between push file and start to polling. (ms)

<br>

To run the jar (in the `target` folder where the `.jar` file is generated):

	java -cp metadefender-gatling-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation
