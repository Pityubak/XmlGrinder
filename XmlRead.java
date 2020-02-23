package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import com.pityubak.liberator.data.Interception;
import com.pityubak.liberator.data.ParameterInterception;
import com.pityubak.liberator.data.Request;
import com.pityubak.xmlgrinder.service.XmlParseService;
import com.pityubak.xmlgrinder.service.XmlData;
import com.pityubak.xmlgrinder.service.XmlReadScoutService;
import com.pityubak.xmlgrinder.service.XmlReadService;
import com.pityubak.xmlgrinder.service.XmlWriteScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.pityubak.xmlgrinder.repository.TemporaryData;
import com.pityubak.xmlgrinder.repository.Data;

/**
 *
 * @author Pityubak
 */
public class XmlRead {

    private final Liberator liberator;
    private final XmlReadScout xmlScout;
    private final TemporaryData data;

    private final Set<Object> classList = new LinkedHashSet<>();
    private boolean isDeeper = false;

    private final Data observer;
    private final Interception readScoutInterception;

    public XmlRead(Liberator liberator, TemporaryData data) {
        this.liberator = liberator;
        this.xmlScout = new XmlReadScout(this.liberator);
        this.data = data;
        this.observer = new XmlData(this.isDeeper);

        readScoutInterception = new ParameterInterception(XmlReadScoutService.class);
        readScoutInterception.registrate(this.observer);
        readScoutInterception.registrate(this.data);

        this.liberator.registerInterception(readScoutInterception);
    }

    public <Q> Q read(Class<?> obj, String path) {

        try {
            String clName = obj.getSimpleName();
            XmlParseService root = new XmlParseService(path, clName);
            Interception serviceInterception = new ParameterInterception(XmlReadService.class);
            serviceInterception.registrate(root, true);
            this.readScoutInterception.registrate(root, true);
            this.liberator.registerInterception(serviceInterception);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(XmlRead.class.getName()).log(Level.SEVERE, null, ex);
        }
        Request request = this.liberator.askForRequest();
        request.setRequestType(obj);
        Object te = request.response("");
        this.register(te);
        this.data.add(classList);
        this.xmlScout.init();

        ///boolean variable will change in the XmlReadScoutService
        while (!this.isDeeper) {

            Set<Object> set = this.data.getDifference();
            this.xmlScout.scout(set);
            this.isDeeper = (boolean) this.observer.getValue();
        }
        this.classList.addAll(this.data.finalData());

        this.liberator.resetConfig();
        this.init();
        this.liberator.injectConcreteClass(new ArrayList<>(this.classList));
        this.data.clear();
        this.readScoutInterception.clearRemoveableData();
        this.classList.clear();
        this.isDeeper = false;

        return (Q) te;

    }

    public void register(Object cl) {
        this.classList.add(cl);
    }

    private void init() {
        this.liberator.addFilter(XmlWriteService.class);
        this.liberator.addFilter(XmlWriteScoutService.class);
        this.liberator.addFilter(XmlReadScoutService.class);
        this.liberator.initConfig();

    }

}
