package com.myproject.play

import com.myproject.play.acl.{AuthDefinition, AclFactory}
import play.api.Play

/**
 * Created by Surendra on 4/15/16.
 */
object AuthDsl {

  object Identify {

    def as(role: String): AuthDefinition = AuthDsl.aclFactory.createAuthDef(role)
  }

  val aclFactory: AclFactory = Play.current.injector.instanceOf(classOf[AclFactory])
}
