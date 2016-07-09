package com.myproject.play.acl

import org.specs2.mock.Mockito
import org.specs2.mutable._

import scala.concurrent.{Awaitable, Future, Await}
import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by Surendra on 6/1/16.
 */
class AccessDefinitionsUnitTest extends Specification with Mockito {

  def execTime[T](toWait: Awaitable[T], atMost: Duration): Long = {
    val startTime = System.currentTimeMillis()
    Await.result(toWait, atMost)
    val endTime = System.currentTimeMillis()
    endTime - startTime
  }

  "ORTree" should {

    "return true if one rule is valid" in {

      val accessDefTrue = mock[AccessDefinition]
      accessDefTrue.isAllowed(anyInt) returns Future { true }
      val accessDefFalse = mock[AccessDefinition]
      accessDefFalse.isAllowed(anyInt) returns Future { false }

      val orTree = new ORTree(List(accessDefTrue, accessDefFalse))

      Await.result(orTree.isAllowed(1), 2 seconds) must beTrue
    }

    "return false if none of is valid" in {

      val accessDefFalse1 = mock[AccessDefinition]
      accessDefFalse1.isAllowed(anyInt) returns Future { false }
      val accessDefFalse2 = mock[AccessDefinition]
      accessDefFalse2.isAllowed(anyInt) returns Future { false }

      val orTree = new ORTree(List(accessDefFalse1, accessDefFalse2))

      Await.result(orTree.isAllowed(Random.nextInt), 2 seconds) must beFalse
    }

    "return on first completed" in {

      val firstCompletingThreadSleep = 300
      val offsetSleep = 200

      val accessDef1 = mock[AccessDefinition]
      accessDef1.isAllowed(anyInt) returns Future {
        Thread.sleep(firstCompletingThreadSleep)
        true
      }

      val accessDef2 = mock[AccessDefinition]
      accessDef2.isAllowed(anyInt) returns Future {
        Thread.sleep(firstCompletingThreadSleep + offsetSleep + Random.nextLong())
        true
      }

      val orTree = new ORTree(List(accessDef1, accessDef2))

      execTime(orTree.isAllowed(Random.nextInt()), 2 seconds) must be_<=(firstCompletingThreadSleep + offsetSleep + 0L)
     }
  }

  "AndTree" should {

    "return true only if all rule is valid" in {

      val accessDefTrue1 = mock[AccessDefinition]
      accessDefTrue1.isAllowed(anyInt) returns Future { true }
      val accessDefTrue2 = mock[AccessDefinition]
      accessDefTrue2.isAllowed(anyInt) returns Future { true }

      val andTree = new ANDTree(List(accessDefTrue1, accessDefTrue2))

      Await.result(andTree.isAllowed(Random.nextInt), 2 seconds) must beTrue
    }

    "return false if any rule is invalid" in {

      val accessDefTrue = mock[AccessDefinition]
      accessDefTrue.isAllowed(anyInt) returns Future { true }
      val accessDefFalse = mock[AccessDefinition]
      accessDefFalse.isAllowed(anyInt) returns Future { false }

      val andTree = new ANDTree(List(accessDefTrue, accessDefFalse))

      Await.result(andTree.isAllowed(Random.nextInt), 2 seconds) must beFalse
    }
  }


  "AccessRule" should {

    "return true for valid case" in {

      val IsArgOne = new AccessRule {
        override def apply(v1: Int): Future[Boolean] = Future.successful({ v1 == 1 })
      }

      Await.result(IsArgOne(1), 2 seconds) must beTrue
      Await.result(IsArgOne(2), 2 seconds) must beFalse
    }
  }

  "AllowAll" should {

    "return always true" in {

      Await.result(AllowAll()(Random.nextInt()), 2 seconds) must beTrue
    }
  }
}
