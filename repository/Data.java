
package com.pityubak.xmlgrinder.repository;

/**
 *
 * @author Pityubak
 * @param <T>
 */
public interface Data<T> {

    void send(T value);

    T getValue();

}
