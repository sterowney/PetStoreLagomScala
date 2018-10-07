package com.sterowney.petstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.sterowney.petstream.api.PetStreamService
import com.sterowney.pet.api.PetService

import scala.concurrent.Future

/**
  * Implementation of the PetStreamService.
  */
class PetStreamServiceImpl(petService: PetService) extends PetStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(petService.hello(_).invoke()))
  }
}
