package com.myproject.play.acl

import play.api.mvc.Results._
import play.api.mvc._

import scala.async.Async._
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Surendra on 4/15/16.
 */
class AuthDefinition (authenticatedAction: AuthenticatedAction, accessDef: AccessDefinition) {

  def in[A](bodyParser: BodyParser[A])(block: AuthenticatedRequest[A] => Future[Result]): Action[A] = authenticatedAction.async(bodyParser) { implicit request =>

    async {
      await(accessDef.isAllowed(request.userId)) match { // check user passes access definition
          case false => Unauthorized
          case true => await(block(request))
      }
    } recover {
      case NonFatal(e) => InternalServerError
    }
  }

  def in(block: AuthenticatedRequest[AnyContent] => Future[Result]): Action[AnyContent] = in(BodyParsers.parse.default)(block)

  def in(block: => Future[Result]): Action[AnyContent] = in(_ => block)
}
