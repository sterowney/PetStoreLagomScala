package com.sterowney.pet.impl.states

import com.sterowney.pet.impl.Pet
import play.api.libs.json.{Format, Json}

case class PetState(pet: Option[Pet])

object PetState {
  implicit val format: Format[PetState] = Json.format
  val initialState = PetState(None)
}
