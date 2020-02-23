
package com.pityubak.xmlgrinder;

import com.pityubak.liberator.Liberator;
import com.pityubak.xmlgrinder.service.TemporaryDataService;
import com.pityubak.xmlgrinder.repository.TemporaryData;

/**
 *
 * @author Pityubak
 */
public class XmlGrinder  {

    private final Liberator liberator;

    private final TemporaryData output;

    private final XmlWrite xmlWrite;

    private final XmlRead xmlRead;

    public XmlGrinder() {

        this.liberator = new Liberator(XmlGrinder.class);
        this.output = new TemporaryDataService();
        this.xmlWrite = new XmlWrite(this.liberator, this.output);
        this.xmlRead = new XmlRead(this.liberator, this.output);
    }

    public void write(Object obj, String path) {
        this.xmlWrite.write(obj, path);
    }

    public <Q> Q read(Class<?> targetClass, String path) {

        return this.xmlRead.read(targetClass, path);

    }

}
