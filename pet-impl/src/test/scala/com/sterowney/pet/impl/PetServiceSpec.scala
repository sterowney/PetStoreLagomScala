package com.sterowney.pet.impl

import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.sterowney.pet.api.{PetRequest, PetService}
import org.scalatest.{AsyncWordSpec, Matchers}

class PetServiceSpec extends AsyncWordSpec with Matchers {

  "The PetService" should {
    "create a pet and retrieve" in ServiceTest.withServer(ServiceTest.defaultSetup) { ctx =>
      new PetApplication(ctx) with LocalServiceLocator
    } { server =>
      val client = server.serviceClient.implement[PetService]

      for {
        createdPet <- client.createPet.invoke(PetRequest("Joey", 1))
//        retrievedPet <- client.getPet(createdPet.id.toString).invoke()
//        updatedPet <- client.updatePet(retrievedPet.id.toString).invoke(PetRequest("Joey updated", 2))
//        _ <- client.deletePet(updatedPet.id.toString).invoke()
//        retrievedPetAfterDelete <- client.getPet(updatedPet.id.toString).invoke()
      } yield {
        createdPet.name shouldBe "Joey"
        createdPet.categoryId shouldBe 1

//        retrievedPet.id shouldBe createdPet.id
//        retrievedPet.name shouldBe createdPet.name
//        retrievedPet.categoryId shouldBe createdPet.categoryId
//
//        updatedPet.id shouldBe retrievedPet.id
//        updatedPet.name shouldBe "Joey updated"
//        updatedPet.categoryId shouldBe 2
//
//        val caught = intercept[NotFound] {
//          retrievedPetAfterDelete
//        }
//
//        caught.exceptionMessage.detail shouldBe s"Pet with id: '${updatedPet.id}' does not exist"
      }
    }
  }

}
