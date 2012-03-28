package com.knoldus.akka.bench.zmq

import org.zeromq.ZMQ

object ZMQPub extends App {
  val numOfMessages = 100000
  val contextZMQ = ZMQ.context(1)
  val publisherSocket = contextZMQ.socket(ZMQ.PUB)
  publisherSocket.bind("tcp://" + "127.0.0.1" + ":" + "1234")
  Thread.sleep(1000)
  val data = "hello".getBytes
  (1 to numOfMessages).foreach { x: Int => publisherSocket.send(data, 0) }
}