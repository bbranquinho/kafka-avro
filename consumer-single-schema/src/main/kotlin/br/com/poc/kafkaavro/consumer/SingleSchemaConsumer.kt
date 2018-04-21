package br.com.poc.kafkaavro.consumer

import br.com.poc.CustomerAddressAdded
import br.com.poc.CustomerCreated
import br.com.poc.CustomerEvent
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SingleSchemaConsumer {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = ["customer"], containerFactory = "genericKafkaListenerContainerFactory")
    fun genericConsumer(event: GenericRecord) {
        logger.info("GENERIC RECORD")
        val payload = event.get("payload") as GenericRecord
        val header = event.get("header") as GenericRecord
        logger.info("Event header: $header")
        logger.info("Event payload: $payload")
        when(header.get("type").toString()) {
            "CustomerCreated" -> logger.info("Event customer fullName: ${payload.get("fullName")}")
            "CustomerAddressAdded" -> logger.info("Event customer address city: ${payload.get("city")}")
        }
    }

    @KafkaListener(topics = ["customer"], containerFactory = "specificKafkaListenerContainerFactory")
    fun specificConsumer(event: CustomerEvent) {
        logger.info("SPECIFIC RECORD")
        val header = event.getHeader()
        val payload = event.getPayload()
        logger.info("Event header: $header")
        logger.info("Event payload: $payload")
        when(payload) {
            is CustomerCreated -> logger.info("Event customer fullName: ${payload.getFullName()}")
            is CustomerAddressAdded -> logger.info("Event customer address city: ${payload.getCity()}")
        }
    }
}