package com.myproject.play.acl

import java.security.cert.X509Certificate

import play.api.mvc.Request

/**
 * Created by Surendra on 4/5/16.
 */
trait AuthenticatedRequest[+A] extends Request[A]{

  val userId: Int
}

object AuthenticatedRequest {

  def apply[A](userIdArg: Int, req: Request[A]) = new AuthenticatedRequest[A] {

    def body = req.body

    def headers = req.headers

    def id = req.id

    def method = req.method

    def path = req.path

    def queryString = req.queryString

    def remoteAddress = req.remoteAddress

    def secure = req.secure

    def tags = req.tags

    def uri = req.uri

    def version = req.version

    val userId: Int = userIdArg
  }
}

