
package com.pityubak.xmlgrinder.service;

import java.util.LinkedHashSet;
import java.util.Set;
import com.pityubak.xmlgrinder.repository.TemporaryData;

/**
 *
 * @author Pityubak
 */
public class TemporaryDataService implements TemporaryData {

    private final Set<Object> data = new LinkedHashSet<>();

    private final Set<Object> diff = new LinkedHashSet<>();

    @Override
    public void add(Set<Object> obj) {
        this.diff.addAll(obj);
    }

    @Override
    public void add(Object obj) {
        this.diff.add(obj);
    }

    
    @Override
    public Set<Object> getDifference() {
        Set<Object> set = new LinkedHashSet<>();
        this.diff.forEach(entry -> {
            if (!this.data.contains(entry)) {
                set.add(entry);
            }
        });
        this.data.addAll(set);
        this.diff.clear();
        return set;
    }

    @Override
    public Set<Object> finalData() {
        return this.data;
    }

    @Override
    public void clear() {
        this.data.clear();
        this.diff.clear();
    }

}
