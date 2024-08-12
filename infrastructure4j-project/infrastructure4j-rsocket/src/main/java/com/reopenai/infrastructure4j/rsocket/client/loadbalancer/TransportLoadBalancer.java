package com.reopenai.infrastructure4j.rsocket.client.loadbalancer;

import io.rsocket.loadbalance.LoadbalanceTarget;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Allen Huang
 */
public interface TransportLoadBalancer {

    Flux<List<LoadbalanceTarget>> load(Class<?> target, String host);

}
