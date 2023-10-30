package com.kumarsgoyal.rabbitmq.controller;


import com.kumarsgoyal.rabbitmq.dto.InvoiceCreatedMessage;
import com.kumarsgoyal.rabbitmq.dto.InvoicePaidMessage;
import com.kumarsgoyal.rabbitmq.producer.InvoiceProducer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Controller("messageContoller")
@RequestMapping("/rest/v1")
public class MessageContoller {

    @Resource
    private InvoiceProducer invoiceProducer;

    @GetMapping("/post")
    @ResponseBody
    public String postMessage() throws InterruptedException {
        String randomInvoiceNumber = "INV - " + ThreadLocalRandom.current().nextInt(100, 200);
        InvoiceCreatedMessage invoiceCreatedMessage = new InvoiceCreatedMessage(152.26,
                LocalDate.now().minusDays(2), "USD", randomInvoiceNumber);
        invoiceProducer.sendInvoiceCreated(invoiceCreatedMessage);

        randomInvoiceNumber = "INV - " + ThreadLocalRandom.current().nextInt(200, 300);
        String randomPaymentNumber = "PAY -" + ThreadLocalRandom.current().nextInt(800, 1000);
        InvoicePaidMessage invoicePaidMessage = new InvoicePaidMessage(randomInvoiceNumber, LocalDate.now(), randomPaymentNumber);
        invoiceProducer.sendInvoicePaid(invoicePaidMessage);

        return "Posted Success";
    }
}
