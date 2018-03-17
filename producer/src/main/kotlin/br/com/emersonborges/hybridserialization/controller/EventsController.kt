package br.com.emersonborges.hybridserialization.controller

import example.avro.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class EventsController (@Qualifier("avroKafkaTemplate") val kafkaTemplate: KafkaTemplate<String, User>) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = ["/events"], consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun create() {
        val user = User
            .newBuilder()
            .setName("name")
            .setFavoriteNumber(1)
            .setFavoriteColor("Black")
            .build()
        kafkaTemplate.send("user", "key", user)
    }
}
