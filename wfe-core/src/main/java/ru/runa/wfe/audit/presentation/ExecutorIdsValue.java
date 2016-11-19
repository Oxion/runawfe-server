package ru.runa.wfe.audit.presentation;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class ExecutorIdsValue implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DELIM = ";";
    private List<Long> ids = Lists.newArrayList();

    public ExecutorIdsValue() {
    }

    public ExecutorIdsValue(String idsString) {
        for (String idString : idsString.split(DELIM, -1)) {
            ids.add(Long.valueOf(idString));
        }
    }

    public List<Long> getIds() {
        return ids;
    }

}
