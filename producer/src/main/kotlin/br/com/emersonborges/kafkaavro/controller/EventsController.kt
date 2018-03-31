package br.com.emersonborges.kafkaavro.controller

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.IOException


@RestController
class EventsController(
    val kafkaTemplate: KafkaTemplate<String, GenericRecord>
) {

    @Value("classpath:avro/CustomerCreated_v1.avsc")
    lateinit var schemaCustomerCreatedV1: Resource

    @Value("classpath:avro/CustomerCreated_v2.avsc")
    lateinit var schemaCustomerCreatedV2: Resource

    @Value("classpath:avro/CustomerAddressAdded_v1.avsc")
    lateinit var schemaCustomerAddressAddedV1: Resource

    @Value("classpath:avro/CustomerCreated_v3.avsc")
    lateinit var schemaCustomerCreatedV3: Resource

    @Value("classpath:avro/CustomerCreated_v4.avsc")
    lateinit var schemaCustomerCreatedV4: Resource

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v1/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV1(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(schemaCustomerCreatedV1.file, event), "CustomerCreated"))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v1/customers/addresses/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerUpdatedV1(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(schemaCustomerAddressAddedV1.file, event), "CustomerAddressAdded"))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v2/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV2(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(schemaCustomerCreatedV2.file, event), "CustomerCreated"))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v3/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV3(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(schemaCustomerCreatedV3.file, event), "CustomerCreated"))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v4/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV4(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(schemaCustomerCreatedV4.file, event), "CustomerCreated"))
    }

    @Throws(IOException::class)
    private fun parseJson(schema: File, json: String): GenericData.Record {
        val parsedSchema = Schema.Parser().parse(schema)
        val decoder = DecoderFactory().jsonDecoder(parsedSchema, json)
        val reader = GenericDatumReader<GenericData.Record>(parsedSchema)
        return reader.read(null, decoder)
    }

    private fun buildRecord(
        value: GenericData.Record,
        header: String
    ): ProducerRecord<String, GenericRecord> {
        return ProducerRecord(
            "customer",
            null,
            "key",
            value,
            mutableListOf(RecordHeader("type", header.toByteArray()))
        )
    }
}
