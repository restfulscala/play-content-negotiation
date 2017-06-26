package org.restfulscala.playcontentnegotiation

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.FakeRequest

import scala.concurrent.Future

class ContentNegotiationSpec extends PlaySpec with Results with ScalaFutures {

  import org.scalatest.prop.TableDrivenPropertyChecks._
  import play.api.test.Helpers._
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "ContentNegotation" must {
    "return specified representation matching Accept header and honour the requested status code" in {
      val data = Table(
        ("Accept",                                    "Status Code",  "Content-type"),
        ("application/json",                          200,            Some("application/json")),
        ("application/xml, application/json; q=0.8",  200,            Some("application/xml")),
        ("*/*",                                       200,            Some("application/json")),
        ("application/hal+json",                      406,            Some("text/plain"))
      )
      object controller extends RequestExtractors with ContentNegotiation {
        val Action = DefaultActionBuilder.apply(PlayBodyParsers.apply().default)
        val representation = represent[String](
          as(Accepts.Json, s => Json.obj("msg" -> s)),
          as(Accepts.Xml, s => <message>s</message>)
        )

        def index() = Action { implicit req =>
          representation(r => r.respond("foo", 200))
        }
        def async() = Action.async { implicit req =>
          representation.async(r => Future.successful(r.respond("foo", 200)))
        }
      }
      forAll(data) { (acceptHeader, responseStatus, expectedContentType) =>

        val result = controller.index()(FakeRequest().withHeaders("Accept" -> acceptHeader))
        status(result) mustEqual responseStatus
        contentType(result) mustEqual expectedContentType

        val resultAsync = controller.async()(FakeRequest().withHeaders("Accept" -> acceptHeader))
        status(resultAsync) mustEqual responseStatus
        contentType(resultAsync) mustEqual expectedContentType

      }
    }
  }

}
