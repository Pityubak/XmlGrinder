
package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Pityubak
 * 
 */
public final class XmlWriteScout {

    private final Liberator liberator;

    public XmlWriteScout(final Liberator liberator) {
        this.liberator = liberator;
    }

    public void scout(Set<Object> set) {
        this.liberator.inject(new ArrayList<>(set));
    }

    public void init() {
        this.liberator.init(XmlWriteScout.class);
    }

}
