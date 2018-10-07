package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

object PetSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[PetState],
    JsonSerializer[CreatePet],
    JsonSerializer[PetCreated]
  )
}