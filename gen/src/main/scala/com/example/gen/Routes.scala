package com.example.gen

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import cats.effect.Async

object Routes {

  def generateJsonRoute[F[_]: Async](G: Generator[F]) = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "generate" / "json" / size =>
      Ok(G.generate(size.toLong))
    }
  }
}
