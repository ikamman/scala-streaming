package com.example.gen

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = GeneratorServer.run[IO]
}
