
package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Pityubak
 */
public class XmlReadScout {

    private final Liberator liberator;

    public XmlReadScout(Liberator liberator) {
        this.liberator = liberator;

    }

    public void scout(Set<Object> set) {
        this.liberator.inject(new ArrayList<>(set));
    }

    public void init() {
        this.liberator.init(XmlReadScout.class);
    }
}
