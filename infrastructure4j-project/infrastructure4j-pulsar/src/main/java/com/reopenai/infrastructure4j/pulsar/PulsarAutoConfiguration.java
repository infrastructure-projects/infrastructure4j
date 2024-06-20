package com.reopenai.infrastructure4j.pulsar;

import com.reopenai.infrastructure4j.pulsar.client.DefaultPulsarClientFactory;
import com.reopenai.infrastructure4j.pulsar.client.PulsarClientBuilderCustomizer;
import com.reopenai.infrastructure4j.pulsar.client.PulsarClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Pulsar自动装配
 *
 * @author Allen Huang
 */
@Configuration
public class PulsarAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PulsarClientFactory.class)
    public PulsarClientFactory defaultPulsarClientFactory(List<PulsarClientBuilderCustomizer> builderCustomizers) {
        return new DefaultPulsarClientFactory(builderCustomizers);
    }

}
