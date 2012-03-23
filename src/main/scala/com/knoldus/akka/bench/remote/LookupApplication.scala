package com.knoldus.akka.bench.remote

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.kernel.Bootable

object LookupApplication extends App with Bootable {
  val totalMessages = 100000
  val context = ActorSystem("Local", ConfigFactory.load.getConfig("remotelookup"))
  val remoteActor = context.actorFor("akka://RemoteApplication@127.0.0.1:2554/user/echo")
  val message = Message("hello", totalMessages)

  (1 to totalMessages).par.foreach ( x => remoteActor ! message )

  def startup = {}

  def shutdown = context.shutdown
}

case class Message(msg: String, total: Int)