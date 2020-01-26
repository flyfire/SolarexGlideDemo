package com.solarexsoft.solarexglide.load.codec;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:12/2020-01-26
 *    Desc:
 * </pre>
 */

public class ResourceDecoderRegistry {

    private final List<Entry<?>> entries = new ArrayList<>();

    public <T> void add(Class<T> dataClass, ResourceDecoder<T> decoder) {
        entries.add(new Entry<>(dataClass, decoder));
    }
    public <Data> List<ResourceDecoder<Data>> getDecoders(Class<Data> dataClass) {
        List<ResourceDecoder<Data>> decoders = new ArrayList<>();
        for (Entry<?> entry : entries) {
            if (entry.handles(dataClass)) {
                decoders.add((ResourceDecoder<Data>) entry.decoder);
            }
        }
        return decoders;
    }

    private static class Entry<T> {
        final Class<T> dataClass;
        final ResourceDecoder<T> decoder;

        public Entry(Class<T> dataClass, ResourceDecoder<T> decoder) {
            this.dataClass = dataClass;
            this.decoder = decoder;
        }

        public boolean handles(Class<?> dataClass) {
            return this.dataClass.isAssignableFrom(dataClass);
        }
    }
}
