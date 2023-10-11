package com.kumarsgoyal.rabbitmq.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kumarsgoyal.rabbitmq.dto.Picture;
import com.kumarsgoyal.rabbitmq.producer.ProducerMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Controller("messageContoller")
@RequestMapping("/rest/v1")
public class MessageContoller {

    private static List<String> SOURCE = List.of("web", "mobile");

    private static List<String> TYPE = List.of("jpg", "png", "svg");

    @Resource
    private ProducerMessage producerMessage;

    @GetMapping("/post")
    @ResponseBody
    public String postMessage(@RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        for(int i = 0; i < count; i++) {
            Picture picture = new Picture();
            picture.setName("pic " + i);;
            picture.setSource(SOURCE.get(i % SOURCE.size()));
            picture.setType(TYPE.get(i % TYPE.size()));
            picture.setSize(ThreadLocalRandom.current().nextInt(1, 10000));
            producerMessage.sendMessage(picture);
        }

        return "Posted Success";
    }
}
