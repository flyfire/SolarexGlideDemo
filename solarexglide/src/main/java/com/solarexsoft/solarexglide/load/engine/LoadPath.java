package com.solarexsoft.solarexglide.load.engine;

import android.graphics.Bitmap;

import com.solarexsoft.solarexglide.load.codec.ResourceDecoder;

import java.io.IOException;
import java.util.List;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 23:06/2020-01-26
 *    Desc:
 * </pre>
 */

public class LoadPath<Data> {
    private final Class<Data> dataClass;
    private final List<ResourceDecoder<Data>> decoders;

    public LoadPath(Class<Data> dataClass, List<ResourceDecoder<Data>> decoders) {
        this.dataClass = dataClass;
        this.decoders = decoders;
    }

    public Bitmap runLoad(Data data, int width, int height) {
        Bitmap result = null;
        for (ResourceDecoder<Data> decoder : decoders) {
            try {
                if (decoder.handles(data)) {
                    result = decoder.decode(data, width, height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
