package com.myproject.play.acl

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This base class to create Access Control Rules
 * Created by Surendra on 4/15/16.
 */
abstract class AccessDefinition {

  /**
   * This returns new Access Tree applying OR rule between them
   * @param rule Next Access Definition rule to apply OR rule
   * @return [[ORTree]]
   */
  final def or(rule: AccessDefinition) = new ORTree(List(this, rule))

  /**
   * This returns new Access Tree applying AND rule between them
   * @param rule Next Access Definition rule to apply AND rule
   * @return [[ANDTree]]
   */
  final def and(rule: AccessDefinition) = new ANDTree(List(this, rule))

  /**
   * Checks if the user passes this access definition
   * @param userId ID of User
   * @return Future of Boolean
   */
  def isAllowed(userId: Long): Future[Boolean]
}

/**
 * Compose access definition
 * @param accessDefs List of access definitions
 */
sealed abstract class AccessTree (accessDefs: List[AccessDefinition]) extends AccessDefinition

/**
 * Composed access definition which allows if any one definition is true
 * @param accessDefs List of definitions
 */
final class ORTree (val accessDefs: List[AccessDefinition]) extends AccessTree(accessDefs) {

  override def isAllowed(userId: Long): Future[Boolean] = {
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

  override def isAllowed(userId: Long): Future[Boolean] = {
    val futures = accessDefs map (_.isAllowed(userId))
    Future.sequence(futures) map { flag =>
      flag.foldLeft(true)(_ && _)
    }
  }
}

/**
 * Unit rule that can be executed
 */
trait AccessRule extends AccessDefinition with Function[Long, Future[Boolean]] {

  override def isAllowed(userId: Long): Future[Boolean] = this(userId)
}

/**
 * Allows all
 */
object AllowAll {

  class AllowAll extends AccessRule {

    override def apply(v1: Long): Future[Boolean] = Future { true }
  }

  def apply() = new AllowAll()
}
