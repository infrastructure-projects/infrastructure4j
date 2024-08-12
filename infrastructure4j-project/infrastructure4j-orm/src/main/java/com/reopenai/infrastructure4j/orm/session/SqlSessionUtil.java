package com.reopenai.infrastructure4j.orm.session;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.sql.Array;
import java.sql.SQLException;

/**
 * MyBatis的Session相关的工具类
 *
 * @author Allen Huang
 */
public final class SqlSessionUtil {

    private static SqlSessionTemplate sqlSessionTemplate;

    /**
     * 获取SessionFactory实例
     *
     * @return session实例
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionTemplate.getSqlSessionFactory();
    }

    /**
     * 获取当前执行器的类型
     *
     * @return 执行器的类型
     */
    public static ExecutorType getExecutorType() {
        return sqlSessionTemplate.getExecutorType();
    }

    /**
     * 获取持久化异常转换器
     *
     * @return 异常转换器实例
     */
    public static PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
        return sqlSessionTemplate.getPersistenceExceptionTranslator();
    }

    /**
     * 获取当前的SqlSession实例
     *
     * @return SqlSession实例
     */
    public static SqlSession getSqlSession() {
        return SqlSessionUtils.getSqlSession(getSqlSessionFactory(), getExecutorType(), getPersistenceExceptionTranslator());
    }

    /**
     * 通过给定的类型获取该类型对应的Mapper实例
     *
     * @param type 需要生成Mapper的类型
     * @return Mapper实例
     */
    public static <T> T getMapper(Class<T> type) {
        return getSqlSession().getMapper(type);
    }

    /**
     * 创建一个Array类型的数据
     *
     * @param typeName 类型的名称
     * @param elements 数据列表
     * @return Array数据实例
     * @throws SQLException 创建Array失败时抛出的异常
     */
    public static Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        SqlSession sqlSession = getSqlSession();
        return sqlSession.getConnection().createArrayOf(typeName, elements);
    }

    /**
     * 初始化工具类的SqlSessionTemplate
     *
     * @param sessionTemplate SqlSessionTemplate实例
     */
    public static void initSqlSessionTemplate(SqlSessionTemplate sessionTemplate) {
        SqlSessionUtil.sqlSessionTemplate = sessionTemplate;
    }


}
