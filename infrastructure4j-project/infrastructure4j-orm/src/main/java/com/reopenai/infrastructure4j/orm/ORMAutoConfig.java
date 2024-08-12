package com.reopenai.infrastructure4j.orm;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * ORM自动配置
 *
 * @author Allen Huang
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(SqlSessionFactory.class)
public class ORMAutoConfig {



}
