package com.reopenai.infrastructure4j.rsocket.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

import java.util.Map;

/**
 * @author Allen Huang
 */
@Data
@ConfigurationPropertiesBinding
@ConfigurationProperties("spring.rsocket")
public class RsocketClientProperties {
    /**
     * 模版
     */
    private String template;
    /**
     * 别名列表
     */
    private Map<String, String> alias;
}
