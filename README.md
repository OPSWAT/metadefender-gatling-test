# MetaDefender Gatling performance test

Useful links:

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [File scanning and sanitization API (cloud)](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)
* [Gatling](https://gatling.io/)

## Requirements

- Java 11 installed (Java 8 is not supported)

## Build

To build the jar, make sure you have Maven:

	mvn clean install

The install step will copy `src/main/resources/config.ini` to `target/config.ini`.

## Configuration

You can modify the settings in this configuration file: `target/config.ini`


Example configuration for **Metadefender Cloud** usage:
```
[general]
BaseUrl=https://api.metadefender.com/v4/file
ConstantUsers=5
TestDuration=10
ScanWorkflow=multiscan,sanitize
LocalPath=/home/opswatuser/testfiles
PollingIntervals=500
ApiKey=1234567890abcdefghijklmnopqrstuv
WaitBeforePolling=1000
```

Example configuration for **local MetaDefender Core** usage:
```
[general]
BaseUrl=http://localhost:8008/file
ConstantUsers=5
TestDuration=10
LocalPath=/home/opswatuser/testfiles
PollingIntervals=500
WaitBeforePolling=1000
```

***BaseURL:***

MetaDefender REST URL, e.g.: https://api.metadefender.com/v4/file (Cloud) or http://localhost:8008/file (local). 
More information about file scanning: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)

***ConstantUsers:***

The number of constant concurrent users for the test. Each simulated user will submit a randomly 
selected file for scanning, wait for the scan result, then select a new file for scanning.

***TestDuration:***

Total test duration while the simulated users will continue to submit files. (seconds)

***ScanWorkflow [optional]:***

MetaDefender scan workflow rule to activate. Multiple values can be separated by "," to combine multiple workflows. For Cloud-based testing, it can be `multiscan`, `sanitize` or `unarchive`. More details about Cloud workflows: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html) -> *Request* -> *rule*

For testing local MetaDefender Core, you are free to specify any custom workflow that is available on your installation.

***LocalPath:***

The folder path where the files to be tested are located, e.g.: */home/user1/tester*

***PollingIntervals:***

Sleep time between polling scan results. (milliseconds)

***ApiKey [Cloud usage only]:***

OPSWAT MetaDefender Cloud API key. You can find your key at [metadefender.opswat.com](https://metadefender.opswat.com/account) -> *API key information and limits* -> *API key*. (Registration required.)

***WaitBeforePolling:***

Waiting time after file submission to start polling. (milliseconds)

*****ShowPollingDetails:***

If true, Gatling will also show the polling details as separate requests. By default, it is false. (true/false)

*****DeveloperMode:***

It true, Gatling will print the HTTP responses to the console. By default, it is false. (true/false)


## Running a test

The `metadefender-gatling-2.0.0-SNAPSHOT.jar` and `config.ini` files are required to run the test. 
Maven will install these files in the `target` folder. 

Run a test using the helper script:

	./start.sh

The script runs the jar file with the following parameters:

	java -cp metadefender-gatling-2.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation

