package com.knoldus.akka.bench.zmq

import org.zeromq.ZMQ

import akka.actor.{ Actor, ActorSystem, Props }

object ZMQWithoutAkkaExtensionApp extends App {
  val numberOfMessages = 1000000
  val system = ActorSystem("zmq")
  val diagnostics = system.actorOf(Props[DiagnosticsActor])
  val publisher = system.actorOf(Props[Pub])
  val subscriber = system.actorOf(Props[Sub])
  subscriber ! "start"
  (1 to numberOfMessages).par.foreach { x: Int => publisher ! "hello"}
}

class Pub extends Actor {
  var contextZMQ: ZMQ.Context = null
  var publisherSocket: ZMQ.Socket = null

  override def preStart = {
    contextZMQ = ZMQ.context(1)
    publisherSocket = contextZMQ.socket(ZMQ.PUB)
    publisherSocket.bind("tcp://" + "127.0.0.1" + ":" + "1234")
    Thread.sleep(1000)
  }

  def receive = {
    case msg: String =>
      publisherSocket.send(msg.getBytes, 0)
  }
}

class Sub extends Actor {
  var contextZMQ: ZMQ.Context = null
  var subscriberSocket: ZMQ.Socket = null

  override def preStart = {
    contextZMQ = ZMQ.context(1)
    subscriberSocket = contextZMQ.socket(ZMQ.SUB)
    subscriberSocket.connect("tcp://" + "127.0.0.1" + ":" + "1234")
    subscriberSocket.subscribe("".getBytes())
    Thread.sleep(1000)
  }

  def receive = {
    case msg => handleMessages
  }

  private def handleMessages = {
    while (true) {
      val request = subscriberSocket.recv(0)
      val data = new String(request)
      ZMQWithoutAkkaExtensionApp.diagnostics ! data
    }
  }
}

class DiagnosticsActor extends Actor {
  var startTime = 0.0
  var counter = 0
  def receive = {
    case msg =>
      counter = counter + 1
      if (counter == 1) startTime = System.currentTimeMillis
      if (counter == ZMQWithoutAkkaExtensionApp.numberOfMessages) {
        val elapsedTime = System.currentTimeMillis - startTime
        val throughput = (ZMQWithoutAkkaExtensionApp.numberOfMessages.toDouble * 1000.0) / elapsedTime
        println("Elapsed Time millis: " + elapsedTime)
        println("Throughput msgs/sec: " + throughput)
      }
  }
}