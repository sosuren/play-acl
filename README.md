# play-acl
ACL for Play Framework [Scala]

This is an authorisation library for Play Framework. You can manage access rights and compose them as well.

## Configuration
1. Create custom authentication mechanism by implementing `AuthenticatedAction`:


```scala
import com.myproject.play.acl.{AuthenticatedRequest, AuthenticatedAction}

class UserAuthenticatedAction @Inject() (val authHandler: AuthHandler) extends AuthenticatedAction {

  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
    // use your own authentication mechanism here  
    authHandler.getUserId(request.headers.get(AuthConst.HeaderCookie).getOrElse("")).flatMap {
      case None => Future.successful(Unauthorized)
      case Some(userId) =>
        block(AuthenticatedRequest[A](userId, Role.USER.toString, request))
    }
  }
}
```

2. Create Injector Module

```scala
class PlayAclModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[AuthenticatedAction]).to(classOf[UserAuthenticatedAction])
  }
}
```

3. Add injector module in config file (e.g. `application.conf`)
```
play.modules.enabled += "com.myproject.play.acl.AclModule"
play.modules.enabled += "PlayAclModule"
```

