package org.restfulscala.playcontentnegotiation

import play.api.http.Writeable
import play.api.mvc.Accepting

trait ContentNegotiation {
  def represent[A](representations: Representation[A]*): RespondWith[A] =
    new RespondWith(representations.toList)

  def as[A, B : Writeable](accepting: Accepting, representationFactory: A => B): Representation[A] =
    new SimpleRepresentation[A, B](accepting, representationFactory)

}
