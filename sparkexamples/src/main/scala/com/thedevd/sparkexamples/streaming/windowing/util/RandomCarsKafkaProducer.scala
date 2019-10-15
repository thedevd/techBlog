package com.thedevd.sparkexamples.streaming.windowing.util

import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.Random
import org.apache.kafka.clients.producer.ProducerRecord

object RandomCarsKafkaProducer {
  
  def main(args: Array[String]): Unit = {
    
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    
    val kafkaProducer = new KafkaProducer[String, String](props)
    
    val topic = "cars"
    val random = new Random()
    
    while(true) {
      val carId = "car" + random.nextInt(10)
      val speed = random.nextInt(150)
      val value = s"$carId,$speed,${System.currentTimeMillis()}"
      
      println("sending data to cars topic: " + value)
      kafkaProducer.send(new ProducerRecord[String,String](topic,carId, value)) // topic, key, value
      
      Thread.sleep(500) // putting some delays
    }
  }
  
}