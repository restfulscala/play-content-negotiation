package org.restfulscala

import play.api.mvc.{Accepting, Result, Results, Rendering}
import play.api.http.{MediaRange, Writeable}
import scala.runtime.AbstractPartialFunction

package object playcontentnegotiation {

  class RespondWith[A](representations: List[Representation[A]]) extends Rendering with Results {

    import play.api.http.MediaRange
    import play.api.mvc.RequestHeader

    def apply(a: A)(status: Int)(implicit req: RequestHeader): Result = {
      val pfs = representations map SingleAcceptFn(a, status)
      val all = pfs.toList ::: notAcceptable :: Nil
      val pf = all.reduceLeft(_ orElse _)
      render(pf)
    }

    protected def notAcceptedResponse: Result = {
      val acceptable = representations map (_.accepting.mimeType) mkString ", "
      NotAcceptable(s"Acceptable media types: $acceptable")
    }

    private val notAcceptable: PartialFunction[MediaRange, Result] = {
      val f = (_: MediaRange) => notAcceptedResponse
      PartialFunction(f)
    }

  }

  trait Representation[A] {
    def accepting: Accepting
    def respond(a: A, status: Int): Result
  }

  class SimpleRepresentation[A, B](
      val accepting: Accepting,
      val representationFactory: A => B)(implicit writeable: Writeable[B]) extends Representation[A] {
    override def respond(a: A, status: Int): Result =
      Results.Status(status)(representationFactory(a))
  }

  private[playcontentnegotiation] class SingleAcceptFn(accepting: Accepting, responder: () => Result)
      extends AbstractPartialFunction[MediaRange, Result] {
    override def isDefinedAt(x: MediaRange) = x.accepts(accepting.mimeType)
    override def applyOrElse[A1 <: MediaRange, B1 >: Result](x: A1, default: (A1) => B1): B1 =
      if (isDefinedAt(x)) responder() else default(x)
  }
  private[playcontentnegotiation] object SingleAcceptFn {
    def apply[A](a: A, status: Int)(responderForType: Representation[A]): SingleAcceptFn =
      new SingleAcceptFn(responderForType.accepting, () => responderForType.respond(a, status))
  }

}
