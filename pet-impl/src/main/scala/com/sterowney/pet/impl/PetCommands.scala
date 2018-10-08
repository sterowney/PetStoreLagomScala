package com.sterowney.pet.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

sealed trait PetCommand[R] extends ReplyType[R]

case class CreatePet(pet: Pet) extends PetCommand[PetCreated]

case object CreatePet {
  implicit val format: Format[CreatePet] = Json.format
}

case class UpdatePet(pet: Pet) extends PetCommand[PetUpdated]

case object UpdatePet {
  implicit val format: Format[UpdatePet] = Json.format
}

case class DeletePet(uuid: UUID) extends PetCommand[PetDeleted]

case object DeletePet {
  implicit val format: Format[DeletePet] = Json.format
}