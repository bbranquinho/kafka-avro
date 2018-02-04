package br.com.emersonborges.hybridserialization

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class HybridSerializationApplication

fun main(args: Array<String>) {
    SpringApplication.run(HybridSerializationApplication::class.java, *args)
}
