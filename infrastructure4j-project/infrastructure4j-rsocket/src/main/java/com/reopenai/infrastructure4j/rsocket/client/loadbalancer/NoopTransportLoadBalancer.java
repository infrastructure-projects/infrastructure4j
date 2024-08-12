package com.reopenai.infrastructure4j.rsocket.client.loadbalancer;

import com.reopenai.infrastructure4j.rsocket.client.RsocketClientProperties;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpClient;

import java.util.List;
import java.util.Optional;

/**
 * @author Allen Huang
 */
@RequiredArgsConstructor
public class NoopTransportLoadBalancer implements TransportLoadBalancer, EnvironmentAware {

    protected final RsocketClientProperties properties;

    protected Environment environment;

    @Override
    public Flux<List<LoadbalanceTarget>> load(Class<?> target, String host) {
        return Flux.just(List.of(LoadbalanceTarget.from("default", createClientTransport(target, host))));
    }

    protected ClientTransport createClientTransport(Class<?> target, String host) {
        host = parseHost(host);
        int index = host.lastIndexOf(":");
        if (index < 1) {
            throw new BeanCreationException(String.format("无法解析Rsocket client端口号.class=%s,host=%s", target.getName(), host));
        }
        String address = host.substring(0, index);
        String port = host.substring(index + 1);
        TcpClient tcpClient = TcpClient.create()
                .host(address)
                .port(Integer.parseInt(port));
        return TcpClientTransport.create(tcpClient);
    }

    protected String parseHost(String host) {
        String clientHost = Optional.ofNullable(properties.getAlias())
                .map(p -> p.get(host))
                .filter(StringUtils::hasText)
                .orElseGet(() -> {
                    String template = properties.getTemplate();
                    if (StringUtils.hasText(template)) {
                        return String.format(template, host);
                    }
                    return host;
                });
        return this.environment.resolvePlaceholders(clientHost);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
