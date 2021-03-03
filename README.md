# MetaDefender Gatling performance test

Useful links:

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [File scanning and sanitization API (cloud)](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)
* [Gatling](https://gatling.io/)

## Requirements

- installed Java 11

## Usage
### Build

To build the jar, make sure you have Maven:

	mvn clean install

The install step will copy `src/main/resources/config.ini` to `target/config.ini`.

### Configuration

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

MetaDefender REST URL (e.g.: https://api.metadefender.com/v4/file (cloud) or http://localhost:8008/file (local)). More information about file scanning: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html)

***ConstantUsers:***

The number of constant concurrent users for the test. Each simulated user will submit a randomly 
selected file for scanning, wait for the scan result, then select a new file for scanning.

***TestDuration:***

Total test duration while the simulated users will continue to submit files. (seconds)

***ScanWorkflow [optional]:***

MetaDefender scanning workflow rule to activate. Multiple values can be sent separated by "," to combine multiple workflows. For cloud-based testing, it can be `multiscan`, `sanitize` or `unarchive`. More details to cloud usage: [File scanning API](https://onlinehelp.opswat.com/mdcloud/2.1_Scanning_a_file_by_file_upload.html) -> *Request* -> *rule*

***LocalPath:***

The folder path where the files to be tested are located, e.g.: */home/user1/tester*

***PollingIntervals:***

Sleep time between polling scan results. (milliseconds)

***ApiKey [cloud usage only]:***

OPSWAT MetaDefender Cloud API key. You can find your key at [metadefender.opswat.com](https://metadefender.opswat.com/account) -> *API key information and limits* -> *API key*. (Registration required.)

***WaitBeforePolling:***

Waiting time after file submission to start polling. (milliseconds)

***SilentScan:***

A silent request is issued but not logged nor reported. If SilentScan is true the get-scan-result method (polling part) still executed but not logged or reported the errors. (true/false)


*****DeveloperMode:***

It can be set a DeveloperMode, which print the HTTP-responses to the terminal. By default, it is hidden and false. (true/false)


### Run a test

`metadefender-gatling-2.0.0-SNAPSHOT.jar` file and `config.ini` file are required to run the test. In case of building `metadefender-gatling-2.0.0-SNAPSHOT.jar` is generated in `target` folder. Command to run:

	java -cp metadefender-gatling-2.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation

