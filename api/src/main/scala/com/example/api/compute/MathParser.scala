package com.example.api.compute

import atto._, Atto._

import com.example.api.compute.MathOps._

object MathParser {

  val VarPattern = "[a-zA-Z|0-9|_]".r

  val ValP: Parser[Val] =
    double.map(Lit) | many1(satisfy(c => VarPattern.matches("" + c)))
      .map(name => Var(name.toList.mkString))

  val AddP: Parser[Add] = for {
    l <- ValP
    _ <- char('+')
    r <- ValP
  } yield Add(l, r)

  val SubP: Parser[Sub] = for {
    l <- ValP
    _ <- char('-')
    r <- ValP
  } yield Sub(l, r)

  val MultP: Parser[Mult] = for {
    l <- ValP
    _ <- char('*')
    r <- ValP
  } yield Mult(l, r)

  val DivP: Parser[Div] = for {
    l <- ValP
    _ <- char('/')
    r <- ValP
  } yield Div(l, r)

  val SqrtP: Parser[Sqrt] = for {
    _ <- string("sqrt")
    _ <- char('(')
    l <- ValP
    _ <- char(')')
  } yield Sqrt(l)

  val PowP: Parser[Pow] = for {
    l <- ValP
    _ <- string("**")
    d <- double.map(Lit)
  } yield Pow(l, d)

  val Exp: Parser[MathOp] = PowP | AddP | SubP | MultP | DivP | SqrtP

  def parseArg(
      arg: String
  ): Either[String, Map[String, MathOp]] = {
    arg
      .split(",")
      .map(comp => Exp.parse(comp).done.map(r => comp -> r).either)
      .toList
      .partitionMap(identity) match {
      case (Nil, rights)  => Right(rights.toMap)
      case (left :: _, _) => Left(left)
    }
  }
}
