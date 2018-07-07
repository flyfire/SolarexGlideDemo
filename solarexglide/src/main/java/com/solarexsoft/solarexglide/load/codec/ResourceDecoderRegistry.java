package com.solarexsoft.solarexglide.load.codec;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    Project: https://solarex.github.io/projects
 *    CreatAt: 22:06/2018/7/7
 *    Copyright: houruhou
 *    Desc:
 * </pre>
 */
public class ResourceDecoderRegistry {
    private final List<Entry<?>> mEntries = new ArrayList<>();

    public <T> void add(Class<T> dataClass, ResourceDecoder<T> decoder) {
        mEntries.add(new Entry<>(dataClass, decoder));
    }

    public <T> List<ResourceDecoder<T>> getDecoders(Class<T> clz){
        List<ResourceDecoder<T>> decoders = new ArrayList<>();
        for (Entry<?> entry : mEntries) {
            if (entry.handles(clz)){
                decoders.add((ResourceDecoder<T>) entry.decoder);
            }
        }
        return decoders;
    }

    private static class Entry<T> {
        private final Class<T> dataClass;
        private final ResourceDecoder<T> decoder;

        public Entry(Class<T> dataClass, ResourceDecoder<T> decoder) {
            this.dataClass = dataClass;
            this.decoder = decoder;
        }

        public boolean handles(Class<?> clz) {
            return this.dataClass.isAssignableFrom(clz);
        }
    }
}
