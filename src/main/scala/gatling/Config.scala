
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
  var pollingDetails = false
  var developerMode = false
}

object Config {
  def checkValues(config: Config): Unit ={
    if(config.baseUrl==null||config.baseUrl==""){throw new IOException("MISSING ENDPOINT")}
    if(config.constantUser<1){config.constantUser=2}
    if(config.testDuration<1){config.testDuration=5}
    if(config.scanWorkflow==null){config.scanWorkflow=""}

    try{
      assert(Files.list(Paths.get(config.localPath)).count()>0)
    }
    catch {
      case _:Throwable => throw new IOException("WRONG PATH OR EMPTY DIRECTORY: "+config.localPath)
    }

    if(config.pollingIntervals<1){config.pollingIntervals=500}
    if(config.apikey==null){config.apikey=""}
    if(config.waitBeforePolling<1){config.waitBeforePolling=1000}
  }

  def parseConfigure(filePath: String): Config = {

    val v = new Config()
    val ini = new Wini(new File(filePath))

    v.baseUrl = ini.get("general", "BaseUrl", classOf[String])
    v.constantUser = ini.get("general", "ConstantUsers", classOf[Int])
    v.testDuration = ini.get("general", "TestDuration", classOf[Int])
    v.scanWorkflow = ini.get("general", "ScanWorkflow", classOf[String])
    v.localPath = ini.get("general", "LocalPath", classOf[String])
    v.pollingIntervals = ini.get("general", "PollingIntervals", classOf[Int])
    v.apikey = ini.get("general", "ApiKey", classOf[String])
    v.apikey=ini.get("general", "ApiKey", classOf[String])
    v.waitBeforePolling = ini.get("general", "WaitBeforePolling", classOf[Int])
    v.pollingDetails = ini.get("general", "ShowPollingDetails", classOf[Boolean])
    v.developerMode = ini.get("general", "DeveloperMode", classOf[Boolean])

    checkValues(v)

    v
  }
}
