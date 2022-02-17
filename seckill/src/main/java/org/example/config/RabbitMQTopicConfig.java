package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: rabbitmq配置类
 * @author: Frankin
 * @create: 2022-02-17 15:42
 */
@Configuration
public class RabbitMQTopicConfig {
    private final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchage";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    //绑定交换机与队列
    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }

}
