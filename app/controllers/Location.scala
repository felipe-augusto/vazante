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

object Location extends Controller {
  // render a view to sign a new location
  def index = Action { request => 
    Ok(views.html.preferred(null))
  }
}