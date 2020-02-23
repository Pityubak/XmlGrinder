
package com.pityubak.xmlgrinder.virtualnode;

import java.util.Objects;

/**
 *
 * @author Pityubak
 */
public class XmlAttribute {

    private final String name;

    private final String value;

    //Represent xml attribute
    public XmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof XmlAttribute)) {
            return false;
        }

        XmlAttribute attr = (XmlAttribute) o;
        return this.name.equals(attr.getName()) && this.value.equals(attr.getValue());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.value);
        return hash;
    }

}
