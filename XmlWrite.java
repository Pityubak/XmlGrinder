package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import com.pityubak.liberator.data.Interception;
import com.pityubak.liberator.data.ParameterInterception;
import com.pityubak.xmlgrinder.service.XmlData;
import com.pityubak.xmlgrinder.service.XmlWriteScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import com.pityubak.xmlgrinder.repository.TemporaryData;
import com.pityubak.xmlgrinder.repository.Data;

/**
 *
 * @author Pityubak
 */
public class XmlWrite {

    private final Liberator liberator;
    private final XmlWriteScout xmlScout;
    private final TemporaryData temporary;

    private final Set<Object> classList = new LinkedHashSet<>();
    private boolean isDeeper = false;
    private final Data data;

    public XmlWrite(Liberator liberator, TemporaryData data) {
        this.liberator = liberator;
        this.xmlScout = new XmlWriteScout(this.liberator);
        this.temporary = data;
        this.data = new XmlData(this.isDeeper);

        Interception scoutInterception = new ParameterInterception(XmlWriteScoutService.class);
        scoutInterception.registrate(this.data);
        scoutInterception.registrate(data);
        this.liberator.registerInterception(scoutInterception);

    }

    public void write(Object obj, String path) {
        Interception serviceInterception = new ParameterInterception(XmlWriteService.class);
        serviceInterception.registrate(path, true);
        this.liberator.registerInterception(serviceInterception);
        this.register(obj);
        this.temporary.add(classList);
        this.xmlScout.init();

        ///boolean variable will change in the XmlWriteScoutService
        while (!this.isDeeper) {

            Set<Object> set = this.temporary.getDifference();
            this.xmlScout.scout(set);
            this.isDeeper = (boolean) this.data.getValue();

        }

        this.classList.addAll(this.temporary.finalData());
        this.temporary.clear();
        //this.liberator.reset();
        this.liberator.init(XmlWrite.class);
        this.liberator.inject(new ArrayList<>(this.classList));
        this.classList.clear();
        this.isDeeper = false;
        serviceInterception.clearRemoveableData();
    }

    public void register(Object cl) {
        this.classList.add(cl);
    }
}
