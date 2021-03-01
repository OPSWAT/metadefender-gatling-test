
import io.gatling.core.Predef._
import io.gatling.core.body.RawFileBody
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import scala.concurrent.duration._


class ScanSimulation extends Simulation {
  val config = Config.parseConfigure("config.ini")
  var localFiles = new LocalFiles(config.localPath)

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(config.baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .disableCaching

  object FileUpload {
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
    def initScanProgress () : HttpRequestBuilder = {
      var base = http("get-scan-result")
        .get(config.baseUrl + "/${dataId}")
        .header("apikey", config.apikey)
        .check(status.is(200))
      if(config.silentScan){
        base = base.silent
      }
      base
        .check(jsonPath("$..scan_results.progress_percentage").optional.saveAs("progress"))
        .check(jsonPath("$..sanitized.result").optional.saveAs("sanitization"))
        .check( jsonPath( "$" ).saveAs( "RESPONSE_DATA" ) )
    }

    def printResponse(): ChainBuilder = {
      exec( session => {
        println("Response:")
        println(session( "RESPONSE_DATA").as[String])
        session
      })
    }

    private val getScanProgress = initScanProgress()

    val action: ChainBuilder =
      exec(_.set("sanitization", "Processing").set("progress", "0"))
        .doIf(session => session("dataId").asOption[String].isDefined) {
          if (config.scan && config.sanitization){
            asLongAs(session => session("progress").as[String] != "100" ||
              session("sanitization").as[String] == "Processing") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode){printResponse()}
            }
          }
          else if(config.scan){
            asLongAs(session => session("progress").as[String] != "100") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode){printResponse()}
            }
          }
          else {
            asLongAs(session => session("sanitization").as[String] == "Processing") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode){printResponse()}
            }
          }
        }
  }


  val pipeline: ScenarioBuilder = scenario("scan-pipeline")
    .feed(localFiles.feeder)
    .exec(FileUpload.submitFile)
    .pause(config.waitBeforePolling.milliseconds)
    .exec(ScanProgress.action)

  setUp(
    pipeline
      .inject(constantConcurrentUsers(config.constantUser).during(config.testDuration))
      .protocols(httpProtocol)
  )
}
