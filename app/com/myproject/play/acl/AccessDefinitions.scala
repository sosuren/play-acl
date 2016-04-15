package com.myproject.play.acl

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Surendra on 4/15/16.
 */
abstract class AccessDefinition {

  final def or(rule: AccessDefinition) = new ORTree(List(this, rule))

  final def and(rule: AccessDefinition) = new ANDTree(List(this, rule))

  def isAllowed(userId: Int): Future[Boolean]
}

/**
 * Composed access definition
 * @param accessDefs List of access definitions
 */
sealed abstract class AccessTree (accessDefs: List[AccessDefinition]) extends AccessDefinition

/**
 * Composed access definition which allows if any one definition is true
 * @param accessDefs List of definitions
 */
final class ORTree (val accessDefs: List[AccessDefinition]) extends AccessTree(accessDefs) {

  override def isAllowed(userId: Int): Future[Boolean] = {
    val futures = accessDefs map (_.isAllowed(userId))
    Future.sequence(futures) map { flag =>
      flag.foldLeft(false)(_ || _)
    }
  }
}

/**
 * Composed access definition which allows if every definition is true
 * @param accessDefs List of definitions
 */
final class ANDTree (val accessDefs: List[AccessDefinition]) extends AccessTree(accessDefs) {

  override def isAllowed(userId: Int): Future[Boolean] = {
    val futures = accessDefs map (_.isAllowed(userId))
    Future.sequence(futures) map { flag =>
      flag.foldLeft(true)(_ && _)
    }
  }
}

/**
 * Unit rule that can be executed
 */
trait AccessRule extends AccessDefinition with Function[Int, Future[Boolean]] {

  override def isAllowed(userId: Int): Future[Boolean] = this(userId)
}

/**
 * Allows all
 */
object AllowAll {

  class AllowAll extends AccessRule {

    override def apply(v1: Int): Future[Boolean] = Future { true }
  }

  def apply() = new AllowAll()
}
