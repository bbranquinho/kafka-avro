package br.com.emersonborges.hybridserialization

import example.avro.User
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer {

    @KafkaListener(topics = ["user"])
    fun consume(user: User) {
        println(user)
    }
}