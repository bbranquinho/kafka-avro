package br.com.emersonborges.hybridserialization

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class HybridSerializationConsumerApplication

fun main(args: Array<String>) {
    SpringApplication.run(HybridSerializationConsumerApplication::class.java, *args)
}
