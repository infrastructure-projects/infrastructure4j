package com.reopenai.infrastructure4j.etcd;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * Etcd properties
 *
 * @author Allen Huang
 */
@Data
@ConfigurationProperties("spring.etcd")
public class EtcdProperties {

    private Set<String> endpoints;

    private Map<String, String> headers;

    private Map<String, String> authHeaders;

    private String authority;

}
