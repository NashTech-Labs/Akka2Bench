package com.knoldus.akka.bench.remote

import com.typesafe.config.ConfigFactory

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.kernel.Bootable

object RemoteApplication extends App with Bootable {
  val system = ActorSystem("RemoteApplication", ConfigFactory.load.getConfig("echo"))
  val remoteActor = system.actorOf(Props[RemoteActor], "echo")

  def startup = {}

  def shutdown = system.shutdown

}

class RemoteActor extends Actor {
  var startTime = 0.0
  var counter = 0
  def receive = {
    case msg: Message =>
      counter = counter + 1
      if (counter == 1) startTime = System.currentTimeMillis
      if (counter == msg.total) {
        val elapsedTime = System.currentTimeMillis - startTime
        val throughput = (msg.total.toDouble * 1000.0) / elapsedTime
        println("Elapsed Time millis: " + elapsedTime)
        println("Throughput msgs/sec: " + throughput)
      }
  }
}