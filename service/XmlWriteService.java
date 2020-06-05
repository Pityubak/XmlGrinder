package com.pityubak.xmlgrinder.service;

import com.pityubak.liberator.annotations.MethodBox;
import com.pityubak.liberator.annotations.MethodElement;
import com.pityubak.liberator.data.Response;
import com.pityubak.liberator.misc.ModificationFlag;
import com.pityubak.xmlgrinder.annotation.Attribute;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import com.pityubak.xmlgrinder.annotation.Xml;
import com.pityubak.xmlgrinder.annotation.XmlElement;
import com.pityubak.xmlgrinder.annotation.XmlList;
import com.pityubak.xmlgrinder.virtualnode.XmlAttribute;
import com.pityubak.xmlgrinder.virtualnode.XmlNode;
import com.pityubak.xmlgrinder.virtualnode.XmlOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.SAXException;
import com.pityubak.xmlgrinder.repository.Establishing;
import com.pityubak.xmlgrinder.repository.Writeable;
import com.pityubak.xmlgrinder.repository.CounterResetable;
import com.pityubak.xmlgrinder.repository.NodeCountable;
import com.pityubak.xmlgrinder.repository.Output;

/**
 *
 * @author Pityubak
 */
@MethodBox
public class XmlWriteService implements Writeable, Establishing, CounterResetable, NodeCountable {

    private final DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();

    private Document dom;

    private XmlNode node;

    private String elementName;

    private long id = 0;

    private final Map<Long, String> uniques = new HashMap<>();

    private final Map<Long, XmlNode> map = new HashMap<>();

    private boolean isFirst = true;

    private final List<Long> removeableList = new ArrayList<>();

    private final String path;

    public XmlWriteService(String path) {
        this.path = path;
    }

    //Class registration and dom creation
    @MethodElement(ModificationFlag.PRIORITY_HIGH)
    public void registerPath(Xml xml, Response response) throws SAXException {

        try {
            final String name = response.getTargetName();
            final String fullName = response.getCallerClassFullName();

            if (isFirst) {
                DocumentBuilder builder = db.newDocumentBuilder();
                this.dom = builder.newDocument();
                isFirst = false;
            } else {
                //we use fullname like reference
                this.uniques.put(this.id, fullName);
            }
            this.elementName = name;

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XmlWriteService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //---With value of field create it virtualnodes and append by id---//
    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public void registerAttribute(Attribute attr, Response response) {

        if (this.map.containsKey(this.id)) {
            XmlAttribute attribute = new XmlAttribute(response.getTargetName(), String.valueOf(response.getValue()));
            this.map.get(this.id).setAttribute(attribute);
        }
    }

    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public void registerListElement(XmlList xmlList, Response response) {
        final String name = response.getTargetName();
        final List<?> list = (List<?>) response.getValue();
        //When current root (specify by id) does not contain XmlNode with same name
        //append, but it contains , then created XmlNode add list of value
        //!!!!!!!nullchecking
        list.forEach(t -> {
            if (this.map.containsKey(this.id)) {
                XmlNode xmlNode = new XmlNode(name);
                xmlNode.addNodeValue(String.valueOf(t));
                xmlNode.setCollectionElement(true);
                boolean isContain = false;
                for (XmlNode targetNode : this.map.get(this.id).getChildren()) {
                    if (targetNode.getNodeName().equals(name)) {
                        targetNode.addNodeValue(String.valueOf(t));
                        isContain = true;
                    }
                }
                if (!isContain) {
                    this.map.get(this.id).appendChild(xmlNode);
                }

            }

        });
    }

    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public void registerPlainElement(XmlElement element, Response response) {

        final String name = response.getTargetName();
        final Object value = response.getValue();

        if (this.map.containsKey(this.id)) {
            XmlNode xmlNode = new XmlNode(name);
            xmlNode.addNodeValue(String.valueOf(value));
            this.map.get(this.id).appendChild(xmlNode);
        }

    }

    private void iterate(Collection<XmlNode> list, String value, XmlNode xml, Long id) {

        list.stream().map(el -> {
            if (el.getNodeValue() != null) {
                Iterator it = el.getNodeValue().iterator();

                while (it.hasNext()) {
                    Object nodeValue = it.next();

                    if (nodeValue.toString().contains(value)) {

                        this.removeableList.add(id);
                        it.remove();
                        //XmlList
                        if (el.isCollectionElement()) {

                            el.appendChild(xml);

                        } else {
                            el.setAttribute(xml.getAttribute());
                            xml.getChildren().forEach(el::appendChild);
                        }
                    }
                }

            }
            return el;
        }).forEachOrdered(el -> iterate(el.getChildren(), value, xml, id));

    }

    @Override
    public void write() {

        this.uniques.keySet().forEach(m -> this.iterate(this.map.values(), this.uniques.get(m), this.map.get(m), m));

        this.map.values().forEach(el -> {
            this.node = el;
        });
        //remove unnecessary XmlNode from root
        this.removeableList.forEach(t -> {
            if (this.map.keySet().contains(t)) {
                this.map.remove(t);
            }
        });
        //remain only main XmlNode 

        Output xmlOutput = new XmlOutput(this.dom, this.node);
        xmlOutput.write(path);
        this.map.clear();
        this.uniques.clear();
        this.removeableList.clear();
        this.id = 0;
        this.isFirst = true;

    }

    @Override
    public void create() {
        //XmlNode creation and store it with id
        final XmlNode xmlNode = new XmlNode(this.elementName);
        this.map.put(id++, xmlNode);

    }

    @Override
    public void reset() {
        this.id = 0;
    }

    @Override
    public void count() {
        this.id++;
    }

}
