package org.restfulscala.playcontentnegotiation

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.FakeRequest

class ContentNegotiationSpec extends PlaySpec with Results with ScalaFutures {

  import org.scalatest.prop.TableDrivenPropertyChecks._
  import play.api.test.Helpers._

  "ContentNegotation" must {
    "return specified representation matching Accept header and honour the requested status code" in {
      val data = Table(
        ("Accept",                                    "Status Code",  "Content-type"),
        ("application/json",                          200,            Some("application/json")),
        ("application/xml, application/json; q=0.8",  200,            Some("application/xml")),
        ("*/*",                                       200,            Some("application/json")),
        ("application/hal+json",                      406,            Some("text/plain"))
      )
      object controller extends Controller with ContentNegotiation {
        val representation = represent[String](
          as(Accepts.Json, s => Json.obj("msg" -> s)),
          as(Accepts.Xml, s => <message>s</message>)
        )
        def index() = Action { implicit req =>
          representation(r => r.respond("foo", 200))
        }
      }
      forAll(data) { (acceptHeader, responseStatus, expectedContentType) =>
        val result = controller.index()(FakeRequest().withHeaders("Accept" -> acceptHeader))
        status(result) mustEqual responseStatus
        contentType(result) mustEqual expectedContentType
      }
    }
  }

}
