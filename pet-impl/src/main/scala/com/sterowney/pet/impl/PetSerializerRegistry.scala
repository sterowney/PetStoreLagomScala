package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.sterowney.pet.impl.commands.{CreatePet, DeletePet, UpdatePet}
import com.sterowney.pet.impl.events.{PetCreated, PetDeleted, PetUpdated}
import com.sterowney.pet.impl.states.PetState

import scala.collection.immutable.Seq

object PetSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[PetState],
    JsonSerializer[CreatePet],
    JsonSerializer[UpdatePet],
    JsonSerializer[DeletePet],
    JsonSerializer[PetCreated],
    JsonSerializer[PetUpdated],
    JsonSerializer[PetDeleted]
  )
}