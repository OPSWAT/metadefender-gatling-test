Using Gatling to perform MetaDefender performance test

To build the jar:

	mvn clean install

To run the jar (in 'target' folder):

	java -cp metadefender-gatling-1.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation

Configuration file:	config.ini (at the same folder with the jar file)
