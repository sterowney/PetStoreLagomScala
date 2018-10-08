package com.sterowney.pet.impl


import java.util.UUID

import akka.NotUsed
import com.datastax.driver.core.utils.UUIDs
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.sterowney.pet.api.{PetRequest, PetService}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.sterowney.pet.api

import scala.concurrent.ExecutionContext

class PetServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, petReadRepository: PetReadRepository)(implicit ec: ExecutionContext) extends PetService {

  override def createPet(): ServiceCall[api.PetRequest, api.Pet] = { petRequest =>
    val petId: UUID = UUIDs.timeBased()
    refForPet(petId).ask(CreatePet(Pet(petId, petRequest.name, petRequest.categoryId))).map { _ =>
      api.Pet(petId, petRequest.name, petRequest.categoryId)
    }
  }

  override def getPet(id: String): ServiceCall[NotUsed, api.Pet] = { _ =>
    petReadRepository.getPet(UUID.fromString(id)).map {
      case Some(pet) => api.Pet(pet.id, pet.name, pet.categoryId)
      case None => throw NotFound(s"Pet with id: '$id' does not exist")
    }
  }

  override def updatePet(id: String): ServiceCall[PetRequest, api.Pet] = { petRequest =>
    val petId: UUID = UUID.fromString(id)
    refForPet(petId).ask(UpdatePet(Pet(petId, petRequest.name, petRequest.categoryId))).map { _ =>
      api.Pet(petId, petRequest.name, petRequest.categoryId)
    }
  }

  override def deletePet(id: String): ServiceCall[NotUsed, NotUsed] = { _ =>
    val petId: UUID = UUID.fromString(id)
    refForPet(petId).ask(DeletePet(petId)).map { _ =>
      NotUsed
    }
  }

  override def getPets(): ServiceCall[NotUsed, Seq[api.Pet]] = { _ =>
    for {
      pets <- petReadRepository.getPets(10)
    } yield {
      pets.map(pet => api.Pet(pet.id, pet.name, pet.categoryId))
    }
  }


  private def refForPet(petId: UUID) =
    persistentEntityRegistry.refFor[PetEntity](petId.toString)
}
