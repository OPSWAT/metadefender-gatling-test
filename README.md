Using Gatling to perform MetaDefender performance test

Documentation

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [Gatling](https://gatling.io/)

To build the jar, make sure you have Maven:

	mvn clean install

Configuration file: [config.ini](src/main/resources/config.ini)

```
[general]
BaseUrl: MetaDefender REST URL (e.g.: http://localhost:8008/file)
ScanWorkflow: MetaDefender workflow name
TestDuration: time to run the test (s)
UsersPerSec: the number of clients per second
RampupDuration: time to launch all clients (s)
LocalPath: Dataset folder path
PollingIntervals: sleep time between each polling scan result (ms)
ApiKey: Opswat MetaDefender Cloud apikey
WaitBeforePolling: waiting time between push file and start to polling (ms)
```

To run the jar (in 'target' folder):

	java -cp metadefender-gatling-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation
