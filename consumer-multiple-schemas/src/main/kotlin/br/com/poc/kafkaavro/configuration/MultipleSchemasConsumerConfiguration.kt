package br.com.poc.kafkaavro.configuration

import br.com.poc.CustomerAddressAdded
import br.com.poc.CustomerCreated
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.nio.charset.Charset

@Configuration
@EnableKafka
class MultipleSchemasConsumerConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Value("\${spring.kafka.group.id}")
    private lateinit var groupId: String

    @Value("\${schema.registry.url}")
    private lateinit var schemaRegistryUrl: String

    companion object {
        const val CUSTOMER_CREATED_EVENT_TYPE = "CustomerCreated"
        const val CUSTOMER_ADDRESS_ADDED_EVENT_TYPE = "CustomerAddressAdded"
        const val EVENT_TYPE_HEADER = "type"
    }

    @Bean
    fun  customerCreatedConsumerFactory(): ConsumerFactory<String, CustomerCreated> {
        return buildConsumerFactory<CustomerCreated>(group = "group1")
    }

    @Bean
    fun  customerAddressAddedConsumerFactory(): ConsumerFactory<String, CustomerAddressAdded> {
        return buildConsumerFactory<CustomerAddressAdded>(group = "group2")
    }

    @Bean
    fun  genericConsumerFactory(): ConsumerFactory<String, GenericRecord> {
        return buildConsumerFactory<GenericRecord>(group = "group3", isSpecificRecord = false)
    }

    @Bean
    fun customerCreatedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CustomerCreated> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CustomerCreated>()
        factory.consumerFactory = customerCreatedConsumerFactory()
        factory.setRecordFilterStrategy(::filterCustomerCreated)
        return factory
    }

    @Bean
    fun customerAddressAddedKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CustomerAddressAdded> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CustomerAddressAdded>()
        factory.consumerFactory = customerAddressAddedConsumerFactory()
        factory.setRecordFilterStrategy(::filterCustomerAddressAdded)
        return factory
    }

    @Bean
    fun genericKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, GenericRecord> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, GenericRecord>()
        factory.consumerFactory = genericConsumerFactory()
        return factory
    }

    private fun <T> filterCustomerCreated(consumerRecord: ConsumerRecord<String, T>): Boolean {
        return getEventType(consumerRecord) != CUSTOMER_CREATED_EVENT_TYPE
    }

    private fun <T> filterCustomerAddressAdded(consumerRecord: ConsumerRecord<String, T>): Boolean {
        return getEventType(consumerRecord) != CUSTOMER_ADDRESS_ADDED_EVENT_TYPE
    }

    private fun getEventType(record: ConsumerRecord<String, *>): String {
        return record.headers().headers(EVENT_TYPE_HEADER).first().value().toString(Charset.defaultCharset())
    }

    private fun <T> buildConsumerFactory(group: String = groupId, isSpecificRecord: Boolean = true): DefaultKafkaConsumerFactory<String, T> {
        val configProps = HashMap<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = group
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        configProps[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
        configProps[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = isSpecificRecord.toString()
        return DefaultKafkaConsumerFactory(configProps)
    }
}