package com.example.api.compute

import io.circe.Json

object MathOps {

  type LitExtractor[A] = String => A => Option[Double]

  sealed trait Val {
    def valueOr(f: String => Option[Double]): Option[Double] = this match {
      case lit: Lit  => Some(lit.value)
      case Var(name) => f(name)
    }
  }
  case class Var(name: String) extends Val
  case class Lit(value: Double) extends Val

  abstract class MathOp(op: (Double, Double) => Double) {
    def l: Val
    def r: Val

    def compute[A](value: A)(implicit le: LitExtractor[A]): Option[Double] =
      (
        l.valueOr(name => le(name)(value)),
        r.valueOr(name => le(name)(value))
      ) match {
        case (Some(l), Some(r)) => Some(op(l, r))
        case _                  => None
      }
  }

  abstract class MathSingleOp(id: Double, op: Double => Double)
      extends MathOp((l, _) => op(l)) {
    def r: Val = Lit(id)
  }
  case class Add(l: Val, r: Val) extends MathOp(_ + _)
  case class Sub(l: Val, r: Val) extends MathOp(_ - _)
  case class Div(l: Val, r: Val) extends MathOp(_ / _)
  case class Mult(l: Val, r: Val) extends MathOp(_ * _)
  case class Sqrt(l: Val) extends MathSingleOp(1, scala.math.sqrt)
  case class Pow(l: Val, r: Lit) extends MathOp(scala.math.pow)

  implicit val jsonLitExtractor: LitExtractor[Map[String, Json]] =
    name => json => json.get(name).flatMap(_.asNumber.map(_.toDouble))
}
