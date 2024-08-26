package com.firefly.pgw.renderer;

import com.firefly.pgw.utils.ListAndArray;

import java.util.List;

public class CMesaLDOList implements ListAndArray {
    public final List<String> CMesaLDOIds;
    public final String[] CMesaLDO;

    public CMesaLDOList(List<String> CMesaLDOIds, String[] CMesaLDO) {
        this.CMesaLDOIds = CMesaLDOIds;
        this.CMesaLDO = CMesaLDO;
    }

    @Override
    public List<String> getList() {
        return CMesaLDOIds;
    }

    @Override
    public String[] getArray() {
        return CMesaLDO;
    }
}