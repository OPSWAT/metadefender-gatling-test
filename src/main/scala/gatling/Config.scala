
import java.io.File

import org.ini4j.Wini


class Config {
  var baseUrl = ""
  var scanWorkflow = ""
  var constantUser = 2
  var testDuration = 5
  var rampupDuration = 5
  var pollingIntervals = 500
  var localPath = ""
  var apikey = ""
  var waitBeforePolling = 1000
  var silentScan = true
  var scan = true
  var sanitization = false
}

object Config {
  def parseConfigure(filePath: String): Config = {

    var rule = "sanitize"
    var scanFlow = ""
    val v = new Config()
    val ini = new Wini(new File(filePath))
    v.baseUrl = ini.get("general", "BaseUrl", classOf[String])
    scanFlow = ini.get("general", "ScanWorkflow", classOf[String])
    v.constantUser = ini.get("general", "ConstantUsers", classOf[Int])
    v.testDuration = ini.get("general", "TestDuration", classOf[Int])
    v.rampupDuration = ini.get("general", "RampupDuration", classOf[Int])
    v.localPath = ini.get("general", "LocalPath", classOf[String])
    v.pollingIntervals = ini.get("general", "PollingIntervals", classOf[Int])
    v.apikey = ini.get("general", "ApiKey", classOf[String])
    v.waitBeforePolling = ini.get("general", "WaitBeforePolling", classOf[Int])
    v.silentScan = ini.get("general", "SilentScan", classOf[Boolean])

    val scan = ini.get("general", "Scan", classOf[Boolean])
    val sanitization = ini.get("general", "Sanitization", classOf[Boolean])

    if (scan && sanitization){
      rule =scanFlow+",sanitize"
    }
    else if(scan){
        rule=scanFlow
    }

    v.scan = scan
    v.sanitization = sanitization
    v.scanWorkflow = rule
    v
  }
}
