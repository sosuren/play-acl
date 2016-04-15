package com.myproject.play.acl

import play.api.mvc.Request

/**
 * Created by Surendra on 4/5/16.
 */
trait AuthenticatedRequest[+A] extends Request[A]{

  val userId: Int
}

object AuthenticatedRequest {

  def apply[A](u: Int, r: Request[A]) = new AuthenticatedRequest[A] {

    def body = r.body

    def headers = r.headers

    def id = r.id

    def method = r.method

    def path = r.path

    def queryString = r.queryString

    def remoteAddress = r.remoteAddress

    def secure = r.secure

    def tags = r.tags

    def uri = r.uri

    def version = r.version

    val userId = u
  }
}

