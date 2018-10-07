package com.sterowney.petstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.sterowney.petstream.api.PetStreamService
import com.sterowney.pet.api.PetService
import com.softwaremill.macwire._

class PetStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new PetStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new PetStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[PetStreamService])
}

abstract class PetStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[PetStreamService](wire[PetStreamServiceImpl])

  // Bind the PetService client
  lazy val petService = serviceClient.implement[PetService]
}
