package com.reopenai.infrastructure4j.core.cache;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring cache扩展
 *
 * @author Allen Huang
 */
@Configuration
@ConditionalOnClass(CacheManager.class)
public class CacheAutoConfig {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    @ConditionalOnProperty(value = "spring.cache.type", matchIfMissing = true)
    public CompositeCacheManager compositeCacheManager(List<ObjectProvider<CacheManager>> cacheManagers) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        if (CollUtil.isNotEmpty(cacheManagers)) {
            List<CacheManager> instances = new ArrayList<>(cacheManagers.size());
            for (ObjectProvider<CacheManager> provider : cacheManagers) {
                CacheManager cacheManager = provider.getObject();
                instances.add(cacheManager);
            }
            compositeCacheManager.setCacheManagers(instances);
        }
        return compositeCacheManager;
    }

}
