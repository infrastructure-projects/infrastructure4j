package com.reopenai.infrastructure4j.beans.builtin;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换成id-node映射，用来做索引列
        Map<ID, NODE> idMapping = nodes.stream()
                .collect(Collectors.toMap(TreeNode::getCurrentId, Function.identity()));

        // treeNodes列表只用来存根节点
        List<NODE> treeNodes = new ArrayList<>(8);
        for (NODE node : nodes) {
            // 检查节点的序号是否为空
            SEQUENCE sequence = node.getNonNullSequence();
            if (sequence == null) {
                throw new IllegalArgumentException("sequence is required");
            }
            if (node.isRoot()) {
                treeNodes.add(node);
                continue;
            }
            ID parentId = node.getParentId();
            NODE parentNode = idMapping.get(parentId);
            // 如果没有找到根节点，也当作第一个节点来处理
            if (parentNode == null) {
                treeNodes.add(node);
                continue;
            }
            // 获取当前非空的子节点
            List<NODE> childrenNodes = parentNode.getNonNullChildNodes();
            childrenNodes.add(node);
        }

        if (sort) {
            // 根节点排序
            treeNodes.sort(Comparator.comparing(TreeNode::getNonNullSequence));
            // 子节点排序
            nodes.parallelStream()
                    .map(TreeNode::getChildNodes)
                    .filter(children -> children != null && !children.isEmpty())
                    .forEach(children -> children.sort(Comparator.comparing(TreeNode::getNonNullSequence)));
        }
        return treeNodes;
    }

}
