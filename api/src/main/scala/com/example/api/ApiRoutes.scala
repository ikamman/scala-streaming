package com.example.api

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.Async
import org.http4s.headers._
import org.http4s.MediaType
import com.example.api.compute.MathParser

object Routes {

  def routes[F[_]: Async](apiService: ApiService[F]) = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / LongVar(size) =>
        Ok(
          apiService.getData(size),
          `Content-Type`(MediaType.text.csv)
        )
      case GET -> Root / "fields" / fields / LongVar(size) =>
        Ok(
          apiService.getDataWithFields(fields.split(",").toList, size),
          `Content-Type`(MediaType.text.csv)
        )
      case GET -> Root / "compute" / computations / LongVar(size) =>
        MathParser.parseArg(computations) match {
          case Left(value) =>
            BadRequest(s"Failed to parse computations: $value")
          case Right(ops) =>
            Ok(
              apiService.getDataWithComputations(ops, size),
              `Content-Type`(MediaType.text.csv)
            )
        }

    }
  }
}
