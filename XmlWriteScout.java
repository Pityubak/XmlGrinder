
package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import com.pityubak.liberator.misc.Insertion;
import com.pityubak.xmlgrinder.service.XmlReadScoutService;
import com.pityubak.xmlgrinder.service.XmlReadService;
import com.pityubak.xmlgrinder.service.XmlWriteScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import java.util.ArrayList;
import java.util.Set;
import com.pityubak.xmlgrinder.repository.Scout;

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
        this.liberator.injectConcreteClass(new ArrayList<>(set));
    }

    public void init() {
        this.liberator.resetConfig();
        this.liberator.registerAbstractMethod(XmlWriteScoutService.class, Scout.class, Insertion.AFTER_LOW);
        this.liberator.addFilter(XmlReadService.class);
        this.liberator.addFilter(XmlWriteService.class);
        this.liberator.addFilter(XmlReadScoutService.class);
        this.liberator.initConfig();
    }

}
