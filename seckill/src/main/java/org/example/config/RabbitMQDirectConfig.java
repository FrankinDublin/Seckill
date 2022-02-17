//package org.example.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @description: rabbitmq直连配置类
// * @author: Frankin
// * @create: 2022-02-17 14:44
// */
//
//@Configuration
//public class RabbitMQDirectConfig {
//    private final String QUEUE01 = "queue_direct01";
//    private final String QUEUE02 = "queue_direct02";
//    private final String EXCHANGE = "directExchange";
//    private final String ROUTINGKEY01 = "queue.red";
//    private final String ROUTINGKEY02 = "queue.green";
//
//    //创建队列
//    @Bean
//    public Queue queue01() {
//        return new Queue(QUEUE01);
//    }
//
//    @Bean
//    public Queue queue02() {
//        return new Queue(QUEUE02);
//    }
//    //创建交换机
//    @Bean
//    public DirectExchange directExchange() {
//        return new DirectExchange(EXCHANGE);
//    }
//
//    //绑定交换机与队列
//    @Bean
//    public Binding binding01(){
//        return BindingBuilder.bind(queue01()).to(directExchange()).with(ROUTINGKEY01);
//    }
//
//    @Bean
//    public Binding binding02(){
//        return BindingBuilder.bind(queue02()).to(directExchange()).with(ROUTINGKEY02);
//    }
//}
