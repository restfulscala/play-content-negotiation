# play-content-negotiation

[![Build Status](https://api.travis-ci.org/restfulscala/play-content-negotiation.svg?branch=master
)](https://api.travis-ci.org/restfulscala/play-content-negotiation)
[![codecov](https://codecov.io/gh/restfulscala/play-content-negotiation/branch/master/graph/badge.svg)](https://codecov.io/gh/restfulscala/play-content-negotiation)

This library provides a more declarative way of content format negotiation for the [Play framework](https://playframework.com/).

Play's content negotiation support is based on pattern matching. Unfortunately, there is a problem with this approach: If the server needs to return a ` 406 Not Acceptable` HTTP response, the Play framework has no way of providing a list of supported content types in the response, which would be very helpful for developers programming clients for your web application.

`play-content-negotiation` aims to solve this by allowing you to specify which content types are supported, and how to render them, declaratively. This way, content negotiation and rendering can be decoupled.

## Setup

`play-content-negotiation` is compiled against Play 2.5.10 and Scala 2.11. To use it in your Play application, add the following to your `build.sbt` file:

```scala
resolvers += "restful-scala" at "https://dl.bintray.com/restfulscala/maven"
libraryDependencies += "org.restfulscala" %% "play-content-negotiation" % "0.3.0"
```

## Concepts

`play-content-negotiation` introduces a few new types and a tiny DSL for creating instances of these types, and is built on top of Play's own content negotiation support.

### Representation

First of all, the library adds a trait called `Representation`:

```scala
import play.api.mvc._

trait Representation[A] {
  def accepting: Accepting
  def respond(a: A, status: Int): Result
}
```

A `Representation` has an `Accepting` field, which is a class provided by Play and allows to determine whether a given mime type matches an HTTP `Accept` header. In addition, it provides a way to create a Play `Result` for a type `A`, using the given status code.

### RespondWith

In addition to `Representation`, the library introduces the `RespondWith` type, which looks like this:

```scala
import play.api.mvc._

class RespondWith[A](representations: List[Representation[A]]) {
  def apply(a: Representation[A] => Result)(implicit req: RequestHeader): Result = ???
  def async(a: Representation[A] => Future[Result])(implicit req: RequestHeader): Future[Result] = ???
}
```

A `RespondWith` instance is created from a list of representations, and it knows how to create a Play `Result` from a function `Representation[A] => Result` or `Representation[A] => Future[Result]`. If the representations known by the `RespondWith` match the `Accept` header of the implicit Play `RequestHeader`, it will apply the matching `Representation` to the passed in function, which is responsible for getting the required value of `A` and returning a `Result`. If no `Representation` matches, it will instead return a `Not Acceptable` response whose body contains information about the supported content types. Use `async` if getting the value of `A` requires IO, for example, if you need to fetch it from a database.

## Usage

In order to make use of `play-content-negotiation`, you need to mix the `ContentNegotiation` trait into your controller. `ContentNegotiation` defines two methods that, together, provide a tiny DSL for creating `Representation`s and `RespondWith`s. Here are the signatures of these two methods:

```scala
trait ContentNegotiation {
  def represent[A](representations: Representation[A]*): RespondWith[A]
  def as[A, B : Writeable](accepting: Accepting, representationFactory: A => B): Representation[A]
}
```

Use `represent` to create a `RespondWith` from one or more `Representation`s. Use `as` to create a `Representation[A]` from a Play `Accepting` and a function that turns `A` into a `B` for which a Play `Writeable` exists.

To illustrate this, here is an example controller:

```scala
import play.api.mvc._

class Sales(saleRepository: SaleRepository) extends Controller with ContentNegotiation {
  def negotiate()(implicit req: RequestHeader) = represent[Sale](
    as(Accepts.Html, views.html.sale(_)),
    as(Accepts.Json, Json.toJson(_))
  )
}
```

Here, we support two representations of a `Sale`, `text/html` and `application/json`. The next step is to use the `RespondWith[Sale]` returned by our `negotiate` function in our actions. One such action may want to add to our controller could look like this:

```scala
def get(saleId: String) = Action.async { implicit req =>
  negotiate().async { variant =>
    saleRepository.findById(SaleId(saleId)) map {
      case Some(sale) => variant.respond(sale, 200)
      case None => NotFound
    }
  }
}
```

The first thing we do is call the `async` method of our `RespondWith[Sale]`. The function we pass to `async` fetches a `Sale` from a repository and, if it exists, it uses the `Representation` the `RespondWith` has determined to use in order to create a `200` result. If no `Sale` can be found, it returns a `404` response instead. If the client asks for an unsupported content type, the `RespondWith` will return a `406` response like this:

```
curl localhost:9000/sales/1 -H"Accept: application/xml"

HTTP/1.1 406 Not Acceptable
Content-Length: 51
Content-Type: text/plain; charset=utf-8
Vary: Accept

Acceptable media types: text/html, application/json
```

## Contributors

Daniel Westheide

