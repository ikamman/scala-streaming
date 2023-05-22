package com.example.api

import AppConfig._

case class AppConfig(port: Int, generatorConfig: GenConfig)

object AppConfig {
  case class GenConfig(url: String)
}
