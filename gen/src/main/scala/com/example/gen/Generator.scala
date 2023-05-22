package com.example.gen

import cats._
import cats.effect._
import org.scalacheck.Gen
import io.circe.Json

trait Generator[F[_]] {
  def generate(size: Long): fs2.Stream[F, Json]
}

object Generator {

  final case class Name(name: String) extends AnyVal

  def generateOne(id: Long): Option[Json] = (for {
    name <- Gen.oneOf("Wiśniowa", "Śliwkowa", "Jabłkowa", "Cytrynowa")
    country <- Gen.oneOf(Seq(("Poland", "PL")))
    fullName <- Gen.const(s"$name, ${country._1}")
    latitude <- Gen.choose[Double](-90, 90)
    longitude <- Gen.choose[Double](-90, 90)
    locationId <- Gen.posNum[Int]
    fromEurope <- Gen.oneOf(true, false)
    coreCountry <- Gen.oneOf(true, false)
  } yield Json.obj(
    "_type" -> Json.fromString("Position"),
    "_id" -> Json.fromLong(id),
    "key" -> Json.Null,
    "name" -> Json.fromString(name),
    "fullName" -> Json.fromString(fullName),
    "iata_airport_code" -> Json.Null,
    "country" -> Json.fromString(country._1),
    "geo_position" -> Json.obj(
      "latitude" -> Json.fromDoubleOrNull(latitude),
      "longitude" -> Json.fromDoubleOrNull(longitude)
    ),
    "location_id" -> Json.fromInt(locationId),
    "inEurope" -> Json.fromBoolean(fromEurope),
    "countryCode" -> Json.fromString(country._2),
    "coreCountry" -> Json.fromBoolean(coreCountry),
    "distance" -> Json.Null
  )).sample

  def impl[F[_]: Concurrent]: Generator[F] = new Generator[F] {
    def generate(size: Long): fs2.Stream[F, Json] =
      fs2.Stream
        .range[F, Long](0, size)
        .parEvalMap(10)(id => Applicative[F].pure(generateOne(id)))
        .filter(_.isDefined)
        .map(_.get)

  }
}
