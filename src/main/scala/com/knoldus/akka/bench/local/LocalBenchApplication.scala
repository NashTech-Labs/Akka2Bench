package com.knoldus.akka.bench.local

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.RoundRobinRouter

object LocalBenchApplication extends App {
  val totalMessages = 10000000
  val system = ActorSystem("bench")
  val diagnostics = system.actorOf(Props[Diagnostics], "diagnostics")
  val message = Message("hello", totalMessages)
  val router = system.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfInstances = 4000)))
  (1 to totalMessages).par.foreach {
    x: Int => router ! message
  }
}

class Worker extends Actor {
  def receive = {
    case msg: Message => LocalBenchApplication.diagnostics ! msg
  }
}

class Diagnostics extends Actor {
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

case class Message(data: String, total: Int)