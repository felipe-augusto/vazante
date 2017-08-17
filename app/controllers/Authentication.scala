package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.json.Json

import play.api.db._
import models._
import play.api.libs.json._

import scalaj.http._
import pdi.jwt.JwtSession

import com.mongodb.casbah.Imports._


object Authentication extends Controller {
  // authenticates using using facebook oauth strategy
  def facebook = Action { request =>
    // receive access_token from front-end
    // request.body.asFormUrlEncoded.get("felipe").mkString.split(",").map(x => println(x))
    request.body.asJson.map { json =>
      (json \ "access_token").asOpt[String].map { access_token =>
        // fetch facebook Graph API to obtain user data
        val graph_response = Http("https://graph.facebook.com/me/")
          .param("access_token", access_token)
          .param("fields", "email,gender,picture,name")
          .asString

        // convert response to json
        val json = Json.parse(graph_response.body)

        // extract necessary data to create user from json
        val name = (json \ "name").as[JsString].value
        val email = (json \ "email").as[JsString].value
        val picture = (json \ "picture" \ "data" \ "url").as[JsString].value
        val gender = (json \ "gender").as[JsString].value
        val fb_id = (json \ "id").as[JsString].value

        // find user in the database
        val current_user = User.find(MongoDBObject("email" -> email))

        // if user does not exists we must create it in the database
        if(current_user == null) {
          val new_user = User(name, email, picture, gender, fb_id)
          User.save(new_user)
        }

        // returns the JWT to the new user
        var session = JwtSession()
        session += ("email", email)
        val response = Map("token" -> session.serialize)
        Ok(Json.toJson(response))
      }.getOrElse {
        BadRequest("Expecting Json data")
      }
    }.getOrElse {
      Ok("Missing parameter [access_token]")
    }
  }

    def facebook_options = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }
}