
package com.pityubak.xmlgrinder.service;

import com.pityubak.liberator.annotations.MethodBox;
import com.pityubak.liberator.annotations.MethodElement;
import com.pityubak.liberator.data.Response;
import com.pityubak.liberator.misc.ModificationFlag;
import com.pityubak.xmlgrinder.annotation.XmlElement;
import com.pityubak.xmlgrinder.annotation.XmlList;
import java.util.List;
import com.pityubak.xmlgrinder.repository.TemporaryData;
import com.pityubak.xmlgrinder.repository.Data;
import com.pityubak.xmlgrinder.repository.Scout;

/**
 *
 * @author Pityubak
 * This class helps to find all other classes, that they must to create
 * Iterate solution: when main class contains some other class, 
 * add to the TemporaryData, boolean variable will send with Data
 * if it's true->while cycle continue, but now with new classes
 * 
 * @see XmlReadScoutService:same logic
 */
@MethodBox
public final class XmlWriteScoutService implements Scout {

    private final Data data;

    private boolean isDeeper = false;

    private final TemporaryData temporary;

    public XmlWriteScoutService(final Data observer, final TemporaryData data) {
        this.data = observer;
        this.temporary = data;

    }

    
    @MethodElement(ModificationFlag.PRIORITY_LOW)
    public void getNestedClass(XmlElement element, Response response) {

        final Object type = response.getTargetType();
        final Object value = response.getValue();
        if (type.toString().contains("class") && !type.toString().contains("String")) {
            this.temporary.add(value);
            this.isDeeper = true;

        } else {
            this.isDeeper = false;
        }

    }

    @MethodElement(ModificationFlag.PRIORITY_LOW)
    public void getLisElement(XmlList xml, Response response) {
        final Object type = response.getTargetType();

        if (type.toString().contains("List")) {
            List<?> list = (List<?>) response.getValue();
            list.forEach(el -> {
                this.isDeeper = true;
                this.temporary.add(el);
            });
        }
    }

    @Override
    public void end() {
        this.isDeeper = !this.isDeeper;
        this.data.send(this.isDeeper);

    }
}
