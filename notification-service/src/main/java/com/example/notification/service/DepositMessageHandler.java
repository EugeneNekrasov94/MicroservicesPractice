package com.example.notification.service;

import com.example.notification.dto.DepositResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class DepositMessageHandler {
    private final JavaMailSender javaMailSender;

    @Autowired
    public DepositMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void receive(DepositResponseDTO depositResponseDTO) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(depositResponseDTO.getEmail());
        mailMessage.setFrom("eugenexxx1@gmail.com");
        mailMessage.setSubject("WARNING");
        mailMessage.setText(String.format("Make deposit,sum + %f", depositResponseDTO.getAmount()));
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(id = "consumer-group-1",
            topics = "${kafka.topics.test-topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handle(@Payload DepositResponseDTO depositResponseDTO) {
        receive(depositResponseDTO);
    }
}
