package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait PetCommand[R] extends ReplyType[R]

case class CreatePet(pet: Pet) extends PetCommand[PetCreated]

case object CreatePet {
  implicit val format: Format[CreatePet] = Json.format
}
