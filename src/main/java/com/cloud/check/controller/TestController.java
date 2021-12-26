package com.cloud.check.controller;

import com.cloud.check.model.HelloDTO;
import com.cloud.check.service.IdetectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("cloudcheck/v1")
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdetectService detectServiceImplGrpc;

    @RequestMapping(value = "/call/test",method = RequestMethod.GET)
    public Mono<ResponseEntity<Map>> test(
            @RequestParam("result") String result){
        String echo = "";
        try {
            HelloDTO helloDTO = detectServiceImplGrpc.detect(result);
            echo = helloDTO.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map map = new HashMap<>();
        map.put("code",200);
        map.put("echo",echo);
        return Mono.just(ResponseEntity.ok(map));
    }
}
