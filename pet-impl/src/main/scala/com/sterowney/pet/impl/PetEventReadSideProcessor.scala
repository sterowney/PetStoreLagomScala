package com.sterowney.pet.impl

import akka.Done
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

import scala.concurrent.{ExecutionContext, Future, Promise}

class PetEventReadSideProcessor(session: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext)
  extends ReadSideProcessor[PetEvent] {

  private val insertPetStatementPromise = Promise[PreparedStatement]
  private def insertPetStatement: Future[PreparedStatement] = insertPetStatementPromise.future

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PetEvent] = {
    readSide.builder[PetEvent]("petEventOffset")
      .setGlobalPrepare(createTables)
      .setPrepare(_ => prepareStatements())
      .setEventHandler[PetCreated](e => insertPet(e.event.pet))
      .build
  }

  override def aggregateTags: Set[AggregateEventTag[PetEvent]] = PetEvent.Tag.allTags

  private def createTables() = {
    for {
      _ <- session.executeCreateTable("""
        CREATE TABLE IF NOT EXISTS pet (
          petId timeuuid PRIMARY KEY,
          name text,
          categoryId bigint
        )
      """)
    } yield Done
  }

  private def prepareStatements() = {
    val insertPetFuture = session.prepare(
      """
          INSERT INTO pet(petId, name, categoryId) VALUES (?, ?, ?)
        """)

    insertPetStatementPromise.completeWith(insertPetFuture)

    for {
      _ <- insertPetFuture
    } yield Done
  }

  private def doInsertPet(pet: Pet) = {
    insertPetStatement.map { ps =>
      val bindPet = ps.bind()
      bindPet.setUUID("petId", pet.id)
      bindPet.setString("name", pet.name)
      bindPet.setLong("categoryId", pet.categoryId)
      bindPet
    }
  }

  private def insertPet(pet: Pet) = {
    for {
      insertPetStatement <- doInsertPet(pet)
    } yield List(insertPetStatement)
  }
}
