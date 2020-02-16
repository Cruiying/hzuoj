package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 语言实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 18:12
 * @Description: TODO
 */
@Table(name = "hzuoj_languages")
public class Language implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer languageId;
    /**
     * 语言名称
     */
    @Column
    private String languageName;
    /**
     * 语言编译命令
     */
    @Column
    private String languageCompileCmd;
    /**
     * 编译器运行识别高亮
     */
    @Column
    private String languageModel;
    /**
     * 语言运行命令
     */
    @Column
    private String languageRuntimeCmd;
    /**
     * 语言保存文件名后缀
     */
    @Column
    private String languageSuffix;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getLanguageId() {
        return languageId;
    }

    /**
     * 设置主键
     *
     * @param languageId
     */
    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    /**
     * 获取语言名称
     *
     * @return
     */
    public String getLanguageName() {
        return languageName;
    }

    /**
     * 设置语言名称
     *
     * @param languageName
     */
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    /**
     * 获取语言对于的编译命令
     *
     * @return
     */
    public String getLanguageCompileCmd() {
        return languageCompileCmd;
    }

    /**
     * 设置语言对于的编译命令
     *
     * @param languageCompileCmd
     */
    public void setLanguageCompileCmd(String languageCompileCmd) {
        this.languageCompileCmd = languageCompileCmd;
    }

    /**
     * 获取语言对于的运行命令
     *
     * @return
     */
    public String getLanguageRuntimeCmd() {
        return languageRuntimeCmd;
    }

    /**
     * 设置语言对于的运行命令
     *
     * @param languageRuntimeCmd
     */
    public void setLanguageRuntimeCmd(String languageRuntimeCmd) {
        this.languageRuntimeCmd = languageRuntimeCmd;
    }

    /**
     * 获取编译器运行识别高亮
     *
     * @return
     */
    public String getLanguageModel() {
        return languageModel;
    }

    /**
     * 设置编译器运行识别高亮
     *
     * @param languageModel
     */
    public void setLanguageModel(String languageModel) {
        this.languageModel = languageModel;
    }

    /**
     * 获取语言文件名后缀
     *
     * @return
     */
    public String getLanguageSuffix() {
        return languageSuffix;
    }

    /**
     * 设置语言文件名后缀
     *
     * @param languageSuffix
     */

    public void setLanguageSuffix(String languageSuffix) {
        this.languageSuffix = languageSuffix;
    }

    @Override
    public String toString() {
        return "Language{" +
                "languageId=" + languageId +
                ", languageName='" + languageName + '\'' +
                ", languageCompileCmd='" + languageCompileCmd + '\'' +
                ", languageModel='" + languageModel + '\'' +
                ", languageRuntimeCmd='" + languageRuntimeCmd + '\'' +
                ", languageSuffix='" + languageSuffix + '\'' +
                '}';
    }
}
