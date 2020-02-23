
package com.pityubak.xmlgrinder.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Pityubak
 */
public class ConvertService {

    private final Map<Class<?>, Function<String, ?>> map = new HashMap<>();

    public <T> T convert(Class<?> type, String value) {
        Objects.requireNonNull(type, "Converting failure: Type is null.");
        Objects.requireNonNull(value, "Converting failure: Value is null.");

        return (T) this.map.get(type).apply(value);
    }

    public void addConverter(Class<?> type, Function<String, ?> function) {
        Objects.requireNonNull(type, "Registration  failure: Type is null.");
        Objects.requireNonNull(function, "Registration failure: Value is null.");

        this.map.put(type, function);

    }

}
