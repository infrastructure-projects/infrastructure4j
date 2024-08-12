package com.reopenai.infrastructure4j.rsocket.client;

import com.reopenai.infrastructure4j.rsocket.client.loadbalancer.TransportLoadBalancer;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * @author Allen Huang
 */
public class RsocketClientFactoryBean implements FactoryBean<Object> {

    @Setter
    private Class<?> target;

    @Autowired
    private TransportLoadBalancer loadBalancer;

    @Autowired
    private RSocketStrategies strategies;

    @Override
    public Object getObject() throws Exception {
        RsocketStub stub = target.getAnnotation(RsocketStub.class);
        String host = stub.host();
        RSocketRequester requester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .rsocketConnector(connector -> connector.reconnect(Retry.backoff(Integer.MAX_VALUE, Duration.ofMillis(3000))))
                .transports(loadBalancer.load(target, host), new RoundRobinLoadbalanceStrategy());
        RSocketServiceProxyFactory factory = RSocketServiceProxyFactory.builder(requester)
                .blockTimeout(Duration.ofMillis(stub.readTimeout()))
                .build();
        return factory.createClient(target);
    }

    @Override
    public Class<?> getObjectType() {
        return target;
    }

}
