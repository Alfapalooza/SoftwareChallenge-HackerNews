package com.challenge


import challenge.Routes
import challenge.guice.Modules
import challenge.logger.impl.{ErrorLogger, RequestLogger}

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest

class RoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with Routes {
  override def modules: Modules =
    ???

  override def requestLogger: RequestLogger =
    ???

  override def errorLogger: ErrorLogger =
    ???

  "routes" should {
    "return no users if no present (GET /users)" in {
      val request = HttpRequest(uri = "/users")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"users":[]}""")
      }
    }

    "be able to add users (POST /users)" in {
      val request = Post("/users").withEntity(???)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"User Kapi created."}""")
      }
    }

    "be able to remove users (DELETE /users)" in {
      val request = Delete(uri = "/users/Kapi")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"description":"User Kapi deleted."}""")
      }
    }
  }
}
