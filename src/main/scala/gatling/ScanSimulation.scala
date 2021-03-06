
import io.gatling.core.Predef._
import io.gatling.core.body.RawFileBody
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._


class ScanSimulation extends Simulation {
  val config = Config.parseConfigure("config.ini")
  var localFiles = new LocalFiles(config.localPath)
  var fileUploadCounter = new AtomicInteger(0)

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(config.baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .disableCaching

  object FileUpload {
    def initFileUpload(): HttpRequestBuilder = {
      var base = http("submit-file")
        .post(config.baseUrl)
        .header("filename", "${filename}")

      if (config.scanWorkflow != "") {
        base = base.header("rule", config.scanWorkflow)
      }

      if (config.apikey != "") {
        base = base.header("apikey", config.apikey)
      }

      base
        .header("Content-Type", "application/octet-stream")
        .body(RawFileBody("${filepath}"))
        .check(status.is(200))
        .check(jsonPath("$..data_id").find.exists)
        .check(jsonPath("$..data_id").find.saveAs("dataId"))
    }

    val submitFile = initFileUpload()
  }

  object ScanProgress {
    def initScanProgress(): HttpRequestBuilder = {
      var base = http("get-scan-result")
        .get(config.baseUrl + "/${dataId}")

      if (config.apikey != "") {
        base = base.header("apikey", config.apikey)
      }

      if (!config.pollingDetails) {
        base = base.silent
      }

      base
        .check(status.is(200))
        .check(jsonPath("$..process_info.progress_percentage").optional.saveAs("progress"))
        .check(jsonPath("$").optional.saveAs("response_data"))
    }

    def printResponse(): ChainBuilder = {
      exec(session => {
        if (session.contains("response_data")) {
          println("Response:")
          println(session("response_data").as[String])
        }
        session
      })
    }

    private val getScanProgress = initScanProgress()

    val action: ChainBuilder =
      exec(_.set("progress", "0"))
        .doIf(session => session("dataId").asOption[String].isDefined) {
          asLongAs(session => session("progress").as[String] != "100") {
            pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
              .doIf(config.developerMode) {
                printResponse()
              }
          }
        }
  }

  val pipeline: ScenarioBuilder = scenario("scan-pipeline")
    .doIf(_ => (config.scanRequestsUpper == 0 || fileUploadCounter.getAndIncrement() < config.scanRequestsUpper)) {
      feed(localFiles.feeder)
        .exec(FileUpload.submitFile)
        .exec(ScanProgress.action)
    }

  setUp(
    pipeline
      .inject(constantUsersPerSec(config.usersPerSec).during(config.injectDuration))
      .protocols(httpProtocol)
  ).maxDuration(config.maxDuration)
}
