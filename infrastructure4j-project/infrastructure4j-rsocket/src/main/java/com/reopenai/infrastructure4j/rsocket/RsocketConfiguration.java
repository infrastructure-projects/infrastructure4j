package com.reopenai.infrastructure4j.rsocket;

import com.reopenai.infrastructure4j.rsocket.client.RsocketClientProperties;
import com.reopenai.infrastructure4j.rsocket.client.loadbalancer.NoopTransportLoadBalancer;
import com.reopenai.infrastructure4j.rsocket.client.loadbalancer.TransportLoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rsocket配置
 *
 * @author Allen Huang
 */
@Configuration
@EnableConfigurationProperties(RsocketClientProperties.class)
public class RsocketConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransportLoadBalancer.class)
    public TransportLoadBalancer noopTransportLoadBalancer(RsocketClientProperties properties) {
        return new NoopTransportLoadBalancer(properties);
    }


}
