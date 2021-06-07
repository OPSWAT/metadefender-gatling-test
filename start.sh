#!/bin/bash
cd target
java -cp metadefender-gatling-3.0.0-SNAPSHOT.jar io.gatling.app.Gatling -s ScanSimulation
cd ..