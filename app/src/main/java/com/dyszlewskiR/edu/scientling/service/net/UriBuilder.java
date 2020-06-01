package com.dyszlewskiR.edu.scientling.service.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Razjelll on 19.04.2017.
 */

public class UriBuilder {
    private StringBuilder mUriBuilder;
    private Map<String, String> mUriParam;

    public UriBuilder() {
        init();
    }

    public UriBuilder(String rootUri) {
        init();
        mUriBuilder.append(rootUri);
    }

    private void init() {
        mUriBuilder = new StringBuilder();
        mUriParam = new HashMap<>();
    }

    public UriBuilder addParam(String name, String value) {
        mUriParam.put(name, value);
        return this;
    }

    /**
     * Metoda, która wstawia tyllko niepuste wartości
     *
     * @param name  nazwa wstawianego parametru
     * @param value wartość wstawianego parametru
     */
    public UriBuilder tryAddParam(String name, String value) {
        if (value != null && !value.isEmpty()) {
            mUriParam.put(name, value);
        }
        return this;
    }

    public UriBuilder addParamDifferentFrom(String name, String value, int differentFrom) {
        if (value.equals(String.valueOf(differentFrom))) {
            mUriParam.put(name, value);
        }
        return this;
    }

    public UriBuilder addParamDifferentFrom(String name, String value, long differentFrom) {
        if (value.equals(String.valueOf(differentFrom))) {
            mUriParam.put(name, value);
        }
        return this;
    }

    public UriBuilder addSegment(String segment) {
        mUriBuilder.append("/").append(segment);
        return this;
    }

    public UriBuilder addRoot(String root) {
        mUriBuilder.append(root);
        return this;
    }

    public String getUri() {
        if (!mUriParam.isEmpty()) {
            mUriBuilder.append("?");
        }
        int count = 0;
        for (Map.Entry<String, String> entry : mUriParam.entrySet()) {
            mUriBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            if (count != mUriParam.size() - 1) {
                mUriBuilder.append("&");
            }
            count++;
        }
        return mUriBuilder.toString();
    }
}
