package com.reopenai.infrastructure4j.pulsar.client;

import org.apache.pulsar.client.api.PulsarClient;

/**
 * PulsarClient实例创建工厂
 *
 * @author Allen Huang
 */
public interface PulsarClientFactory {

    /**
     * 创建PulsarClient实例
     *
     * @return PulsarClient实例
     */
    PulsarClient createClient();

}
