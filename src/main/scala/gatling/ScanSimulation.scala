
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
    def initFileUpload () : HttpRequestBuilder = {
      var base = http("submit-file")
        .post(config.baseUrl)
        .header("filename","${filename}")

      if(config.scanWorkflow != ""){
        base = base.header("rule", config.scanWorkflow)
      }

      if(config.apikey != ""){
        base = base.header("apikey", config.apikey)
      }

      base
        .header("Content-Type","application/octet-stream")
        .body(RawFileBody("${filepath}"))
        .check(status.is(200))
        .check(jsonPath("$..data_id").find.exists)
        .check(jsonPath("$..data_id").find.saveAs("dataId"))
    }

    val submitFile = initFileUpload()
  }

  object ScanProgress {
    def initScanProgress () : HttpRequestBuilder = {
      var base = http("get-scan-result")
        .get(config.baseUrl + "/${dataId}")

      if(config.apikey != ""){
        base = base.header("apikey", config.apikey)
      }

      if(config.silentScan){
        base = base.silent
      }

      base
        .check(status.is(200))
        .check(jsonPath("$..scan_results.progress_percentage").optional.saveAs("progress"))
        .check(jsonPath("$..sanitized.result").optional.saveAs("sanitization"))
        .check( jsonPath( "$" ).saveAs( "RESPONSE_DATA" ) )
    }

    def printResponse(): ChainBuilder = {
      exec( session => {
        println(session( "RESPONSE_DATA").as[String])
        session
      })
    }

    private val getScanProgress = initScanProgress()

    val action: ChainBuilder =
      exec(_.set("sanitization", "Processing").set("progress", "0"))
        .doIf(session => session("dataId").asOption[String].isDefined) {
          if (config.checkSanitization){
            asLongAs(session => session("progress").as[String] != "100" ||
              session("sanitization").as[String] == "Processing") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode){
                println("Scan and sanitization response:")
                printResponse()
              }
            }
          }
          else {
            asLongAs(session => session("progress").as[String] != "100") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode){
                println("Scan response:")
                printResponse()
              }
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
