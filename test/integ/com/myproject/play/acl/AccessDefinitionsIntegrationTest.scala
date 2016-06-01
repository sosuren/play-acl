package com.myproject.play.acl

import org.specs2.Specification
import org.specs2.specification.core.SpecStructure

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
/**
 * Created by Surendra on 6/1/16.
 */
class AccessDefinitionsIntegrationTest extends Specification {
  override def is: SpecStructure = s2"""
    Composing AccessDefinitions Specifications
      where 6 is divisible by 3                       $num6IsDivisibleBy3
      where 7 is not divisible by 3                   $num7IsNotDivisibleBy3
      where 10 is divisible by 5                      $num10IsDivisibleBy5
      where 11 is not divisible by 3                  $num11IsNotDivisibleBy5
      where 9 is divisible by (3 Or 5)                $num9IsDivisibleBy3Or5
      where 10 is divisible by (3 Or 5)               $num10IsDivisibleBy3Or5
      where 15 is divisible by (3 Or 5)               $num15IsDivisibleBy3Or5
      where 8 is not divisible by (3 Or 5)            $num8IsNotDivisibleBy3Or5
      where 15 is divisible by (3 And 5)              $num15IsDivisibleBy3And5
      where 13 is not divisible by (3 And 5)          $num13IsNotDivisibleBy3And5
      where 14 is divisible by (3 And 5) Or 7         $num14IsDivisibleBy3And5_Or7
      where 15 is divisible by (3 And 5) Or 7         $num15IsDivisibleBy3And5_Or7
      where 25 is not divisible by (3 And 5) Or 7     $num25IsNotDivisibleBy3And5_Or7
  """

  val IsDivisibleBy3 = new AccessRule {
    override def apply(v1: Int): Future[Boolean] = Future.successful({ v1 % 3 == 0 })
  }

  val IsDivisibleBy5 = new AccessRule {
    override def apply(v1: Int): Future[Boolean] = Future.successful({ v1 % 5 == 0 })
  }

  val IsDivisibleBy7 = new AccessRule {
    override def apply(v1: Int): Future[Boolean] = Future.successful({ v1 % 7 == 0 })
  }

  val IsDivisibleBy3Or5 = IsDivisibleBy3 or IsDivisibleBy5
  val IsDivisibleBy3And5 = IsDivisibleBy3 and IsDivisibleBy5
  val IsDivisibleBy3And5_Or7 = IsDivisibleBy3And5 or IsDivisibleBy7

  def num6IsDivisibleBy3 = Await.result(IsDivisibleBy3.isAllowed(6), 1 second) must beTrue
  def num7IsNotDivisibleBy3 = Await.result(IsDivisibleBy3.isAllowed(7), 1 second) must beFalse

  def num10IsDivisibleBy5 = Await.result(IsDivisibleBy5.isAllowed(10), 1 second) must beTrue
  def num11IsNotDivisibleBy5 = Await.result(IsDivisibleBy5.isAllowed(11), 1 second) must beFalse

  def num9IsDivisibleBy3Or5 = Await.result(IsDivisibleBy3Or5.isAllowed(9), 1 second) must beTrue
  def num10IsDivisibleBy3Or5 = Await.result(IsDivisibleBy3Or5.isAllowed(10), 1 second) must beTrue
  def num15IsDivisibleBy3Or5 = Await.result(IsDivisibleBy3Or5.isAllowed(15), 1 second) must beTrue
  def num8IsNotDivisibleBy3Or5 = Await.result(IsDivisibleBy3Or5.isAllowed(8), 1 second) must beFalse

  def num15IsDivisibleBy3And5 = Await.result(IsDivisibleBy3And5.isAllowed(15), 1 second) must beTrue
  def num13IsNotDivisibleBy3And5 = Await.result(IsDivisibleBy3And5.isAllowed(13), 1 second) must beFalse

  def num14IsDivisibleBy3And5_Or7 = Await.result(IsDivisibleBy3And5_Or7.isAllowed(14), 1 second) must beTrue
  def num15IsDivisibleBy3And5_Or7 = Await.result(IsDivisibleBy3And5_Or7.isAllowed(15), 1 second) must beTrue
  def num25IsNotDivisibleBy3And5_Or7 = Await.result(IsDivisibleBy3And5_Or7.isAllowed(25), 1 second) must beFalse
}
