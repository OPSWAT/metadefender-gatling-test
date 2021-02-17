
import io.gatling.core.Predef._
import io.gatling.core.body.RawFileBody
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._


class ScanSimulation extends Simulation {
  val config = Config.parseConfigure("config.ini")
  var localFiles = new LocalFiles(config.localPath)

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(config.baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .disableCaching

  object Scan {
    val submitFile = http("submit-file")
      .post(config.baseUrl)
      .headers(Map("filename" -> "${filename}", "rule" -> config.scanWorkflow))
      .header("apikey", config.apikey)
      .header("Content-Type","application/octet-stream")
      .body(RawFileBody("${filepath}"))
      .check(status.is(200))
      .check(jsonPath("$..data_id").find.exists)
      .check(jsonPath("$..data_id").find.saveAs("dataId"))
  }

  object ScanProgress {
    private val getScanProgress =
      http("get-scan-result")
        .get(config.baseUrl + "/${dataId}")
        .header("apikey", config.apikey)
        .check(status.is(200))
        .check(jsonPath("$..process_info.progress_percentage").find.saveAs("progress"))
        .check(jsonPath("$..process_info.post_processing.actions_ran").optional.saveAs("sanitization"))

    val action: ChainBuilder =
      exec(_.set("progress", "0"))
        .doIf(session => session("dataId").asOption[String].isDefined) {
          asLongAs(session => session("progress").as[String] != "100") {
            pause(config.pollingIntervals.millis).exec(getScanProgress)
          }
        }
  }

  <!-- uncomment to if you want to check sanitization result -->
  <!--
  object GetSanitized {
    private val getSanitizedFile =
      http("get-sanitized-file")
        .get(config.baseUrl + "/converted/${dataId}")
        .check(status.is(200))

    val action: ChainBuilder = doIf(session => session("sanitization").asOption[String].contains("Sanitized")) {
      exec(getSanitizedFile)
    }
  }
  -->

  val pipeline: ScenarioBuilder = scenario("scan-pipeline")
    .feed(localFiles.feeder)
    .exec(Scan.submitFile)
    .exec(ScanProgress.action)
    //.exec(GetSanitized.action)

  setUp(
    pipeline
      .inject(
        rampUsersPerSec(1) to config.userPerSec during config.rampupDuration,
        constantUsersPerSec(config.userPerSec) during config.testDuration
      )
      .protocols(httpProtocol)
  )
}
