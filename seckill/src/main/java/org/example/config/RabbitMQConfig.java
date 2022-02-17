//package org.example.config;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.FanoutExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// * @description: rabbitMQ配置类
// * @author: Frankin
// * @create: 2022-02-17 13:38
// */
//@Configuration
//public class RabbitMQConfig {
//    private final String QUEUE01 = "queue_fanout01";
//    private final String QUEUE02 = "queue_fanout02";
//    private final String EXCHANGE = "fanoutExchange";
//    @Bean
//    public Queue queue(){
//        return new Queue("queue",true);
//    }
//
//    @Bean
//    public Queue queue01(){return new Queue(QUEUE01);}  //创建队列
//
//    @Bean
//    public Queue queue02(){return new Queue(QUEUE02);}
//
//    @Bean
//    public FanoutExchange fanoutExchange(){return new FanoutExchange(EXCHANGE);}    //创建交换机
//
//    //绑定交换机与队列
//    @Bean
//    public Binding binding01(){
//        return BindingBuilder.bind(queue01()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding binding02(){
//        return BindingBuilder.bind(queue02()).to(fanoutExchange());
//    }
//}
