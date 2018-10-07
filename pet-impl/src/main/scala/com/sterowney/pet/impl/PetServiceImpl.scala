package com.sterowney.pet.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.sterowney.pet.api.PetService
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.sterowney.pet.api

import scala.concurrent.ExecutionContext

class PetServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends PetService {

  override def createPet(): ServiceCall[api.CreatePetRequest, api.Pet] = { request =>
    val petId = UUID.randomUUID()
    refForPet(petId).ask(CreatePet(Pet(petId, request.name, request.categoryId))).map { _ =>
      api.Pet(petId, request.name, request.categoryId)
    }
  }

  private def refForPet(petId: UUID) =
    persistentEntityRegistry.refFor[PetEntity](petId.toString)
}
