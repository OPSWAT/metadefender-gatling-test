
import java.io.{File, IOException}
import org.ini4j.Wini
import java.nio.file.{Files, Paths}


class Config {
  var baseUrl = ""
  var constantUser = 2
  var injectDuration = 0
  var maxDuration = 60
  var scanWorkflow = ""
  var localPath = ""
  var pollingIntervals = 500
  var apikey = ""
  var pollingDetails = false
  var developerMode = false
  var scanNumberUpper = 0
}

object Config {
  def checkValues(config: Config): Unit = {
    if (config.baseUrl == null || config.baseUrl == "") {
      throw new IOException("MISSING ENDPOINT")
    }
    if (config.constantUser < 1) {
      config.constantUser = 2
    }
    if (config.injectDuration == 0) {
      config.injectDuration = 10
    }
    if (config.maxDuration == 0) {
      config.maxDuration = config.injectDuration * 10
    }
    if (config.scanWorkflow == null) {
      config.scanWorkflow = ""
    }

    try {
      assert(Files.list(Paths.get(config.localPath)).count() > 0)
    }
    catch {
      case _: Throwable => throw new IOException("WRONG PATH OR EMPTY DIRECTORY: " + config.localPath)
    }

    if (config.pollingIntervals < 1) {
      config.pollingIntervals = 500
    }
    if (config.apikey == null) {
      config.apikey = ""
    }
  }

  def parseConfigure(filePath: String): Config = {

    val v = new Config()
    val ini = new Wini(new File(filePath))

    v.baseUrl = ini.get("general", "BaseUrl", classOf[String])
    v.constantUser = ini.get("general", "ConstantUsers", classOf[Int])
    v.injectDuration = ini.get("general", "InjectDuration", classOf[Int])
    v.maxDuration = ini.get("general", "MaxDuration", classOf[Int])
    v.scanWorkflow = ini.get("general", "ScanWorkflow", classOf[String])
    v.localPath = ini.get("general", "LocalPath", classOf[String])
    v.pollingIntervals = ini.get("general", "PollingIntervals", classOf[Int])
    v.apikey = ini.get("general", "ApiKey", classOf[String])
    v.apikey = ini.get("general", "ApiKey", classOf[String])
    v.pollingDetails = ini.get("general", "ShowPollingDetails", classOf[Boolean])
    v.developerMode = ini.get("general", "DeveloperMode", classOf[Boolean])
    v.scanNumberUpper = ini.get("general", "ScanNumberUpperBound", classOf[Int])
    checkValues(v)

    v
  }
}
