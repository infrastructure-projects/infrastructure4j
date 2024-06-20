package com.reopenai.infrastructure4j.beans.builtin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 树结构中的某个节点
 *
 * @author Allen Huang
 */
public interface TreeNode<NODE extends TreeNode<NODE, ID, SEQUENCE>, ID, SEQUENCE extends Comparable<SEQUENCE>> {

    /**
     * 获取父节点ID
     *
     * @return 父节点ID
     */
    @JsonIgnore
    ID getParentId();

    /**
     * 获取当前节点ID
     *
     * @return 当前节点ID
     */
    @JsonIgnore
    ID getCurrentId();

    /**
     * 获取当前节点的序号。
     *
     * @return 当前节点的序号, 如果当前节点的序号为空，应该返回默认的序号.此方法需要保证返回的结果不为null
     */
    @JsonIgnore
    SEQUENCE getNonNullSequence();

    /**
     * 获取当前节点的所有子节点。如果当前节点不存在子节点，则返回null
     *
     * @return 当前节点的所有子节点
     */
    @JsonIgnore
    List<NODE> getChildNodes();

    /**
     * 获取当前节点的所有子节点.
     * 此方法要保证返回的结果不为空，且返回的结果是可编辑的。
     * 如果当前节点不存在子节点，则应该创建一个空的子节点列表，并赋值给当前节点后返回子节点列表的引用。
     *
     * @return 当前节点的所有子节点
     */
    @JsonIgnore
    List<NODE> getNonNullChildNodes();

    /**
     * 判断当前节点是否是根节点
     *
     * @return 如果是根节点则返回true，否则返回false
     */
    @JsonIgnore
    default boolean isRoot() {
        return getParentId() == null;
    }

    /**
     * 将树型节点列表转换成树型结构
     *
     * @param nodes 树型节点
     * @return 构建后的树
     */
    static <NODE extends TreeNode<NODE, ID, SEQUENCE>, ID, SEQUENCE extends Comparable<SEQUENCE>> List<NODE> newTree(List<NODE> nodes) {
        return newTree(nodes, false);
    }

    /**
     * 将树型节点列表转换成树型结构,并且对所有的子节点进行排序
     *
     * @param nodes 树型节点
     * @return 构建后的树
     */
    static <NODE extends TreeNode<NODE, ID, SEQUENCE>, ID, SEQUENCE extends Comparable<SEQUENCE>> List<NODE> newTreeAndSortChildNodes(List<NODE> nodes) {
        return newTree(nodes, true);
    }

    /**
     * 将节点列表转换成树型结构
     *
     * @param nodes 树型节点
     * @param sort  兄弟节点之间是否需要排序
     * @return 构建后的树
     */
    static <NODE extends TreeNode<NODE, ID, SEQUENCE>, ID, SEQUENCE extends Comparable<SEQUENCE>> List<NODE> newTree(List<NODE> nodes, boolean sort) {
        // TODO
        return null;
    }

}
