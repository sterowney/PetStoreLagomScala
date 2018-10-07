package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

sealed trait PetEvent extends AggregateEvent[PetEvent] {
  def aggregateTag: AggregateEventShards[PetEvent] = PetEvent.Tag
}

object PetEvent {
  val NumShards = 20 // TODO - configure me
  val Tag = AggregateEventTag.sharded[PetEvent](NumShards)
}

case class PetCreated(pet: Pet) extends PetEvent

case object PetCreated {
  implicit val format: Format[PetCreated] = Json.format
}


