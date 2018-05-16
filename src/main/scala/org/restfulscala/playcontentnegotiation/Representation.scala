package org.restfulscala.playcontentnegotiation

import play.api.http.Writeable
import play.api.mvc.{Accepting, Result, Results}

trait Representation[A] {
  def accepting: Accepting
  def respond(a: A, status: Int): Result
}

class SimpleRepresentation[A, B](
    val accepting: Accepting,
    val representationFactory: A => B)(implicit writeable: Writeable[B])
    extends Representation[A] {
  override def respond(a: A, status: Int): Result = {
    Results.Status(status)(representationFactory(a))
  }
}
