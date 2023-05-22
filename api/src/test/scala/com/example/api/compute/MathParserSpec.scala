package com.example.api.compute

import org.scalatest.flatspec.AnyFlatSpec
import atto.Atto._
import atto.ParseResult._
import org.scalatest.matchers.should.Matchers
import com.example.api.compute.MathParser
import com.example.api.compute.MathOps

class MathParserSpec extends AnyFlatSpec with Matchers {

  import MathParser._
  import MathOps._

  "Math parser" should "parse +" in {

    Exp.parse("somevar+5").done shouldBe Done("", Add(Var("somevar"), Lit(5)))
    Exp.parse("5+somevar").done shouldBe Done("", Add(Lit(5), Var("somevar")))
    Exp.parse("someothervar+somevar").done shouldBe Done(
      "",
      Add(Var("someothervar"), Var("somevar"))
    )
  }

  it should "parse -" in {
    Exp.parse("somevar-5").done shouldBe Done("", Sub(Var("somevar"), Lit(5)))
    Exp.parse("5-somevar").done shouldBe Done("", Sub(Lit(5), Var("somevar")))
    Exp.parse("someothervar-somevar").done shouldBe Done(
      "",
      Sub(Var("someothervar"), Var("somevar"))
    )
  }

  it should "parse *" in {
    Exp.parse("somevar*5").done shouldBe Done("", Mult(Var("somevar"), Lit(5)))
    Exp.parse("somevar*-5").done shouldBe Done(
      "",
      Mult(Var("somevar"), Lit(-5))
    )
    Exp.parse("5*somevar").done shouldBe Done("", Mult(Lit(5), Var("somevar")))
    Exp.parse("someothervar*somevar").done shouldBe Done(
      "",
      Mult(Var("someothervar"), Var("somevar"))
    )
  }

  it should "parse /" in {
    Exp.parse("somevar/5").done shouldBe Done("", Div(Var("somevar"), Lit(5)))
    Exp.parse("somevar/-5").done shouldBe Done(
      "",
      Div(Var("somevar"), Lit(-5))
    )
    Exp.parse("5/somevar").done shouldBe Done("", Div(Lit(5), Var("somevar")))
    Exp.parse("someothervar/somevar").done shouldBe Done(
      "",
      Div(Var("someothervar"), Var("somevar"))
    )
  }

  it should "parse sqrt" in {
    Exp.parse("sqrt(somevar)").done shouldBe Done("", Sqrt(Var("somevar")))
    Exp.parse("sqrt(5)").done shouldBe Done("", Sqrt(Lit(5)))
    Exp.parse("sqrt(-5)").done shouldBe Done("", Sqrt(Lit(-5)))
  }

  it should "parse x**y " in {

    Exp.parse("5**2").done shouldBe Done("", Pow(Lit(5), Lit(2)))
    Exp.parse("somevar**5").done shouldBe Done("", Pow(Var("somevar"), Lit(5)))
    Exp.parse("5**somevar").done shouldBe a[Fail[*]]
  }
}
