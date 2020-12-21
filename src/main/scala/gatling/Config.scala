
import java.io.File

import org.ini4j.Wini


class Config {
  var baseUrl = ""
  var scanWorkflow = ""
  var userPerSec = 2
  var testDuration = 5
  var rampupDuration = 5
  var pollingIntervals = 500
  var localPath = ""
}

object Config {
  def parseConfigure(filePath: String): Config = {
    val v = new Config()
    val ini = new Wini(new File(filePath))
    v.baseUrl = ini.get("general", "BaseUrl", classOf[String])
    v.scanWorkflow = ini.get("general", "ScanWorkflow", classOf[String])
    v.userPerSec = ini.get("general", "UsersPerSec", classOf[Int])
    v.testDuration = ini.get("general", "TestDuration", classOf[Int])
    v.rampupDuration = ini.get("general", "RampupDuration", classOf[Int])
    v.localPath = ini.get("general", "LocalPath", classOf[String])
    v.pollingIntervals = ini.get("general", "PollingIntervals", classOf[Int])
    v
  }
}
