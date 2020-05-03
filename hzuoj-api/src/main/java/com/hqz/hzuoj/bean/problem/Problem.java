package com.hqz.hzuoj.bean.problem;

import com.hqz.hzuoj.bean.user.Admin;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 问题实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 13:30
 * @Description: TODO
 */
@Table(name = "hzuoj_problems")
public class Problem implements Serializable {
    //问题主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer problemId;
    //问题是否可见
    @Column
    private Integer problemPublic;
    //问题标题
    @Column
    private String problemTitle;
    //问题内容
    @Column
    private String problemContent;
    //问题说明
    @Column
    private String problemExplain;
    //问题输入说明
    private String problemInputFormat;
    //问题输出说明
    @Column
    private String problemOutputFormat;
    //问题背景
    @Column
    private String problemBackground;
    //问题数据地址
    @Column
    private String problemDataAddress;
    /**
     * 上传的测试数据版本
     */
    @Column
    private Integer problemDataVersions;
    /**
     * 题目创建时间
     */
    @Column
    private Date problemCreateTime;
    /**
     * 题目修改时间
     */
    @Column
    private Date problemUpdateTime;

    @Transient
    private ProblemSubmitInfo problemSubmitInfo;
    //问题创建者
    @Transient
    private Admin admin;
    //样例集合
    @Transient
    private List<Example> examples;
    //标签集合
    @Transient
    private List<Tag> tags;

    public Integer getProblemDataVersions() {
        return problemDataVersions;
    }

    public void setProblemDataVersions(Integer problemDataVersions) {
        this.problemDataVersions = problemDataVersions;
    }

    /**
     * 题目数据
     */
    @Transient
    private List<Data> datas;
    /**
     * 题目难题等级
     */
    @Transient
    private ProblemLevel problemLevel;

    /**
     * 获取问题主键
     *
     * @return
     */
    public Integer getProblemId() {
        return problemId;
    }

    /**
     * 设置问题主键
     *
     * @param problemId
     */
    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    /**
     * 获取问题可见状态
     *
     * @return
     */
    public Integer getProblemPublic() {
        return problemPublic;
    }

    /**
     * 设置问题可见状态
     *
     * @param problemPublic
     */
    public void setProblemPublic(Integer problemPublic) {
        this.problemPublic = problemPublic;
    }

    /**
     * 获取问题标题
     *
     * @return
     */
    public String getProblemTitle() {
        return problemTitle;
    }

    /**
     * 设置问题标题
     *
     * @param problemTitle
     */
    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    /**
     * 获取问题内容
     *
     * @return
     */
    public String getProblemContent() {
        return problemContent;
    }

    /**
     * 设置问题内容
     *
     * @param problemContent
     */
    public void setProblemContent(String problemContent) {
        this.problemContent = problemContent;
    }

    /**
     * 设置问题说明
     *
     * @return
     */
    public String getProblemExplain() {
        return problemExplain;
    }

    /**
     * 设置问题说明
     *
     * @param problemExplain
     */
    public void setProblemExplain(String problemExplain) {
        this.problemExplain = problemExplain;
    }

    /**
     * 获取问题输入格式
     *
     * @return
     */
    public String getProblemInputFormat() {
        return problemInputFormat;
    }

    /**
     * 设置问题输入格式
     *
     * @param problemInputFormat
     */
    public void setProblemInputFormat(String problemInputFormat) {
        this.problemInputFormat = problemInputFormat;
    }

    /**
     * 获取问题输出格式
     *
     * @return
     */
    public String getProblemOutputFormat() {
        return problemOutputFormat;
    }

    /**
     * 设置问题输出格式
     *
     * @param problemOutputFormat
     */
    public void setProblemOutputFormat(String problemOutputFormat) {
        this.problemOutputFormat = problemOutputFormat;
    }

    /**
     * 设置问题背景
     *
     * @return
     */
    public String getProblemBackground() {
        return problemBackground;
    }

    /**
     * 获取问题背景
     *
     * @param problemBackground
     */
    public void setProblemBackground(String problemBackground) {
        this.problemBackground = problemBackground;
    }

    /**
     * 获取问题数据输入地址
     *
     * @return
     */
    public String getProblemDataAddress() {
        return problemDataAddress;
    }

    /**
     * 设置问题数据输入地址
     *
     * @param problemDataAddress
     */
    public void setProblemDataAddress(String problemDataAddress) {
        this.problemDataAddress = problemDataAddress;
    }

    /**
     * 获取问题创建者
     *
     * @return
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * 设置问题创建者
     *
     * @param admin
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    /**
     * 获取样例集合
     *
     * @return
     */
    public List<Example> getExamples() {
        return examples;
    }

    /**
     * 设置样例集合
     *
     * @param examples
     */
    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    /**
     * 获取该问题的标签集合
     *
     * @return
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * 设置该问题的标签集合
     *
     * @param tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * 获取题目创建时间
     *
     * @return
     */
    public Date getProblemCreateTime() {
        return problemCreateTime;
    }

    /**
     * 设置题目创建时间
     *
     * @param problemCreateTime
     */
    public void setProblemCreateTime(Date problemCreateTime) {
        this.problemCreateTime = problemCreateTime;
    }

    /**
     * 获取题目修改时间
     *
     * @return
     */
    public Date getProblemUpdateTime() {
        return problemUpdateTime;
    }

    /**
     * 设置题目修改时间
     *
     * @param problemUpdateTime
     */
    public void setProblemUpdateTime(Date problemUpdateTime) {
        this.problemUpdateTime = problemUpdateTime;
    }

    /**
     * 获取题目的测试
     *
     * @return
     */
    public List<Data> getDatas() {
        return datas;
    }

    /**
     * 设置题目的测试数据
     *
     * @param datas
     */
    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }

    public ProblemSubmitInfo getProblemSubmitInfo() {
        return problemSubmitInfo;
    }

    public void setProblemSubmitInfo(ProblemSubmitInfo problemSubmitInfo) {
        this.problemSubmitInfo = problemSubmitInfo;
    }

    /**
     * 获取题目等级
     * @return
     */
    public ProblemLevel getProblemLevel() {
        return problemLevel;
    }

    /**
     * 设置题目等级
     * @param problemLevel
     */
    public void setProblemLevel(ProblemLevel problemLevel) {
        this.problemLevel = problemLevel;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "problemId=" + problemId +
                ", problemPublic=" + problemPublic +
                ", problemTitle='" + problemTitle + '\'' +
                ", problemContent='" + problemContent + '\'' +
                ", problemExplain='" + problemExplain + '\'' +
                ", problemInputFormat='" + problemInputFormat + '\'' +
                ", problemOutputFormat='" + problemOutputFormat + '\'' +
                ", problemBackground='" + problemBackground + '\'' +
                ", problemDataAddress='" + problemDataAddress + '\'' +
                ", problemDataVersions=" + problemDataVersions +
                ", problemCreateTime=" + problemCreateTime +
                ", problemUpdateTime=" + problemUpdateTime +
                ", problemSubmitInfo=" + problemSubmitInfo +
                ", admin=" + admin +
                ", examples=" + examples +
                ", tags=" + tags +
                ", datas=" + datas +
                ", problemLevel=" + problemLevel +
                '}';
    }
}
