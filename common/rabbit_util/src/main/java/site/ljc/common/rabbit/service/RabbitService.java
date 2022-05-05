package site.ljc.common.rabbit.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Hamster
 * @Date 2022/5/4 10:44
 * @Version 1.0
 */
@Service
public class RabbitService {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }
}
