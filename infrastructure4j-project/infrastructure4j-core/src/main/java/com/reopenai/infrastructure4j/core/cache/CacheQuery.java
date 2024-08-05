package com.reopenai.infrastructure4j.core.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 缓存查询器,提供多层查询能力。当上一层查询不到数据或者查询数据出错时，会自动降级到下一层查询数据
 *
 * @author Allen Huang
 */
@NoArgsConstructor
public class CacheQuery<T> {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(CacheQuery.class);

    protected final List<Command<T>> queryLayers = new ArrayList<>(4);

    protected Exception lastException;

    protected Logger log = CacheQuery.DEFAULT_LOGGER;

    public CacheQuery(Logger logger) {
        this.log = logger;
    }

    /**
     * 添加缓存查询层
     *
     * @param queryLayer 缓存查询层
     * @return 当前缓存查询器引用
     */
    public CacheQuery<T> addQueryLayer(Command<T> queryLayer) {
        this.queryLayers.add(queryLayer);
        return this;
    }

    /**
     * 执行查询操作
     *
     * @return 查询的结果
     */
    public T query() {
        Iterator<Command<T>> iterator = queryLayers.iterator();
        return doQuery(iterator);
    }

    protected T doQuery(Iterator<Command<T>> iterator) {
        if (iterator.hasNext()) {
            Command<T> command = iterator.next();
            try {
                T cacheData = command.query();
                if (cacheData == null) {
                    cacheData = doQuery(iterator);
                    command.store(cacheData);
                }
                return cacheData;
            } catch (Exception e) {
                this.lastException = e;
                log.info("[CacheQuery]缓存查询出错.", e);
                return doQuery(iterator);
            }
        }
        if (this.lastException != null) {
            throw new RuntimeException(lastException);
        }
        return null;
    }

    /**
     * 缓存查询命令
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Command<T> {
        /**
         * 查询命令不能为空，如果为空查询过程可能会NPE
         */
        private Supplier<T> queryCommand;
        /**
         * 存储命令可以为空，为空时不进行存储
         */
        private Consumer<T> storeCommand;

        /**
         * 查询缓存数据
         *
         * @return 查询结果
         */
        public T query() {
            return queryCommand.get();
        }

        /**
         * 保存缓存数据
         *
         * @param data 保存结果
         */
        public void store(T data) {
            if (storeCommand != null) {
                storeCommand.accept(data);
            }
        }

    }

}
