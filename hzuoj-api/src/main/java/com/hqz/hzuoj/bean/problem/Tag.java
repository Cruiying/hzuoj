package com.hqz.hzuoj.bean.problem;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 标签实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 13:49
 * @Description: TODO
 */
@Table(name = "hzuoj_tags")
public class Tag  implements Serializable {
    //标签主键
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer tagId;
    //标签名称
    @Column
    private String tagName;


    /**
     * 获取标签主键
     * @return
     */
    public Integer getTagId() {
        return tagId;
    }

    /**
     * 设置标签主键
     * @param tagId
     */
    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    /**
     * 获取标签名称
     * @return
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 设置标签名称
     * @param tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
