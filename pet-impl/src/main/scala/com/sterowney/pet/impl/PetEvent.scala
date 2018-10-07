package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

sealed trait PetEvent extends AggregateEvent[PetEvent] {
  def aggregateTag: AggregateEventTag[PetEvent] = PetEvent.Tag
}

object PetEvent {
  val Tag = AggregateEventTag[PetEvent]
}

case class PetCreated(pet: Pet) extends PetEvent

case object PetCreated {
  implicit val format: Format[PetCreated] = Json.format
}


