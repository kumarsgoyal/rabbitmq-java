package com.kumarsgoyal.rabbitmq.controller;


import com.kumarsgoyal.rabbitmq.dto.Employee;
import com.kumarsgoyal.rabbitmq.producer.ProducerMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("messageContoller")
@RequestMapping("/rest/v1")
public class MessageContoller {

    @Resource
    private ProducerMessage producerMessage;

    @GetMapping("/post")
    @ResponseBody
    public String postMessage(@RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        for(int i = 0; i < count; i++) {
            Employee employee = new Employee();
            employee.setName("employee " + i);
            employee.setEmpId("" + i);
            producerMessage.sendMessage(employee);
        }

        return "Posted Success";
    }
}
