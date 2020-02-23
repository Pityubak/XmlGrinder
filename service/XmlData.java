
package com.pityubak.xmlgrinder.service;

import com.pityubak.xmlgrinder.repository.Data;

/**
 *
 * @author Pityubak
 * @param <T>
 */
public class XmlData<T> implements Data<T> {

    private  T value;

    public XmlData(T value) {
        this.value = value;
    }

    @Override
    public void send(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return this.value;

    }

}
