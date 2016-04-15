package com.myproject.play.acl

import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.myproject.play.AuthDsl
import play.api.mvc.Results._
import play.api.mvc._

import scala.async.Async._
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Surendra on 4/15/16.
 */
class AuthDefinition @Inject() (authenticatedAction: AuthenticatedAction, @Assisted() role: String, @Assisted() accessDef: AccessDefinition = AllowAll()) {

  def allowTo[A](accessDef: AccessDefinition) = AuthDsl.aclFactory.createAuthDef(role, accessDef)

  def in[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result]): Action[A] = authenticatedAction.async(bodyParser) { implicit request =>

    async {
      await(accessDef.isAllowed(request.userId)) match {
        case false => BadRequest
        case true => await(block(request))
      }
    } recover {
      case NonFatal(e) => InternalServerError
    }
  }

  def in(block: AuthenticatedRequest[AnyContent] => Future[Result]): Action[AnyContent] = in(BodyParsers.parse.default)(block)

  def in(block: => Future[Result]): Action[AnyContent] = in(_ => block)
}

