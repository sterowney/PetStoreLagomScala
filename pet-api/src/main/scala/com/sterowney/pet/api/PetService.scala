package com.sterowney.pet.api

import java.util.UUID

import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait PetService extends Service {

  def createPet(): ServiceCall[CreatePetRequest, Pet]

  override final def descriptor = {
    import Service._
    named("pet")
      .withCalls(
        restCall(Method.POST, "/api/pet", createPet _)
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
