package com.example.notification.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.LoggingErrorHandler;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private static final String DLT_TOPIC_SUFFIX = ".dlt";

    private final ProducerFactory<Object,Object> producerFactory;
    private final ConsumerFactory<Object,Object> consumerFactory;

    @Bean
    public KafkaTemplate<Object,Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object,Object> kafkaListenerContainerFactory(LoggingErrorHandler errorHandler) {
        // Позволяет создавать консюмеров, которые могут обрабатывать сообщения из нескольких партиций Kafka одновременно,
        // а также настраивать параметры такие как количество потоков, хэндлинг и т.д.
        ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        // Настройка фабрики для создания консьюмера Kafka
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory);
        // Обработка сообщений в 4 потока
        kafkaListenerContainerFactory.setConcurrency(4);
        return kafkaListenerContainerFactory;
    }

    /**
     * Публикатор в dead-letter topic.
     */
    @Bean
    public DeadLetterPublishingRecoverer publisher(KafkaTemplate<Object, Object> bytesTemplate) {
        //  Определяем логику выбора партиции для отправки сообщения в DLT.
        //  В данном случае, создаём новый объект TopicPartition, используя имя топика (consumerRecord.topic()) и добавляя суффикс DLT_TOPIC_SUFFIX,
        //  а также номер партиции (consumerRecord.partition()).
        //  Следовательно в DLT топике должно быть столько партиций сколько и в топике откуда читаем
        return new DeadLetterPublishingRecoverer(bytesTemplate, (consumerRecord, exception) ->
                new TopicPartition(consumerRecord.topic() + DLT_TOPIC_SUFFIX, consumerRecord.partition()));
    }

    /**
     * Обработчик исключений при получении сообщений из kafka по умолчанию.
     */
    @Bean
    public LoggingErrorHandler errorHandler(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
        return new LoggingErrorHandler();
    }

}
