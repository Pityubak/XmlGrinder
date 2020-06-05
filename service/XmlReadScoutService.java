package com.pityubak.xmlgrinder.service;

import com.pityubak.liberator.annotations.MethodBox;
import com.pityubak.liberator.annotations.MethodElement;
import com.pityubak.liberator.data.Request;
import com.pityubak.liberator.data.Response;
import com.pityubak.liberator.misc.ModificationFlag;
import com.pityubak.xmlgrinder.annotation.XmlElement;
import com.pityubak.xmlgrinder.annotation.XmlList;
import com.pityubak.xmlgrinder.virtualnode.XmlNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.pityubak.xmlgrinder.repository.TemporaryData;
import com.pityubak.xmlgrinder.repository.Data;
import com.pityubak.xmlgrinder.repository.Scout;

/**
 *
 * @author Pityubak This class helps to find all other classes, that they must
 * to create Iterate solution: when main class contains some other class, add to
 * the TemporaryData, boolean variable will send with Data if it's true->while
 * cycle continue, but now with new classes
 *
 * @see XmlWriteScoutService:same logic
 */
@MethodBox
public final class XmlReadScoutService implements Scout {

    private final Data observer;

    private boolean isDeeper = false;

    private final TemporaryData data;

    private final XmlParseService root;

    private boolean isFirst = true;

    private final List<XmlNode> collection = new ArrayList<>();

    public XmlReadScoutService(final Data observer, final TemporaryData data, final XmlParseService root) {
        this.observer = observer;
        this.data = data;
        this.root = root;
    }

    @MethodElement(ModificationFlag.PRIORITY_LOW)
    public void getNestedClass(XmlElement element, Response response) {
        final Class<?> type = response.getTargetType();
        final String name = response.getTargetName();
        if (!type.isInterface() && !type.isPrimitive() && !type.toString().contains("String")) {

            
            Request request = response.getRequest();
            request.setRequestType(type);
            Object value = request.response(name);
            this.data.add(value);
            this.isDeeper = true;

        }
    }

    //though list generic type  is known
    //it's possible more than one list 
    //add class to list and compare other
    //when it's new , then create, when not, nothing's happend
    @MethodElement(ModificationFlag.PRIORITY_LOW)
    public void getListElement(XmlList xml, Response response) {
        if (isFirst) {
            final String name = response.getTargetName();
            final XmlNode parent = this.root.getRoot();

            final List<XmlNode> target = parent.getElementsByName(name);

            target.forEach(t -> {
                if (t.hasChild()) {
                    t.getChildren().forEach(this.collection::add);
                }
            });
            AtomicInteger size = new AtomicInteger(this.collection.size());
            final List<XmlNode> comp = new ArrayList<>();
            comp.addAll(this.collection);
            Collections.reverse(comp);

            this.collection.forEach(node -> {
                comp.remove(comp.size() - 1);
                comp.stream().filter(other -> (this.root.compareTo(node, other))).forEachOrdered(item -> size.getAndDecrement());
            });

            response.getTargetGenericTypes().forEach(t -> {

                for (int i = 0; i < size.get(); i++) {
                    Request request = response.getRequest();
                    request.setRequestType((Class) t);
                    Object value = request.response(name + i);
                    this.data.add(value);
                    this.isDeeper = true;
                }

            });
            isFirst = false;
        }
    }

    @Override
    public void end() {
        this.isDeeper = !this.isDeeper;
        this.observer.send(this.isDeeper);

    }

}
