
import java.io.{File, IOException}
import org.ini4j.Wini
import java.nio.file.{Files, Paths}


class Config {
  var baseUrl = ""
  var constantUser = 2
  var testDuration = 5
  var scanWorkflow = ""
  var localPath = ""
  var pollingIntervals = 500
  var apikey = ""
  var waitBeforePolling = 1000
  var silentScan = true
  var developerMode = false
}

object Config {
  def parseConfigure(filePath: String): Config = {

    val v = new Config()
    val ini = new Wini(new File(filePath))

    v.baseUrl = ini.get("general", "BaseUrl", classOf[String])
    v.constantUser = ini.get("general", "ConstantUsers", classOf[Int])
    v.testDuration = ini.get("general", "TestDuration", classOf[Int])
    v.scanWorkflow = ini.get("general", "ScanWorkflow", classOf[String])
    v.localPath = ini.get("general", "LocalPath", classOf[String])

    try{
      assert(Files.list(Paths.get(v.localPath)).count()>0)
    }
    catch {
      case _:Throwable => throw new IOException("WRONG PATH OR EMPTY DIRECTORY: "+v.localPath)
    }

    v.pollingIntervals = ini.get("general", "PollingIntervals", classOf[Int])
    v.apikey = ini.get("general", "ApiKey", classOf[String])
    v.waitBeforePolling = ini.get("general", "WaitBeforePolling", classOf[Int])
    v.silentScan = ini.get("general", "SilentScan", classOf[Boolean])
    v.developerMode = ini.get("general", "DeveloperMode", classOf[Boolean])
    v
  }
}
