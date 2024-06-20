package com.reopenai.infrastructure4j.pulsar.client;

import lombok.RequiredArgsConstructor;
import org.apache.pulsar.client.api.PulsarClient;

import java.util.List;

/**
 * 默认的PulsarClient创建工厂
 *
 * @author Allen Huang
 */
@RequiredArgsConstructor
public class DefaultPulsarClientFactory implements PulsarClientFactory {

    private final List<PulsarClientBuilderCustomizer> builderCustomizers;

    @Override
    public PulsarClient createClient() {
        return null;
    }

}
