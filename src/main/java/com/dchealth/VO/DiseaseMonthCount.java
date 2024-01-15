package com.dchealth.VO;

/**
 * Created by Administrator on 2017/12/1.
 */
public class DiseaseMonthCount {
    private String name;
    private String dcode;
    private long totalCount;
    private long nowMonthCount;
    private long lastMonthCount;
    private long sumTotalCount;

    public DiseaseMonthCount() {
    }

    public DiseaseMonthCount(String name, String dcode, Long totalCount, Long nowMonthCount, Long lastMonthCount, Long sumTotalCount) {
        this.name = name;
        this.dcode = dcode;
        this.totalCount = (totalCount==null?0:totalCount);
        this.nowMonthCount = (nowMonthCount==null?0:nowMonthCount);
        this.lastMonthCount = (lastMonthCount==null?0:lastMonthCount);
        this.sumTotalCount = (sumTotalCount==null?0:sumTotalCount);
    }

    public DiseaseMonthCount(String name, String dcode, Long totalCount, Long nowMonthCount, Long lastMonthCount) {
        this.name = name;
        this.dcode = dcode;
        this.totalCount = (totalCount==null?0:totalCount);
        this.nowMonthCount = (nowMonthCount==null?0:nowMonthCount);
        this.lastMonthCount = (lastMonthCount==null?0:lastMonthCount);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDcode() {
        return dcode;
    }

    public void setDcode(String dcode) {
        this.dcode = dcode;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getNowMonthCount() {
        return nowMonthCount;
    }

    public void setNowMonthCount(long nowMonthCount) {
        this.nowMonthCount = nowMonthCount;
    }

    public long getLastMonthCount() {
        return lastMonthCount;
    }

    public void setLastMonthCount(long lastMonthCount) {
        this.lastMonthCount = lastMonthCount;
    }

    public long getSumTotalCount() {
        return sumTotalCount;
    }

    public void setSumTotalCount(long sumTotalCount) {
        this.sumTotalCount = sumTotalCount;
    }
}
