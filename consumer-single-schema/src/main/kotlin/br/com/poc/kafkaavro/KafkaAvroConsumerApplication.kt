package br.com.poc.kafkaavro

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration

@SpringBootApplication
@EnableAutoConfiguration(exclude = [KafkaAutoConfiguration::class])
class KafkaAvroConsumerApplication

fun main(args: Array<String>) {
    SpringApplication.run(KafkaAvroConsumerApplication::class.java, *args)
}
