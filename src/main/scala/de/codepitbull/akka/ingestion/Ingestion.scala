package de.codepitbull.akka.ingestion

import java.io.InputStreamReader

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import com.github.tototoshi.csv.CSVReader
import de.codepitbull.akka.ingestion.domain.{EnrichedValue, Value}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

/**
  * Created by jochen on 17.04.16.
  */
object Ingestion {

  def createCsvSource(): Source[Seq[String], NotUsed] = {
    Source.fromIterator(() => {
      val it = CSVReader
        .open(new InputStreamReader(
          getClass.getClassLoader.getResourceAsStream("values.csv")
        ))
        .iterator
      if (it.hasNext) it.next()
      it
    })
  }

  def goodMessage(elem: EnrichedValue) = new ProducerRecord[Array[Byte], String]("enriched", elem.toString)

  def deadLetterMessage(elem: EnrichedValue) = new ProducerRecord[Array[Byte], String]("deadletters", elem.toString)

  def main(args: Array[String]): Unit = {
    val ip = args(0)

    implicit var system = ActorSystem("IngestionSystem")
    implicit val materializer = ActorMaterializer()

    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers("${ip}:9092")

    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val src = createCsvSource()
      val transformFlow = Flow[Seq[String]].map(in => Value(in(0).toInt, in(1)))
      val enrichFlow = Flow[Value].map(in => EnrichedValue(in, 2))
      val bcast = builder.add(Broadcast[Value](2))
      val filterValid = Flow[Value].filter(i => i.val1 >= 2)
      val filterInalid = Flow[Value].filter(i => i.val1 < 2)

      val sink = Sink.ignore

      src ~> transformFlow ~> bcast ~> filterValid ~> enrichFlow.map(goodMessage) ~> Producer.plainSink(producerSettings)
                              bcast ~> filterInalid ~> enrichFlow.map(deadLetterMessage) ~> Producer.plainSink(producerSettings)
      ClosedShape
    }).run()
  }
}
