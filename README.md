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

## Access Right Composition

You can actually create access rule separately and compose them.

### Define Access Rule

```scala
import com.myproject.play.acl.AccessRule

class IsOwner extends AccessRule {

  override def apply(userId: Int): Future[Boolean] = Future { 
    userId == 1 // your implementation goes here
  }
}

class HasWriteRight(resourceId: Int) extends AccessRule {

  override def apply(userId: Int): Future[Boolean] = Future {
    // use id of resource and user id to find if user has write access to resource 
    false // your implementation goes here
  }
}
```

### Use Access Rule

```scala
import com.myproject.play.AuthDsl._
...

class TestController @Inject() extends Controller {

  /** only owner can access */
  def test1() = Identify as Role.USER.toString allowTo IsOwner() in { request =>
    Future.successful(Ok)
  }
  
  /** Either owner or user having write right can access */
  def test2(resId: Int) = Identify as Role.USER.toString allowTo (IsOwner() or HasWriteRight(resId)) in { request =>
    Future.successful(Ok)
  }
}

```

### How to compose Access Rule?

> OR rule

Using `or` will give access to action if any of them returns true

> AND rule

Using `and` will only give access to action if all of them returns true

> Multiple rule

You can even compose to create complex rule like: `Rule1() or (Rule2() and Rule3())`

## Benefits

1. You can write authorisation code as sentence using `AuthDsl`

For example look at this snippet: `Identify as Role.USER.toString allowTo (IsOwner() or HasWriteRight(resId)) in { ... }`
It can be read as `Identify as user and allow to owner or having write right`

2. Testings made easy

You can test each access rule separately and later compose them to create complex rule

