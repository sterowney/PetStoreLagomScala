package com.sterowney.pet.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import play.api.libs.json._

class PetEntity extends PersistentEntity {

  override type Command = PetCommand[_]
  override type Event = PetEvent
  override type State = PetState

  override def initialState: PetState = PetState.initialState

  override def behavior: Behavior = {
    Actions().onCommand[CreatePet, PetCreated] {
      case (CreatePet(pet), ctx, _) =>
        ctx.thenPersist(PetCreated(pet))(ctx.reply)
    }.onEvent {
      case (PetCreated(pet), _) => PetState(Some(pet))
    }
  }
}

case class Pet(id: UUID, name: String, categoryId: Long)

case object Pet {
  implicit val format: Format[Pet] = Json.format

  def create(name: String, categoryId: Long) =
    Pet(id = UUID.randomUUID(), name = name, categoryId = categoryId)
}
