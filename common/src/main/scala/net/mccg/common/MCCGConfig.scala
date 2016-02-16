package net.mccg.common

/**
 * This class represents our app configuration.
 */
case class MCCGConfig(amqpURL: Option[String], mongoURL: Option[String], port: Option[Int])

object MCCGConfig {
    def apply(): MCCGConfig = {
        val amqpURL = Option(System.getenv("RABBITMQ_URL"))
        val mongoURL = Option(System.getenv("MONGOHQ_URL"))
        val port = Option(System.getenv("PORT")) map { s => Integer.parseInt(s) }
        val config = MCCGConfig(amqpURL, mongoURL, port)
        config
    }
}
