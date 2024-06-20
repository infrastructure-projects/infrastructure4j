package com.reopenai.infrastructure4j.beans.spi;

/**
 * RequestId提供者
 *
 * @author Allen Huang
 */
public interface RequestIdProvider {

    /**
     * 获取RequestId
     *
     * @return requestId
     */
    String getRequestId();

}
