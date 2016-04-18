package com.myproject.play.acl

import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

/**
 * Created by Surendra on 4/15/16.
 */
class AclModule extends AbstractModule {

  override def configure(): Unit = {
    install(new FactoryModuleBuilder().
      implement(classOf[AuthDefinition], classOf[AuthDefinition]).
      build(classOf[AclFactory])
    )
  }
}

trait AclFactory {

  def createAuthDef(role: String, accessDefinition: AccessDefinition): AuthDefinition
}
