package com.testeawsterraform.aws.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aws/s3")
public class AwsController {

    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AmazonSQSAsync sqs;

    @PostMapping("/note")
    public void note(@RequestParam String name, @RequestParam String content) {
        // comentario abaixo serve para criar um bucket e ja criar um arquivo
        // amazonS3.createBucket("testemichelbucketspring");
        // amazonS3.putObject("testemichelbucketspring",name+".txt",content);

        new QueueMessagingTemplate(sqs).convertAndSend("NOTA_QUEUE", content);
        jdbcTemplate.update("insert into nota (nome, conteudo) values (?,?)", name, content);
    }

    @SqsListener("NOTA_QUEUE")
    public void consumir(String message) {
        System.out.println("ola sou sqs" + message);
    }
}
