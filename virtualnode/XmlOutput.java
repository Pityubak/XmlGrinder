package com.pityubak.xmlgrinder.virtualnode;

import com.pityubak.xmlgrinder.repository.Output;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Pityubak
 */
public final class XmlOutput implements Output {

    private final Document dom;

    private final XmlNode root;

    public XmlOutput(final Document dom, final XmlNode root) {
        this.dom = dom;
        this.root = root;
    }

    //it builds dom based on main xmlNode  
    private void iterateNode(final Element rootElement,final XmlNode node,final boolean isRoot) {

        final Element childElement = this.dom.createElement(node.getNodeName());
        if (node.getAttribute() != null) {
            childElement.setAttribute(node.getAttribute().getName(), String.valueOf(node.getAttribute().getValue()));
        }
        if (node.getNodeValue() != null && !node.getNodeValue().isEmpty()) {
            if (node.isCollectionElement()) {
                childElement.appendChild(this.dom.createTextNode(String.valueOf(node.getNodeValue())));
            } else {
                node.getNodeValue().forEach(n -> childElement.appendChild(this.dom.createTextNode(String.valueOf(n))));
            }
        }
        if (node.hasChild()) {
            node.getChildren().forEach(nd -> iterateNode(childElement, nd, false));

        }
        if (isRoot) {
            this.dom.appendChild(childElement);

        } else {
            rootElement.appendChild(childElement);
        }
    }

    private void build() {
        final Element rootElement = this.dom.createElement(root.getNodeName());
        this.iterateNode(rootElement, root, true);
    }

    @Override
    public void write(String path) {
        try {
            this.build();
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer tr = factory.newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(path)));
        } catch (FileNotFoundException | TransformerException ex) {
            Logger.getLogger(XmlWriteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
