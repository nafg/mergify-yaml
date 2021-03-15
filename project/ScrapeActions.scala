import org.jsoup.Jsoup

import java.net.URL
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


/**
 * Scrape the available set of actions from the Mergify website
 */
object ScrapeActions {
  def run() = {
    val baseUrl = new URL("https://docs.mergify.io/actions/index.html")
    val index = Jsoup.parse(baseUrl, 5000)

    val actionPages =
      index.select("#actions > .use-cases a")
        .eachAttr("href")
        .asScala.toList
        .map(baseUrl.toURI.resolve(_).toURL)

    val actionDocs =
      Await.result(
        Future.traverse(actionPages) { url =>
          Future {
            println("Scraping " + url)
            val ret = Jsoup.parse(url, 5000)
            println("Finished " + url)
            ret
          }
        },
        20.seconds
      )

    for (doc <- actionDocs) yield {
      val h1 = doc.select("h1").first()
      val name = h1.ownText().split('_').map(_.capitalize).mkString
      val description = h1.nextElementSibling().text().trim

      val table = doc.select("table").first()

      val rows =
        if (table == null)
          Nil
        else
          table.select("tbody > tr").asScala.toList
            .map(_.select("td").asScala.toList.map(_.text()))


      (if (description.isEmpty) "" else "/** " + description + "*/\n") +
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

          "\n  /** " + description + defaultMsg + "*/\n  " +
            camelCaseName + ": " + scalaTypeAndDefault
        })
          .mkString("(", ",", "\n)")
    }
  }
}
