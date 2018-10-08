package com.sterowney.pet.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait PetService extends Service {

  /**
    * curl -X POST http://localhost:9000/api/pet -H 'content-type: application/json' -d '{ "name": "Joey", "categoryId": 1 }'
    */
  def createPet(): ServiceCall[CreatePetRequest, Pet]

  /**
    * curl -X GET http://localhost:9000/api/pet/:id
    */
  def getPet(id: String): ServiceCall[NotUsed, Pet]

  /**
    * curl -X GET http://localhost:9000/api/pet
    */
  def getPets(): ServiceCall[NotUsed, Seq[Pet]]

  override final def descriptor = {
    import Service._
    named("pet")
      .withCalls(
        restCall(Method.POST, "/api/pet", createPet _),
        restCall(Method.GET, "/api/pet/:id", getPet _),
        restCall(Method.GET, "/api/pet", getPets _)
      )
      .withAutoAcl(true)
  }
}

case class CreatePetRequest(name: String, categoryId: Long)

case object CreatePetRequest {
  implicit val format: Format[CreatePetRequest] = Json.format
}

case class Pet(id: UUID, name: String, categoryId: Long)

case object Pet {
  implicit val format: Format[Pet] = Json.format
}
