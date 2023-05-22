package com.example.api

import cats.effect.Async
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.http4s.ember.client.EmberClientBuilder
import com.example.api.GenService
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object GeneratorServer {

  def run[F[_]: Async]: F[Nothing] = {

    for {
      client <- EmberClientBuilder.default[F].build
      config = ConfigSource.default.loadOrThrow[AppConfig]
      generatorService = GenService.impl[F](client, config.generatorConfig)
      apiService = ApiService.impl[F](generatorService)
      httpApp = (Routes.routes[F](apiService)).orNotFound
      appWithLogger = Logger.httpApp(true, true)(httpApp)
      _ <- EmberServerBuilder
        .default[F]
        .withHost(ipv4"0.0.0.0")
        .withPort(Port.fromInt(config.port).getOrElse(port"8081"))
        .withHttpApp(appWithLogger)
        .build
    } yield ()
  }.useForever
}
