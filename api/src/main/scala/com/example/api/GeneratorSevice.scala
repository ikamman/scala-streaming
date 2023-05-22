package com.example.api

import io.circe.Json
import io.circe.jawn.CirceSupportParser
import org.http4s.Request
import org.http4s.Method
import org.http4s.Uri
import org.typelevel.jawn.fs2._
import cats.effect.kernel.Async
import org.http4s.client.Client
import fs2._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait GeneratorService[F[_]] {
  def generate(size: Long): Stream[F, Json]
}

object GenService {
  case class ConnectionFailure(msg: String) extends Exception

  implicit val f = new CirceSupportParser(None, false).facade

  def impl[F[_]: Async](
      client: Client[F],
      genConfig: AppConfig.GenConfig
  ): GeneratorService[F] =
    new GeneratorService[F] {

      val logger: Logger[F] = Slf4jLogger.getLogger[F]

      def generate(size: Long): Stream[F, Json] = {
        val req =
          Request[F](
            Method.GET,
            Uri.unsafeFromString(
              s"${genConfig.url}/generate/json/${size.toString()}"
            )
          )
        client
          .stream(req)
          .flatMap { res =>
            if (res.status.isSuccess) res.body.chunks.unwrapJsonArray
            else {
              Stream
                .eval(
                  logger.error(
                    s"Cannot connect to generator through: ${genConfig.url}"
                  )
                )
                .flatMap(_ => Stream.empty)
            }
          }
      }
    }
}
