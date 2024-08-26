package com.firefly.pgw.renderer;

import com.firefly.pgw.utils.ListAndArray;

import java.util.List;

public class CMesaLibList implements ListAndArray {
    public final List<String> CMesaLibIds;
    public final String[] CMesaLibs;

    public CMesaLibList(List<String> CMesaLibIds, String[] CMesaLibs) {
        this.CMesaLibIds = CMesaLibIds;
        this.CMesaLibs = CMesaLibs;
    }

    @Override
    public List<String> getList() {
        return CMesaLibIds;
    }

    @Override
    public String[] getArray() {
        return CMesaLibs;
    }
}