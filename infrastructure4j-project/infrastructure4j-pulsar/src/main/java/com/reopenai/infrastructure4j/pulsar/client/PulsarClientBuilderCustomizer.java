package com.reopenai.infrastructure4j.pulsar.client;

import org.apache.pulsar.client.api.ClientBuilder;

/**
 * 在构建PulsarClient实例时对它进行定制
 *
 * @author Allen Huang
 */
public interface PulsarClientBuilderCustomizer {

    /**
     * 定制PulsarClientBuilder
     *
     * @param clientBuilder ClientBuilder实例
     * @return 新的ClientBuilder实例
     */
    ClientBuilder customize(ClientBuilder clientBuilder);

}
