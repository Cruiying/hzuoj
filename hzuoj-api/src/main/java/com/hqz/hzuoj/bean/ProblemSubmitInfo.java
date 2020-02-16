package com.hqz.hzuoj.bean;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/13 20:22
 * @Description: TODO
 */
public class ProblemSubmitInfo implements Serializable {
    //提交通过人数
    private Integer allAcceptedTotal;
    //总提交人数
    private Integer allTotal;
    //我的提交通过总数
    private Integer myAcceptedTotal;
    //我的提交总数
    private Integer myAllTotal;
    //
    private boolean showSubmit = false;

    /**
     * 获取所有提交通过总数
     * @return
     */
    public Integer getAllAcceptedTotal() {
        return allAcceptedTotal;
    }

    /**
     * 设置所有提交通过总数
     * @param allAcceptedTotal
     */
    public void setAllAcceptedTotal(Integer allAcceptedTotal) {
        this.allAcceptedTotal = allAcceptedTotal;
    }

    /**
     * 获取所有提交总数
     * @return
     */
    public Integer getAllTotal() {
        return allTotal;
    }

    /**
     * 设置所有提交总数
     * @param allTotal
     */
    public void setAllTotal(Integer allTotal) {
        this.allTotal = allTotal;
    }

    /**
     * 获取我的提交通过总数
     * @return
     */
    public Integer getMyAcceptedTotal() {
        return myAcceptedTotal;
    }

    /**
     * 设置我的提交通过总数
     * @param myAcceptedTotal
     */
    public void setMyAcceptedTotal(Integer myAcceptedTotal) {
        this.myAcceptedTotal = myAcceptedTotal;
    }

    /**
     * 获取我的提交总数
     * @return
     */
    public Integer getMyAllTotal() {
        return myAllTotal;
    }

    /**
     * 设置投的提交总数
     * @param myAllTotal
     */
    public void setMyAllTotal(Integer myAllTotal) {
        this.myAllTotal = myAllTotal;
    }

    /**
     * 获取是否显示我的提交
     * @return
     */
    public boolean isShowSubmit() {
        return showSubmit;
    }



    /**
     * 设置是否显示我的提交
     * @param showSubmit
     */
    public void setShowSubmit(boolean showSubmit) {
        this.showSubmit = showSubmit;
    }

    @Override
    public String toString() {
        return "ProblemSubmitInfo{" +
                "allAcceptedTotal=" + allAcceptedTotal +
                ", allTotal=" + allTotal +
                ", myAcceptedTotal=" + myAcceptedTotal +
                ", myAllTotal=" + myAllTotal +
                ", showSubmit=" + showSubmit +
                '}';
    }
}
