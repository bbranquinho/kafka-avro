package br.com.emersonborges.kafkaavro.controller

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.io.InputStream


@RestController
class EventsController(
    val kafkaTemplate: KafkaTemplate<String, GenericRecord>
) {

    companion object {
        const val CUSTOMER_CREATED_HEADER = "CustomerCreated"
        const val CUSTOMER_ADDRESS_ADDED_HEADER = "CustomerAddressAdded"
    }

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v1/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV1(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(readSchema("CustomerCreated_v1"), event), CUSTOMER_CREATED_HEADER))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v1/customers/addresses/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerUpdatedV1(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(readSchema("CustomerAddressAdded_v1"), event), CUSTOMER_ADDRESS_ADDED_HEADER))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v2/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV2(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(readSchema("CustomerCreated_v2"), event), CUSTOMER_CREATED_HEADER))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v3/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV3(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(readSchema("CustomerCreated_v3"), event), CUSTOMER_CREATED_HEADER))
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/v4/customers/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun customerCreatedV4(@RequestBody event: String) {
        kafkaTemplate.send(buildRecord(parseJson(readSchema("CustomerCreated_v4"), event), CUSTOMER_CREATED_HEADER))
    }

    @Throws(IOException::class)
    private fun parseJson(schema: InputStream, json: String): GenericData.Record {
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

    private fun readSchema(schemaName: String) : InputStream {
        return resourceLoader.getResource("classpath:avro/$schemaName.avsc").inputStream
    }
}
