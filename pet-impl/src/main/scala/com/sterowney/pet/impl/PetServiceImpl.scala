package com.sterowney.pet.impl


import java.util.UUID

import akka.NotUsed
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.sterowney.pet.api.PetService
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.sterowney.pet.api

import scala.concurrent.ExecutionContext

class PetServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends PetService {

  override def createPet(): ServiceCall[api.CreatePetRequest, api.Pet] = { request =>
    val petId: UUID = UUIDs.timeBased()
    refForPet(petId).ask(CreatePet(Pet(petId, request.name, request.categoryId))).map { _ =>
      api.Pet(petId, request.name, request.categoryId)
    }
  }

  override def getPet(id: String): ServiceCall[NotUsed, api.Pet] = { _ =>
    refForPet(UUID.fromString(id)).ask(GetPet).map {
      case Some(pet) => api.Pet(pet.id, pet.name, pet.categoryId)
      case None => throw NotFound(s"Pet with id: '$id' does not exist")
    }
  }


  private def refForPet(petId: UUID) =
    persistentEntityRegistry.refFor[PetEntity](petId.toString)
}
