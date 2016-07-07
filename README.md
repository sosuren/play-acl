# play-acl
ACL for Play Framework [Scala]

This is an authorisation library for Play Framework 2.4.x. You can manage access rights and compose them as well.

## Configuration

**1**. Create custom authentication mechanism by implementing `AuthenticatedAction`:


```scala
import com.myproject.play.acl.{AuthenticatedRequest, AuthenticatedAction}

class UserAuthenticatedAction @Inject() (val authHandler: AuthHandler) extends AuthenticatedAction {

  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
    // use your own authentication mechanism here  
    authHandler.getUserId(request.headers.get("MyCookie").getOrElse("")).flatMap {
      case None => Future.successful(Unauthorized)
      case Some(userId) =>
        block(AuthenticatedRequest[A](userId, request))
    }
  }
}
```

**2**. Configure Injector Module

```scala
class PlayAclModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[AuthenticatedAction]).to(classOf[UserAuthenticatedAction])
  }
}
```

## Access Right Composition

You can actually create access rule separately and compose them.

### Define Access Rule

```scala
import com.myproject.play.acl.AccessRule

class Admin extends AccessRule {

  override def apply(userId: Int): Future[Boolean] = Future { 
    userId == 1 // your implementation goes here
  }
}

class HasWriteAccess(resourceId: Int) extends AccessRule {

  override def apply(userId: Int): Future[Boolean] = Future {
    // use id of resource and user id to find if user has write access to resource 
    resourceId == 2 && userId == 2 // your implementation goes here
  }
}
```

### Use Access Rule

```scala
import com.myproject.play.acl.AuthenticatedAction
...

class TestController @Inject() (authAxn: AuthenticatedAction) extends Controller {

  /** only admin can access */
  def test1() = authAxn allowTo Admin() in { request =>
    Future.successful(Ok)
  }
  
  /** Either admin or user having write access */
  def test2(resId: Int) = authAxn allowTo (IsOwner() or HasWriteAccess(resId)) in { request =>
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

**1**. Your access protection code is more readable

For example look at this snippet: `authAxn allowTo (Admin() or HasWriteAccess(resId)) in { ... }`
It can be read as `Authenticated Action should allow to Admin or Having Write Access`

**2**. Testings made easy

You can test each access rule separately and later compose them to create complex rule

