# play-content-negotiation

[![Build Status](https://api.travis-ci.org/restfulscala/play-content-negotiation.svg?branch=master
)](https://api.travis-ci.org/restfulscala/play-content-negotiation)
[![codecov](https://codecov.io/gh/restfulscala/play-content-negotiation/branch/master/graph/badge.svg)](https://codecov.io/gh/restfulscala/play-content-negotiation)

This library provides a more declarative and DRY way of content format negotiation for the [Play framework](https://playframework.com/).

Play's content negotiation support is based on pattern matching. Unfortunately, there are a few problems with this approach:

- if the server needs to return a `Not Acceptable` HTTP response, the Play framework has no way of providing a list of supported content types in the response, which would be very helpful for developers programming clients for your web application
- it's easy to do the wrong thing with Play's content negotiation support, specifically, to do unnecessary database lookups or even perform side effects, even if the server cannot respond with the content type requested by the client
- to mitigate the former, quite a bit of boilerplate code is required

`play-content-negotiation` aims to solve these problems by allowing you to specify which content types are supported, and how to render them, declaratively. This way, content negotiation and rendering can be decoupled without requiring any boilerplate code.

## Setup

`play-content-negotiation` is compiled against Play 2.5.10 and Scala 2.11. To use it in your Play application, add the following to your `build.sbt` file:

```scala
resolvers += "restful-scala" at "https://dl.bintray.com/restfulscala/maven"
libraryDependencies += "org.restfulscala" %% "play-content-negotiation" % "0.3.0"
```

## Usage

`play-content-negotiation` introduces a few new types and a tiny DSL for creating instances of these types, and is built on top of Play's own content negotiation support.

First of all, the library adds a trait called `Representation`:

```scala
import play.api.mvc._

trait Representation[A] {
    def accepting: Accepting
    def respond(a: A, status: Int): Result
  }
```

A `Representation` has an `Accepting` field, which is a class provided by Play and allows to determine whether a given mime type matches an HTTP `Accept` header. In addition, it provides a way to create a Play `Result` for a type `A`, using the given status code.

In addition to `Representation`, the library introduces the `RespondWith` type, which looks like this:

```scala
import play.api.mvc._

class RespondWith[A](representations: List[Representation[A]]) {
  def apply(a: Representation[A] => Result)(implicit req: RequestHeader): Result = ???
  def async(a: Representation[A] => Future[Result])(implicit req: RequestHeader): Future[Result] = ???
}
```

A `RespondWith` instance is created from a list of representations, and it knows how to create a Play `Result` from a function `Representation[A] => Result` or `Representation[A] => Future[Result]`. If the representations known by the `RespondWith` match the `Accept` header of the implicit Play `RequestHeader`, it will apply the matching `Representation` to the passed in function, which is responsible for getting the required value of `A` and returning a `Result`. If no `Representation` matches, it will instead return a `Not Acceptable` response whose body contains information about the supported content types.
