package de.codepitbull.akka.ingestion

import java.io.{File, FileReader}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import com.github.tototoshi.csv.CSVReader
import de.codepitbull.akka.ingestion.domain.{EnrichedValue, Value}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

/**
  * Created by jochen on 17.04.16.
  */
object Digestion {

  def main(args: Array[String]): Unit = {
    val ip = args(0)

    implicit var system = ActorSystem("DigestionSystem")
    implicit val materializer = ActorMaterializer()
    val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer,
      Set("valid"))
      .withBootstrapServers(s"${ip}:9092")
      .withGroupId("group6")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    Consumer.committableSource(consumerSettings.withClientId("client9"))
        .runForeach(i => {
          println(i.value)
          i.committableOffset.commit()
        })

  }
}
