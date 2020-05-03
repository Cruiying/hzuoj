package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.problem.ProblemLevel;
import com.hqz.hzuoj.bean.problem.ProblemQuery;
import com.hqz.hzuoj.bean.problem.Tag;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 13:53
 * @Description: TODO
 */
public interface ProblemService {
    /**
     * 保存题目
     *
     * @param problem
     * @return
     */
    Integer saveProblem(Problem problem);

    /**
     * 保存题目上传的数据
     *
     * @param problemId
     * @param path
     */
    String updateProblemData(Integer problemId, String path);

    /**
     * 返回所有问题信息 [管理员方法]
     *
     * @return
     */
    PageInfo<Problem> getAllProblem(Integer page);

    /**
     * 分页查询，放回所有比赛列表
     *
     * @param page
     * @param userId
     * @return
     */
    PageInfo<Problem> getAllProblem(Integer page, Integer userId);

    /**
     * 根据问题problemId返回问题信息
     *
     * @param problemId
     * @return
     */
    Problem getProblem(Integer problemId);

    /**
     * 更新问题用户是否可见性
     *
     * @param problem
     */
    void updateProblemPublic(Problem problem);

    /**
     * 删除问题
     *
     * @param problemId
     */
    String deleteProblem(Integer problemId);

    /**
     * 保存[保存]标签
     *
     * @param tag
     * @return
     */
    Tag saveTag(Tag tag);

    /**
     * 获取标签
     *
     * @param tagId
     * @return
     */
    Tag getTag(Integer tagId);

    /**
     * 获取标签列表
     *
     * @param page
     * @return
     */
    PageInfo<Tag> getTags(Integer page);

    /**
     * 删除标签
     *
     * @param tagId
     * @return
     */
    boolean deleteTag(Integer tagId);

    /**
     * 获取所有标签
     * @return
     */
    List<Tag> getAllTags();

    /**
     * 获取所有题目难题等级
     * @return
     */
    List<ProblemLevel> getProblemLevels();

    PageInfo<ProblemLevel> getProblemLevels(Integer page);

    boolean deleteProblemLevel(Integer ProblemLevelId);

    ProblemLevel saveProblemLevel(ProblemLevel problemLevel);

    ProblemLevel getProblemLevel(Integer ProblemLevelId);

    PageInfo<Problem> getProblemQuery(Integer page, Integer userId, ProblemQuery problemQuery);
}
