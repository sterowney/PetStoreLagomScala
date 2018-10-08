package com.sterowney.pet.impl

import java.util.UUID

import akka.stream.Materializer
import com.datastax.driver.core.Row
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.{ExecutionContext}

class PetReadRepository(session: CassandraSession)(implicit ec: ExecutionContext, mat: Materializer) {

  def getPets(limit: Int) = {
    selectPets(limit)
  }

  def getPet(id: UUID) = {
    selectPet(id)
  }

  private def selectPets(limit: Int) = {
    session.selectAll("""
      SELECT * FROM pet
      LIMIT ?
    """, Integer.valueOf(limit)).map { rows =>
      rows.map(convertPet)
    }
  }

  private def selectPet(petId: UUID) = {
    session.selectOne("""
      SELECT * FROM pet
      WHERE petId = ?
    """, petId).map { rows =>
      rows.map(convertPet)
    }
  }

  private def convertPet(pet: Row): Pet = {
    Pet(
      pet.getUUID("petId"),
      pet.getString("name"),
      pet.getLong("categoryId")
    )
  }

}
