package br.com.poc.kafkaavro

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class KafkaAvroProducerApplication

fun main(args: Array<String>) {
    SpringApplication.run(KafkaAvroProducerApplication::class.java, *args)
}
