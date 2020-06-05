package com.pityubak.xmlgrinder.service;

import com.pityubak.liberator.annotations.MethodBox;
import com.pityubak.liberator.annotations.MethodElement;
import com.pityubak.liberator.data.Request;
import com.pityubak.liberator.data.Response;
import com.pityubak.liberator.misc.ModificationFlag;
import com.pityubak.xmlgrinder.annotation.Attribute;
import com.pityubak.xmlgrinder.annotation.XmlElement;
import com.pityubak.xmlgrinder.annotation.XmlList;
import com.pityubak.xmlgrinder.virtualnode.XmlNode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * @author Pityubak
 */
@MethodBox
public class XmlReadService {

    private final XmlNode root;

    private final Map<String, List<String>> map = new HashMap<>();

    private final List<XmlNode> collection = new ArrayList<>();

    private final ConvertService converter;

    private List<XmlNode> targetElementList;

    private boolean isFirst = true;

    private final XmlParseService mainRoot;

    public XmlReadService(XmlParseService mainRoot) {
        this.mainRoot = mainRoot;
        this.root = this.mainRoot.getRoot();
        this.converter = new ConvertService();
        this.addTypeToConverter();
    }

    //---- Get data from XmlParseService, and put the list with field name ----//
    @MethodElement(ModificationFlag.PRIORITY_HIGH)
    public void getValue(XmlElement element, Response response) {
        getDataFromRoot(response);

    }

    @MethodElement(ModificationFlag.PRIORITY_HIGH)
    public void getListValue(XmlList xml, Response response) {
        getDataFromRoot(response);
    }

    @MethodElement(ModificationFlag.PRIORITY_HIGH)
    public void getAttribute(Attribute attr, Response response) {
        getDataFromRoot(response);

    }

    private void getDataFromRoot(Response response) {
        final String name = response.getTargetName();
        final List<String> list = this.root.search(name).stream().collect(Collectors.toList());
        this.map.put(name, list);
    }

    //---- Set fields with stored value ----//
    //Beacuse order fixed by XmlReadScoutService and by XmlParseService
    // the first value of list convert to right type, and then remove
    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public List setListElement(XmlList xml, Response response) {

        final String name = response.getTargetName();

        final List list = this.map.get(name).stream().collect(Collectors.toList());

        if (isFirst) {
            targetElementList = this.root.getElementsByName(name);
            isFirst = false;

        }
        XmlNode main = this.targetElementList.get(0);

        int colSize = this.collection.size();
        AtomicInteger size = new AtomicInteger(main.getChildrenSize());
        List<Integer> counter = new ArrayList<>();

        for (int j = 0; j < colSize; j++) {
            for (int k = 0; k < main.getChildrenSize(); k++) {
                XmlNode collElement = this.collection.get(j);
                XmlNode mainElement = main.getChildren().get(k);
                if (this.mainRoot.compareTo(collElement, mainElement)) {
                    size.getAndDecrement();
                    counter.add(j);
                }
            }
        }

        response.getTargetGenericTypes().forEach(t -> {

            for (int i = colSize; i < colSize + size.get(); i++) {
                Object value = createValue(response, t, name, i);
                list.add(value);
            }

            counter.forEach(r -> {
                Object value = createValue(response, t, name, r);
                list.add(value);
            });

        });
        if (main.hasChild()) {
            main.getChildren().forEach(this.collection::add);
        }
        this.targetElementList.remove(0);

        return list;
    }

    
    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public <T> T setElementValue(XmlElement element, Response response) {

        final String name = response.getTargetName();
        final Class<?> type = response.getTargetType();
        final List<String> elementList = this.map.get(name);
        T value = null;

        if (!elementList.isEmpty()) {
            String temp = elementList.get(0);
            value = this.converter.convert(type, temp);
            elementList.remove(temp);
        } else {
            value = (T) this.createValue(response, type, name, null);
        }

        return value;

    }

    @MethodElement(ModificationFlag.PRIORITY_NORMAL)
    public <T> T setAttribute(Attribute attr, Response response) {
        final String name = response.getTargetName();
        final Class<?> type = response.getTargetType();
        final List<String> attrList = this.map.get(name);

        T value = null;

        if (!attrList.isEmpty()) {
            String temp = attrList.get(0);

            value = this.converter.convert(type, temp);
            attrList.remove(temp);
        }

        return value;
    }

    private Object createValue(Response response, Type t, final String name, Integer r) {
        Request request = response.getRequest();
        request.setRequestType((Class) t);
        Object value = r == null ? request.response(name) : request.response(name + r);
        return value;
    }

    private void addTypeToConverter() {
        this.converter.addConverter(Integer.class, t -> Integer.valueOf(t));
        this.converter.addConverter(int.class, Integer::parseInt);
        this.converter.addConverter(Long.class, t -> Long.valueOf(t));
        this.converter.addConverter(long.class, Long::parseLong);
        this.converter.addConverter(Double.class, t -> Double.valueOf(t));
        this.converter.addConverter(double.class, Double::parseDouble);
        this.converter.addConverter(Float.class, t -> Float.valueOf(t));
        this.converter.addConverter(float.class, Float::parseFloat);
        this.converter.addConverter(Byte.class, t -> Byte.valueOf(t));
        this.converter.addConverter(byte.class, Byte::parseByte);
        this.converter.addConverter(Short.class, t -> Short.valueOf(t));
        this.converter.addConverter(short.class, Short::valueOf);
        this.converter.addConverter(Character.class, t -> t.charAt(0));
        this.converter.addConverter(char.class, t -> t.charAt(0));
        this.converter.addConverter(Boolean.class, t -> Boolean.valueOf(t));
        this.converter.addConverter(boolean.class, Boolean::parseBoolean);
        this.converter.addConverter(String.class, t -> t);
    }
}
