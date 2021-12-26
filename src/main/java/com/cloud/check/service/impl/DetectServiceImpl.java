package com.cloud.check.service.impl;

import com.cloud.check.model.HelloDTO;
import com.cloud.check.rpc.GreeterGrpc;
import com.cloud.check.rpc.HelloReply;
import com.cloud.check.rpc.HelloRequest;
import com.cloud.check.service.IdetectService;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service("detectServiceImplGrpc")
public class DetectServiceImpl implements IdetectService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${check.grpc.timeout.second}")
    private Integer grpcTimeoutSecond;

    @Value("${check.server.id}")
    private String CHECK_SERVER_ID;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    Map<String,GreeterGrpc.GreeterBlockingStub> clients = new ConcurrentHashMap<>();

    @Override
    public HelloDTO detect(String name) throws Exception {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        GreeterGrpc.GreeterBlockingStub client = getClientByNacos();
        if(client == null) {
            throw new Exception("");
        }
        HelloReply reply = client.withDeadlineAfter(grpcTimeoutSecond, TimeUnit.SECONDS).sayHello(request);
        HelloDTO helloDTO = new HelloDTO();
        helloDTO.setMessage(reply.getMessage());
        return helloDTO;
    }

    private GreeterGrpc.GreeterBlockingStub getClientByNacos(){
        ServiceInstance instance = loadBalancerClient.choose(CHECK_SERVER_ID);
        if(instance==null){
            logger.error("没有实例子");
            return null;
        }
        String key = instance.getHost() + ":" + instance.getPort();
        if(!clients.containsKey(key)){
            synchronized (this){
                if(!clients.containsKey(key)){
                    ManagedChannel channel = NettyChannelBuilder.forAddress(instance.getHost(),instance.getPort())
                            .negotiationType(NegotiationType.PLAINTEXT)
                            .build();
                    GreeterGrpc.GreeterBlockingStub client = GreeterGrpc.newBlockingStub(channel);
                    clients.put(key,client);
                }
            }
        }
        return clients.get(key);
    }
}
