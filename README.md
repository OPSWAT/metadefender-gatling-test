Using Gatling to perform MetaDefender performance test

Documentation

* [MetaDefender Core developer guide](https://onlinehelp.opswat.com/corev4/9._%28NEW%29_MetaDefender_Core_Developer_Guide.html)
* [Gatling](https://gatling.io/)

To build the jar, make sure you have Maven:

	mvn clean install

The install step will copy `src/main/resources/config.ini` to `target/config.ini`.

You can modify the settings in this configuration file: `target/config.ini`

```
[general]
BaseUrl: MetaDefender REST URL (e.g.: http://localhost:8008/file)
ConstantUsers: the number of constant concurrent users
TestDuration: time to run the test (s)
ScanWorkflow: MetaDefender workflow name
LocalPath: Dataset folder path
PollingIntervals: sleep time between each polling scan result (ms)
ApiKey: OPSWAT MetaDefender Cloud apikey
WaitBeforePolling: waiting time between push file and start to polling (ms)
```

TODO

To run the jar (in the `target` folder where the `.jar` file is generated):

	java -cp metadefender-gatling-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation
