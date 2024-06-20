package com.reopenai.infrastructure4j.etcd;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ETCD自动配置
 *
 * @author Allen Huang
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(EtcdProperties.class)
public class EtcdAutoConfiguration {

    private final EtcdProperties properties;

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(Client.class)
    public Client etchClient(List<ObjectProvider<ClientInterceptor>> interceptors) {
        ClientBuilder builder = Client.builder();
        Set<String> endpoints = properties.getEndpoints();
        if (endpoints == null || endpoints.isEmpty()) {
            throw new IllegalArgumentException("etcd endpoints is required");
        }
        builder.endpoints(endpoints.toArray(new String[0]));
        for (ObjectProvider<ClientInterceptor> interceptor : interceptors) {
            interceptor.ifAvailable(builder::interceptor);
        }
        Map<String, String> headers = properties.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        Map<String, String> authHeader = properties.getAuthHeaders();
        if (authHeader != null && !authHeader.isEmpty()) {
            for (Map.Entry<String, String> entry : authHeader.entrySet()) {
                builder.authHeader(entry.getKey(), entry.getValue());
            }
        }
        String authority = properties.getAuthority();
        if (authority != null && !authority.isEmpty()) {
            builder.authority(authority);
        }
        return builder.build();
    }

}
