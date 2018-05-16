package org.restfulscala.playcontentnegotiation

import play.api.http.MediaRange
import play.api.mvc.{Accepting, Rendering, Result, Results}

import scala.concurrent.Future
import scala.runtime.AbstractPartialFunction

class RespondWith[A](representations: List[Representation[A]])
    extends Rendering
    with Results {

  import play.api.http.MediaRange
  import play.api.mvc.RequestHeader

  def apply(a: Representation[A] => Result)(
      implicit req: RequestHeader): Result = {
    val pfs = representations map SingleAcceptFn(a)
    val all = pfs ::: notAcceptable :: Nil
    val pf = all.reduceLeft(_ orElse _)
    render(pf)
  }

  def async(a: Representation[A] => Future[Result])(
      implicit req: RequestHeader): Future[Result] = {
    val pfs = representations map SingleAsyncAcceptFn(a)
    val all = pfs ::: notAcceptableAsync :: Nil
    val pf = all.reduceLeft(_ orElse _)
    render.async(pf)
  }

  protected def notAcceptedResponse: Result = {
    val acceptable = representations map (_.accepting.mimeType) mkString ", "
    NotAcceptable(s"Acceptable media types: $acceptable")
  }

  private val notAcceptable: PartialFunction[MediaRange, Result] = {
    case _ => notAcceptedResponse
  }

  private val notAcceptableAsync
    : PartialFunction[MediaRange, Future[Result]] = {
    case _ => Future.successful(notAcceptedResponse)
  }

}

private[playcontentnegotiation] class SingleAcceptFn(accepting: Accepting,
                                                     responder: () => Result)
    extends AbstractPartialFunction[MediaRange, Result] {
  override def isDefinedAt(x: MediaRange): Boolean = x.accepts(accepting.mimeType)
  override def applyOrElse[A1 <: MediaRange, B1 >: Result](
      x: A1,
      default: A1 => B1): B1 =
    if (isDefinedAt(x)) responder() else default(x)
}
private[playcontentnegotiation] object SingleAcceptFn {
  def apply[A](a: Representation[A] => Result)(
      responderForType: Representation[A]): SingleAcceptFn =
    new SingleAcceptFn(responderForType.accepting, () => a(responderForType))
}

private[playcontentnegotiation] class SingleAsyncAcceptFn(
    accepting: Accepting,
    responder: () => Future[Result])
    extends AbstractPartialFunction[MediaRange, Future[Result]] {
  override def isDefinedAt(x: MediaRange): Boolean = x.accepts(accepting.mimeType)
  override def applyOrElse[A1 <: MediaRange, B1 >: Future[Result]](
      x: A1,
      default: A1 => B1): B1 =
    if (isDefinedAt(x)) responder() else default(x)
}
private[playcontentnegotiation] object SingleAsyncAcceptFn {
  def apply[A](a: Representation[A] => Future[Result])(
      responderForType: Representation[A]): SingleAsyncAcceptFn =
    new SingleAsyncAcceptFn(responderForType.accepting,
                            () => a(responderForType))
}
