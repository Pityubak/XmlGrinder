
package com.pityubak.xmlgrinder.repository;

import java.util.Set;

/**
 *
 * @author Pityubak
 */
public interface TemporaryData {

    void add(Set<Object> obj);

    void add(Object obj);

    Set<Object> getDifference();

    Set<Object> finalData();
    
    void clear();
}
