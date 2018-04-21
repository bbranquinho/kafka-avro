package br.com.poc.kafkaavro

import br.com.poc.CustomerAddressAdded
import br.com.poc.CustomerCreated
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.IndexedRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MultipleSchemasConsumer {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = ["customer"], containerFactory = "specificRecordKafkaListenerContainerFactory")
    fun specificRecordConsumer(event: IndexedRecord) {
        logger.info("SPECIFIC RECORD RECORD")
        logger.info("Event: $event")
        when (event) {
            is CustomerCreated -> {
                logger.info("CUSTOMER CREATED RECORD")
                logger.info("Event: $event")
                logger.info("Event customer fullName: ${event.getFullName()}")
            }
            is CustomerAddressAdded -> {
                logger.info("CUSTOMER ADDRESS ADDED RECORD")
                logger.info("Event: $event")
                logger.info("Event customer address street: ${event.getStreet()}")
            }
        }
    }

    @KafkaListener(topics = ["customer"], containerFactory = "genericKafkaListenerContainerFactory")
    fun genericConsumer(event: GenericRecord) {
        logger.info("GENERIC RECORD")
        logger.info("Event: $event")
        if (event.schema.fullName == CustomerCreated.getClassSchema().fullName) {
            logger.info("GENERIC CUSTOMER CREATED RECORD")
            logger.info("Event customer fullName: ${event.get("fullName")}")
        }
        if (event.schema.fullName == CustomerAddressAdded.getClassSchema().fullName) {
            logger.info("GENERIC CUSTOMER ADDRESS ADDED RECORD")
            logger.info("Event customer address street: ${event.get("street")}")
        }
    }

}