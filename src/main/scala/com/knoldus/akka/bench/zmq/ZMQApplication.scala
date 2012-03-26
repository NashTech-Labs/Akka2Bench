package com.knoldus.akka.bench.zmq

import akka.zeromq._
import akka.actor.{ ActorSystem, Actor, Props }

object ZMQApplication extends App {
  val numberOfMessages = 10000
  val system = ActorSystem("zmq")
  val diagnostics = system.actorOf(Props[Diagnostics])
  val pubSocket = ZeroMQExtension(system).newSocket(SocketType.Pub, Bind("tcp://127.0.0.1:1234"))
  val subSocket = ZeroMQExtension(system).newSocket(SocketType.Sub, Listener(system.actorOf(Props[Receiver])), Connect("tcp://127.0.0.1:1234"), Subscribe("foo.bar"))
  Thread.sleep(1000)
  (1 to numberOfMessages) foreach { x: Int => pubSocket ! ZMQMessage(Seq(Frame("foo.bar"), Frame("hello"))) }
}

class Receiver extends Actor {
  var startTime = 0.0
  var counter = 0
  def receive = {
    case Connecting =>
    case m: ZMQMessage => ZMQApplication.diagnostics ! "done"
    case _ =>
  }
}

class Diagnostics extends Actor {
  var startTime = 0.0
  var counter = 0
  def receive = {
    case msg =>
      counter = counter + 1
      if (counter == 1) startTime = System.currentTimeMillis
      if (counter == ZMQApplication.numberOfMessages) {
        val elapsedTime = System.currentTimeMillis - startTime
        val throughput = (ZMQApplication.numberOfMessages.toDouble * 1000.0) / elapsedTime
        println("Elapsed Time millis: " + elapsedTime)
        println("Throughput msgs/sec: " + throughput)
      }
  }
}