package com.scsentry.common.util;

public class TableSerialNo {

    private TableSerialNo() {

    }

    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static int get() {
        int serialNo = 2;
        if (threadLocal.get() != null) {
            serialNo = threadLocal.get();
        }
        threadLocal.set(serialNo + 1);
        return serialNo;
    }

    public static void clear() {
        threadLocal.remove();
    }
}
