package com.pityubak.xmlgrinder.service;

import com.pityubak.xmlgrinder.virtualnode.XmlAttribute;
import com.pityubak.xmlgrinder.virtualnode.XmlNode;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

/**
 *
 * @author Pityubak
 */
public final class XmlParseService {

    private final DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder builder;
    private final Document dom;

    private final XmlNode root;

    private final String path;

    public XmlParseService(String path, String rootName)
            throws SAXException, IOException,
            ParserConfigurationException {
        this.path = path;

        this.db.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        this.builder = this.db.newDocumentBuilder();
        this.dom = this.builder.parse(this.path);
        this.dom.getDocumentElement().normalize();
        this.root = new XmlNode(rootName);

        DocumentTraversal traversal = (DocumentTraversal) this.dom;
        TreeWalker walker = traversal.createTreeWalker(
                this.dom.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT, null, true);

        traverseLevel(walker, this.root);

    }

    //parse xml file to XmlNode
    private void traverseLevel(TreeWalker walker,
            XmlNode xmlNode) {

        Node node = walker.getCurrentNode();
        XmlNode nodes = null;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            nodes = new XmlNode(node.getNodeName());
            xmlNode.appendChild(nodes);
            if (node.hasAttributes()) {
                NamedNodeMap map = node.getAttributes();
                for (int i = 0; i < map.getLength(); i++) {
                    Node attr = map.item(i);
                    nodes.setAttribute(new XmlAttribute(attr.getNodeName(), attr.getNodeValue()));
                }
            }
        }

        if (node.getNodeType() == Node.TEXT_NODE) {

            String content = node.getTextContent().trim();

            if (content.length() > 0) {
                xmlNode.addNodeValue(content);

            }
        }

        for (Node n = walker.firstChild(); n != null;
                n = walker.nextSibling()) {

            traverseLevel(walker, nodes);
        }

        walker.setCurrentNode(node);
    }

    public boolean compareTo(final XmlNode base, final XmlNode node) {
        if (node == null) {
            return false;
        }
        if (node.getAttribute() != null && !node.getAttribute().equals(base.getAttribute())) {
            return false;
        }
        if (!node.getNodeName().equals(base.getNodeName())) {
            return false;
        }
        int valueNSize = node.getNodeValue().size();
        int valueBSize = base.getNodeValue().size();

        if (valueNSize != valueBSize) {
            return false;
        }
        for (int k = 0; k < valueNSize; k++) {
            if (!node.getNodeValue().get(k).equals(base.getNodeValue().get(k))) {
                return false;
            }
        }

        int nSize = node.getChildrenSize();
        int tSize = base.getChildrenSize();

        if (nSize != tSize) {
            return false;
        }
        for (int i = 0; i < nSize; i++) {
            XmlNode same = base.getChildren().get(i);
            XmlNode other = node.getChildren().get(i);
            return this.compareTo(same, other);
        }

        return true;
    }

    public Document getDom() {
        return dom;
    }

    public XmlNode getRoot() {
        return root;
    }

}
