package br.com.emersonborges.kafkaavro.configuration

import br.com.emersonborges.CustomerEvent
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@Configuration
@EnableKafka
class SingleSchemaConsumerConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Value("\${spring.kafka.group.id}")
    private lateinit var groupId: String

    @Value("\${schema.registry.url}")
    private lateinit var schemaRegistryUrl: String

    companion object {
        const val CUSTOMER_CREATED_EVENT_TYPE = "CustomerCreated"
        const val EVENT_TYPE_HEADER = "type"
    }

    @Bean
    fun  genericConsumerFactory(): ConsumerFactory<String, GenericRecord> {
        return buildConsumerFactory<GenericRecord>(group = "group2", isSpecificRecord = false)
    }

    @Bean
    fun  specificConsumerFactory(): ConsumerFactory<String, CustomerEvent> {
        return buildConsumerFactory<CustomerEvent>(group = "group3")
    }

    @Bean
    fun specificKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CustomerEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CustomerEvent>()
        factory.consumerFactory = specificConsumerFactory()
        return factory
    }

    @Bean
    fun genericKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, GenericRecord> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, GenericRecord>()
        factory.consumerFactory = genericConsumerFactory()
        return factory
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