package com.myproject.play.acl

import play.api.mvc.ActionBuilder

/**
 * Created by Surendra on 4/15/16.
 */
trait AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {

  def allowTo(accessDefinition: AccessDefinition) = new AuthDefinition(this, accessDefinition)
}
