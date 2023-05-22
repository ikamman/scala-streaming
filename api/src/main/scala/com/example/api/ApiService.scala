package com.example.api

import io.circe.Json
import fs2._
import com.example.api.compute.MathOps._

trait ApiService[F[_]] {
  def getData(size: Long): Stream[F, String]
  def getDataWithFields(fields: List[String], size: Long): Stream[F, String]
  def getDataWithComputations(
      computations: Map[String, MathOp],
      size: Long
  ): Stream[F, String]
}

object ApiService {

  type JProps = Map[String, Json]
  type Props = Map[String, String]

  def impl[F[_]](generatorService: GeneratorService[F]) =
    new ApiService[F] {

      val flattenJson: JProps => JProps = _.flatMap {
        case (_, obj) if obj.isObject => flattenJson(obj.asObject.get.toMap)
        case value: Any               => value :: Nil
      }

      val csvRow: Pipe[F, List[String], String] =
        _.map(_.mkString(",")).map(_ + "\n")

      val stringify: Pipe[F, JProps, Props] =
        _.map(_.map(v => (v._1, v._2.toString())))

      val valuesWithHeader: Pipe[F, Props, List[String]] =
        _.zipWithIndex
          .flatMap {
            case (props, idx) if idx == 0 =>
              Stream.emits(
                List(props.keys.toList, props.values.toList)
              )
            case (props, _) => Stream.emit(props.values.toList)
          }

      val propertySelection: List[String] => Pipe[F, JProps, JProps] =
        props => _.map(_.filter(kv => props.contains(kv._1)))

      private def getDataAsJsonValues(size: Long) = generatorService
        .generate(size)
        .map(_.asObject)
        .filter(_.isDefined)
        .map(_.get.toMap)
        .map(flattenJson)

      def getData(size: Long): Stream[F, String] =
        getDataAsJsonValues(size)
          .through(stringify)
          .through(valuesWithHeader)
          .through(csvRow)

      def getDataWithFields(
          fields: List[String],
          size: Long
      ): Stream[F, String] =
        getDataAsJsonValues(size)
          .through(propertySelection(fields))
          .through(stringify)
          .through(valuesWithHeader)
          .through(csvRow)

      def getDataWithComputations(
          computations: Map[String, MathOp],
          size: Long
      ): Stream[F, String] = getDataAsJsonValues(size)
        .map(props =>
          computations.map { case (comp, op) =>
            (comp, op.compute(props).map(_.toString).getOrElse("failure"))
          }
        )
        .through(valuesWithHeader)
        .through(csvRow)
    }
}
