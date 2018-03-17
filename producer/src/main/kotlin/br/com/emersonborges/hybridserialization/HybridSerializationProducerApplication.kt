package br.com.emersonborges.hybridserialization

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class HybridSerializationProducerApplication

fun main(args: Array<String>) {
    SpringApplication.run(HybridSerializationProducerApplication::class.java, *args)
}
