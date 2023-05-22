package com.example.gen

import cats.effect.Async
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object GeneratorServer {

  def run[F[_]: Async]: F[Nothing] = {
    val httpApp = (Routes.generateJsonRoute[F](Generator.impl[F])).orNotFound
    val appWithLogger = Logger.httpApp(true, true)(httpApp)
    val config = ConfigSource.default.loadOrThrow[AppConfig]

    EmberServerBuilder
      .default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(Port.fromInt(config.port).getOrElse(port"8081"))
      .withHttpApp(appWithLogger)
      .build
  }.useForever
}
