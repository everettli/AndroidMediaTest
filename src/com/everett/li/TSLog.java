package com.everett.li;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.util.AndroidRuntimeException;

public class TSLog {

    static final String TAG = TSLog.class.getName();

    private static HashMap<String, Period> mMap = new HashMap<String, Period>();
    private static ValueComparator bvc = new ValueComparator(mMap);
    private static TreeMap<String, Period> sorted_map = new TreeMap<String, Period>(bvc);

    public static void startTs(String key) {
        Period period = new Period(key, System.currentTimeMillis(), 0L);
        mMap.put(key, period);
    }

    public static void endTs(String key) {
        Period period = mMap.get(key);
        if (period != null) {
            period.setEndTs(System.currentTimeMillis());
        } else {
            throw new AndroidRuntimeException("key: " + key + " must call startTs");
        }
    }

    public static StringBuilder outPutAll() {
        StringBuilder sb = new StringBuilder();
        sorted_map.putAll(mMap);
        Set<Entry<String, Period>> set = sorted_map.entrySet();
        for(Entry<String, Period> entry : set){
            sb.append(entry.getValue().toString());
            sb.append("\n");
        }
        return sb;
    }
}

class ValueComparator implements Comparator<String> {

    Map<String, Period> base;

    public ValueComparator(Map<String, Period> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
        if (base.get(a).getStartTs() < base.get(b).getStartTs()) {
            return -1;
        } else {
            return 1;
        }
    }
}

class Period {
    private String key;
    private long startTs;
    private long endTs;

    public Period(String key, long startTs, long endTs) {
        super();
        this.key = key;
        this.startTs = startTs;
        this.endTs = endTs;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getStartTs() {
        return startTs;
    }

    public void setStartTs(long startTs) {
        this.startTs = startTs;
    }

    public long getEndTs() {
        return endTs;
    }

    public void setEndTs(long endTs) {
        this.endTs = endTs;
    }
    
    @Override
    public String toString(){
        final StringBuffer sb = new StringBuffer();
        sb.append("<");
        sb.append(key);
        sb.append(">,\t");
        sb.append(endTs-startTs);
        sb.append(",\t");
        sb.append(startTs);
        sb.append(",\t");
        sb.append(endTs);
        return sb.toString();
    }

}
