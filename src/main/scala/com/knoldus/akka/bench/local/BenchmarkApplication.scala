package com.knoldus.akka.bench.local

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorLogging

object BenchmarkApplication extends App {
  val totalMessages = 10000000

  val context = ActorSystem("local")
  val localActor = context.actorOf(Props[LocalActor], "local")
  val message = LocalMessage("echo", totalMessages)
  (1 to totalMessages).par.foreach {
    x: Int => localActor ! message
  }
}

class LocalActor extends Actor with ActorLogging {
  var startTime = 0.0
  var counter = 0
  def receive = {
    case msg: LocalMessage =>
      counter = counter + 1
      if (counter == 1) startTime = System.currentTimeMillis
      if (counter == msg.total) {
        val elapsedTime = System.currentTimeMillis - startTime
        val throughput = (msg.total.toDouble * 1000.0) / elapsedTime
        log.info("Elapsed Time millis: " + elapsedTime)
        log.info("Throughput msgs/sec: " + throughput)
      }
  }

}

case class LocalMessage(data: String, total: Int)