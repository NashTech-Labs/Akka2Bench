package com.knoldus.akka.bench.zmq

import org.zeromq.ZMQ

object ZMQSub extends App {
  val contextZMQ = ZMQ.context(1)
  var count = 0
  var start = 0.0
  val subscriberSocket = contextZMQ.socket(ZMQ.SUB)
  subscriberSocket.connect("tcp://" + "127.0.0.1" + ":" + "1234")
  subscriberSocket.subscribe("".getBytes())
  Thread.sleep(1000)
  println("Subscriber started...")
  while (true) {
    val request = subscriberSocket.recv(0)
    count = count + 1
    if (count == 1) start = System.currentTimeMillis
    if (count == 100000) {
      println("aaya finally")
      val elapsedTime = System.currentTimeMillis - start
      val throughput = (100000.toDouble * 1000.0) / elapsedTime
      println("Elapsed Time millis: " + elapsedTime)
      println("Throughput msgs/sec: " + throughput)
    }
  }
}