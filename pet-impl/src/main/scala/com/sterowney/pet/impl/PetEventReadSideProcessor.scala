package com.sterowney.pet.impl

import java.util.UUID

import akka.Done
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

import scala.concurrent.{ExecutionContext, Future, Promise}

class PetEventReadSideProcessor(session: CassandraSession,
                                readSide: CassandraReadSide)(implicit ec: ExecutionContext) extends ReadSideProcessor[PetEvent] {

  private val insertPetStatementPromise = Promise[PreparedStatement]
  private def insertPetStatement: Future[PreparedStatement] = insertPetStatementPromise.future

  private val updatePetStatementPromise = Promise[PreparedStatement]
  private def updatePetStatement: Future[PreparedStatement] = updatePetStatementPromise.future

  private val deletePetStatementPromise = Promise[PreparedStatement]
  private def deletePetStatement: Future[PreparedStatement] = deletePetStatementPromise.future

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[PetEvent] = {
    readSide.builder[PetEvent]("petEventOffset")
      .setGlobalPrepare(createTables)
      .setPrepare(_ => prepareStatements())
      .setEventHandler[PetCreated](e => insertPet(e.event.pet))
      .setEventHandler[PetUpdated](e => updatePet(e.event.pet))
      .setEventHandler[PetDeleted](e => deletePet(e.event.uuid))
      .build
  }

  override def aggregateTags: Set[AggregateEventTag[PetEvent]] = PetEvent.Tag.allTags

  private def createTables() = {
    for {
      _ <- session.executeCreateTable(
        """
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

    val updatePetFuture = session.prepare(
      """
          UPDATE pet SET name = ?, categoryId = ? WHERE petId = ?
        """)

    updatePetStatementPromise.completeWith(insertPetFuture)

    val deletePetFuture = session.prepare(
      """
          DELETE FROM pet WHERE petId = ?
        """)

    deletePetStatementPromise.completeWith(deletePetFuture)

    for {
      _ <- insertPetFuture
      _ <- updatePetFuture
      _ <- deletePetFuture
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

  private def doUpdatePet(pet: Pet) = {
    updatePetStatement.map { ps =>
      val bindPet = ps.bind()
      bindPet.setUUID("petId", pet.id)
      bindPet.setString("name", pet.name)
      bindPet.setLong("categoryId", pet.categoryId)
      bindPet
    }
  }

  private def doDeletePet(uuid: UUID) = {
    deletePetStatement.map { ps =>
      val bindPet = ps.bind()
      bindPet.setUUID("petId", uuid)
    }
  }

  private def insertPet(pet: Pet) = {
    for {
      insertPetStatement <- doInsertPet(pet)
    } yield List(insertPetStatement)
  }

  private def updatePet(pet: Pet) = {
    for {
      updatePetStatement <- doUpdatePet(pet)
    } yield List(updatePetStatement)
  }

  private def deletePet(uuid: UUID) = {
    for {
      deletePetStatement <- doDeletePet(uuid)
    } yield List(deletePetStatement)
  }
}
