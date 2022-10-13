package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

// 在 DefaultSqlSession 一系列的增删改查操作的其实都是在调用Executor的接口
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    // 更新
    int update(MappedStatement ms, Object parameter) throws SQLException;

    // 查询，带分页，带缓存，BoundSql
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

    // 查询，带分页
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    // 刷新批处理语句
    List<BatchResult> flushStatements() throws SQLException;

    // 提交和回滚，参数是是否要强制
    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    // 创建CacheKey
    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

    // 判断是否缓存了
    boolean isCached(MappedStatement ms, CacheKey key);

    // 清理Session缓存
    void clearLocalCache();

    // 延迟加载
    void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);

}
