import org.jsoup.helper.HttpConnection
import org.jsoup.nodes.Document
import org.jsoup.{Connection, HttpStatusException}

import java.net.URL
import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.collection.JavaConverters.*
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

/** Scrape the available set of actions from the Mergify website
  */
object ScrapeActions {
  def getJsoupDocument(url: URL): Document = {
    val originalUrlString = url.toString

    @tailrec
    def get(urlString: String): Document = {
      println(s"[$originalUrlString] Getting " + urlString)
      val connection = new HttpConnection()
      connection.url(urlString).followRedirects(true).method(Connection.Method.GET).ignoreHttpErrors(true)
      connection.execute()
      connection.response().statusCode() match {
        case s if s >= 200 && s < 300 => connection.response().parse()
        case 429 =>
          val retryAfter = connection.response().header("Retry-After").toInt
          Console.err.println(s"[$originalUrlString] Too many requests, waiting $retryAfter seconds...")
          Thread.sleep(retryAfter * 1000)
          get(urlString)
        case s =>
          val message = s"HTTP error fetching URL (${connection.response().statusMessage()})"
          throw new HttpStatusException(message, s, urlString)
      }
    }

    @tailrec
    def followMetaRefresh(urlString: String): Document = {
      val doc = get(urlString)
      val canonical = doc.select("html > head > meta[http-equiv=refresh]").first()
      if (canonical == null) doc
      else {
        val href = canonical.attr("content").split("=").last
        if (href.equals(urlString)) doc
        else {
          println(s"[$originalUrlString] Following to $href")
          followMetaRefresh(href)
        }
      }
    }

    followMetaRefresh(originalUrlString)
  }

  def run() = {
    val baseUrl = new URL("https://docs.mergify.io/actions/index.html")
    val index   = getJsoupDocument(baseUrl)

    val actionPages =
      index
        .select("#actions > .use-cases a")
        .eachAttr("href")
        .asScala
        .toList
        .map(baseUrl.toURI.resolve(_).toURL)

    val actionDocs = {
      implicit val executionContext =
        ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
      Await.result(
        Future.traverse(actionPages) { url =>
          Future {
            println("Scraping " + url)
            getJsoupDocument(url)
          }.andThen {
            case Success(value) => println("Finished " + url)
            case Failure(exception) =>
              Console.err.println("Failed for " + url + ":")
              exception.printStackTrace()
          }
        },
        20.seconds
      )
    }

    for (doc <- actionDocs) yield {
      val h1          = doc.select("h1").first()
      val name        = h1.ownText().split('_').map(_.capitalize).mkString
      val description = h1.nextElementSibling().text().trim

      val table = doc.select("table").first()

      val rows =
        if (table == null)
          Nil
        else
          table
            .select("tbody > tr")
            .asScala
            .toList
            .map(_.select("td").asScala.toList.map(_.text()))

      def wrapText(initial: String, width: Int) = {
        @tailrec
        def loop(string: String, splitComments: Vector[String]): Vector[String] =
          if (string.length > width) {
            val i = string.lastIndexOf(" ", width)
            loop(string.substring(i).trim, splitComments :+ string.take(i))
          } else
            splitComments :+ string
        loop(initial, Vector.empty)
      }

      def mkComment(description: String, indent: String) =
        if (description.isEmpty) ""
        else
          wrapText(description, 114 - indent.length)
            .mkString(s"$indent/** ", s"\n$indent  * ", s"\n$indent  */\n")

      mkComment(description, "") +
        "case class " + name +
        (for (List(name, typ, default, description) <- rows) yield {
          val camelCaseName = (name.split('_').toList match {
            case Nil          => ""
            case head :: tail => head + tail.map(_.capitalize).mkString
          }) match {
            case "type" => "`type`"
            case s      => s
          }

          val baseScalaType = typ match {
            case "list of string" | "list of Template" => "Seq[String]"
            case "Boolean"                             => "Boolean"
            case "string" | "Template"                 => "String"
            case other                                 => "ToJson /*" + other + "*/"
          }

          val (scalaTypeAndDefault, commentDefault) =
            (baseScalaType, default.trim) match {
              case ("String", s)                       => (baseScalaType + s""" = "$s"""", None)
              case ("Seq[String]", "[]")               => (baseScalaType + " = Nil", None)
              case ("Boolean", d @ ("true" | "false")) => (baseScalaType + " = " + d, None)
              case (_, "")                             => (baseScalaType, None)
              case (_, d)                              => (s"Option[$baseScalaType] = None", Some(d))
            }

          val defaultMsg = commentDefault.fold("")("\nDefault: " + _)

          "\n" + mkComment(description + defaultMsg, "  ") + "  " +
            camelCaseName + ": " + scalaTypeAndDefault
        })
          .mkString("(", ",", "\n)")
    }
  }
}
