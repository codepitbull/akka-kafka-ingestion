package de.codepitbull.akka.ingestion.domain

/**
  * Created by jochen on 17.04.16.
  */
case class Value (val1: Int, val2: String)

case class EnrichedValue (value: Value, val3: Int)
