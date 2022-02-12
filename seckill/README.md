## 系统介绍
本系统是使用SpringBoot开发的高并发限时抢购秒杀系统，除了实现基本的登录、查看商品列表、秒杀、下单等功能，项目中还针对高并发情况实现了系统缓存、降级和限流。

## 开发工具
IntelliJ IDEA + Navicat + Sublime Text3 + Git + Chrome

## 压测工具
JMeter


## 开发技术
前端技术 ：Bootstrap + jQuery + Thymeleaf

后端技术 ：SpringBoot + MyBatis + MySQL

中间件技术 : Druid + Redis + RabbitMQ + Guava   

## 实现技术点
### 1. 两次MD5加密

将用户输入的密码和固定Salt通过MD5加密生成第一次加密后的密码，再讲该密码和随机生成的Salt通过MD5进行第二次加密，最后将第二次加密后的密码和用户Salt存数据库

好处：    
     
1. 第一次作用：防止用户明文密码在网络进行传输
2. 第二次作用：防止数据库被盗，避免通过MD5反推出密码，双重保险

### 2. JSR303自定义参数验证
使用JSR303自定义校验器，实现对用户账号、密码的验证，免去冗杂的健壮性保护措施。

### 3. 全局异常统一处理
通过拦截所有异常，对各种异常进行相应的处理，当遇到异常就逐层上抛，一直抛到最终由一个统一的、专门负责异常处理的地方处理，这有利于对异常的维护。

### 2. session共享
验证用户账号密码都正确情况下，通过UUID生成唯一id作为token，再将token作为key、用户信息作为value模拟session存储到redis，同时将token存储到cookie，保存登录状态

好处： 在分布式集群情况下，服务器间需要同步，定时同步各个服务器的session信息，会因为延迟到导致session不一致，使用redis把session数据集中存储起来，解决session不一致问题。

总流程：用户输入账号密码--后端通过账号查出完整信息--生成token(uuid)--将token存入cookie--将token和用户对象(通过json)存入redis--验证页面通过cookie拿出token--通过token从redis中取出user