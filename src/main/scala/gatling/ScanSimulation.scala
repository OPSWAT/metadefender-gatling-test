
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

    private val getScanProgress =
      http("get-scan-result")
        .get(config.baseUrl + "/${dataId}")
        .header("apikey", config.apikey)
        .check(status.is(200))
        .check(jsonPath("$..scan_results.progress_percentage").optional.saveAs("progress"))
        .check(jsonPath("$..sanitized.result").optional.saveAs("sanitization"))
        .check( jsonPath( "$" ).saveAs( "RESPONSE_DATA" ) )


    val actionSum: ChainBuilder =
      exec(_.set("sanitization", "Processing").set("progress", "0"))
        .doIf(session => session("dataId").asOption[String].isDefined) {
          if (config.scan && config.sanitization){
            println("Scan and Sanitization")
            asLongAs(session => session("progress").as[String] != "100" ||
              session("sanitization").as[String] == "Processing") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)}
          }
          else if(config.scan){
            println("Scan")
            asLongAs(session => session("progress").as[String] != "100") {
              pause(config.pollingIntervals.millis).exec(getScanProgress)}
          }
          else {
            println("Sanitization")
            asLongAs(session => session("sanitization").as[String] == "Processing") {
              pause(config.pollingIntervals.millis)
              .exec(getScanProgress)
                .exec( session => {
                  println( "Some Restful Service:" )
                  println( session( "RESPONSE_DATA" ).as[String] )
                  session
                })
            }
          }
        }

  }

  <!-- uncomment to if you want to check sanitization result -->

//  object GetSanitized {
//    private val getSanitizedFile =
//      http("get-sanitized-file")
//        .get(config.baseUrl + "/converted/${dataId}")
//        .header("apikey", config.apikey)
//        .check(status.is(200))
//
//    val action: ChainBuilder = doIf(session => session("sanitization").asOption[String].contains("Sanitized")) {
//      exec(getSanitizedFile)
//    }
//  }


  val pipeline: ScenarioBuilder = scenario("scan-pipeline")
    .feed(localFiles.feeder)
    .exec(FileUpload.submitFile)
    .pause(config.waitBeforePolling.milliseconds)
    .exec(ScanProgress.actionSum)

  setUp(
    pipeline
      .inject(constantConcurrentUsers(config.constantUser).during(config.testDuration))
      .protocols(httpProtocol)
  )
}
