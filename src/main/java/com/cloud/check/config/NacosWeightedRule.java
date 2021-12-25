package com.cloud.check.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NacosWeightedRule extends AbstractLoadBalancerRule {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Autowired
    private NacosDiscoveryProperties discoveryProperties;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Override
    public Server choose(Object o) {
        BaseLoadBalancer loadBalancer = (BaseLoadBalancer) getLoadBalancer();
        String name = loadBalancer.getName();
        try {
            NamingService namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());
            Instance instance = namingService.selectOneHealthyInstance(name);
            logger.info("choose server:{}:{}",instance.getIp(),instance.getPort());
            return new NacosServer(instance);
        }catch (NacosException e){
            logger.error("ribbon choose error:",e);
            return null;
        }
    }
}
