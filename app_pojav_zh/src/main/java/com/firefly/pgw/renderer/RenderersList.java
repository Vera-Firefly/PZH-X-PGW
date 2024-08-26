package com.firefly.pgw.renderer;

import com.firefly.pgw.utils.ListAndArray;

import java.util.List;

public class RenderersList implements ListAndArray {
    public final List<String> rendererIds;
    public final String[] rendererDisplayNames;

    public RenderersList(List<String> rendererIds, String[] rendererDisplayNames) {
        this.rendererIds = rendererIds;
        this.rendererDisplayNames = rendererDisplayNames;
    }

    @Override
    public List<String> getList() {
        return rendererIds;
    }

    @Override
    public String[] getArray() {
        return rendererDisplayNames;
    }
}