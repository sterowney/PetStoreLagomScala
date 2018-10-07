package com.sterowney.pet.impl

import akka.stream.Materializer
import com.datastax.driver.core.Row
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession

import scala.concurrent.ExecutionContext

class PetReadRepository(session: CassandraSession)(implicit ec: ExecutionContext, mat: Materializer) {

  def getPets(limit: Int) = {
    for {
      petsResults <- selectPets(limit)
    } yield {
      petsResults
    }
  }

  private def selectPets(limit: Int) = {
    session.selectAll("""
      SELECT * FROM pet
      ORDER BY petId DESC
      LIMIT ?
    """, Integer.valueOf(limit)).map { rows =>
      rows.map(convertPet)
    }
  }

  private def convertPet(pet: Row) = {
    Pet(
      pet.getUUID("petId"),
      pet.getString("name"),
      pet.getLong("categoryId")
    )
  }

}
