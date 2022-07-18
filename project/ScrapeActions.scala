import java.net.URL
import java.util.concurrent.Executors

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/** Scrape the available set of actions from the Mergify website
  */
object ScrapeActions {
  @tailrec
  def getJsoupDocument(url: URL): Document = {
    val urlString = url.toString
    val doc       = Jsoup.connect(urlString).followRedirects(true).get()
    val canonical = doc.select("html > head > link[rel=canonical]").first()
    if (canonical == null) doc
    else {
      val href = new URL(canonical.attr("href"))
      if (href.equals(url)) doc
      else getJsoupDocument(href)
    }
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
