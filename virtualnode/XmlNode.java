package com.pityubak.xmlgrinder.virtualnode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Pityubak
 */
public final class XmlNode {

    private final List<String> nodeValue = new ArrayList<>();

    private final String nodeName;

    private XmlAttribute attribute;

    private final List<XmlNode> children = new ArrayList<>();

    private boolean collectionElement;

    public XmlNode(final String nodeName) {

        this.nodeName = nodeName;
    }

    //We need all items with same name 
    //but order is important--high order data must overtake lower ordered value
    public List<String> search(final String name) {

        List<String> list = new ArrayList<>();
        Queue<XmlNode> queue = new ArrayDeque();
        queue.add(this);

        XmlNode currentNode;

        while (!queue.isEmpty()) {

            currentNode = queue.remove();
            //Attribute null check, and then compare attribute"s name with parameter
            //if equals, add to list
            if (currentNode.getAttribute() != null && currentNode.getAttribute().getName().equals(name)) {
                list.add(currentNode.getAttribute().getValue());
            }
            //if nodeName equals with parameter, then add currentNode to list
            if (currentNode.getNodeName().equals(name)) {
                if (!currentNode.getNodeValue().isEmpty()) {
                    currentNode.getNodeValue().forEach(list::add);
                }
            } else {
                //continue with children
                queue.addAll(currentNode.getChildren());
            }

        }

        return list;

    }

    //Same logic is as above, but it's return with collection of XmlNode
    public List<XmlNode> getElementsByName(final String name) {

        List<XmlNode> list = new ArrayList<>();
        Queue<XmlNode> queue = new ArrayDeque();
        queue.add(this);

        XmlNode currentNode;

        while (!queue.isEmpty()) {

            currentNode = queue.remove();

            if (currentNode.getNodeName().equals(name)) {
                list.add(currentNode);
            } else {
                queue.addAll(currentNode.getChildren());
            }

        }

        return list;

    }

    public int getChildrenSize() {
        return this.children.size();
    }

    public void addNodeValue(String value) {
        this.nodeValue.add(value);
    }

    public void removeNodeValue(String value) {
        this.nodeValue.remove(value);
    }

    public boolean isCollectionElement() {
        return collectionElement;
    }

    public void setCollectionElement(boolean collectionElement) {
        this.collectionElement = collectionElement;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public void appendChild(XmlNode node) {
        this.children.add(node);
    }

    public void remove(XmlNode node) {
        this.children.remove(node);
    }

    public List<String> getNodeValue() {
        return nodeValue;
    }

    public String getNodeName() {
        return nodeName;
    }

    public List<XmlNode> getChildren() {
        return children;
    }

    public XmlAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(XmlAttribute attribute) {
        this.attribute = attribute;
    }

}
