package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.problem.*;
import com.hqz.hzuoj.mapper.problem.*;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 题目服务
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/17 13:54
 * @Description: TODO
 */
@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private ExampleMapper exampleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ProblemLevelMapper problemLevelMapper;


    /**
     * 保存问题
     *
     * @param problem
     * @return
     */
    @Override
    public Integer saveProblem(Problem problem) {
        Integer problemId = null;
        if (problem.getProblemId() != null) {
            //更新问题
            Problem problem1 = problemMapper.selectOne(problem);
            problem.setProblemUpdateTime(new Date());
            problem.setProblemCreateTime(problem1.getProblemCreateTime());
            problemMapper.updateByPrimaryKey(problem);
            problemMapper.deleteProblemTags(problem1.getProblemId());
            problemMapper.updateProblemLevel(problem.getProblemId(), problem.getProblemLevel());
            List<Tag> tags = problem.getTags();
            if (tags != null) {
                for (int i = 0; i < tags.size(); i++) {
                    problemMapper.saveProblemTag(tags.get(i).getTagId(), problem1.getProblemId());
                }
            }
            return problem.getProblemId();
        }
        try {
            problem.setProblemUpdateTime(new Date());
            problem.setProblemCreateTime(new Date());
            problemMapper.saveProblem(problem);
            problemId = problem.getProblemId();
            List<Tag> tags = problem.getTags();
            if (tags != null) {
                for (int i = 0; i < tags.size(); i++) {
                    problemMapper.saveProblemTag(tags.get(i).getTagId(), problemId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return problemId;
    }

    /**
     * 更新问题中的测试数据地址
     *
     * @param problemId 更新的地址
     * @param up        新的测试数据地址
     * @return 返回数据库中的测试数据路径
     */
    @Override
    public String updateProblemData(Integer problemId, String up) {
        Problem p = new Problem();
        p.setProblemId(problemId);
        //获取数据库中的数据地址
        Problem problem = problemMapper.selectOne(p);
        String file = problem.getProblemDataAddress();
        problemMapper.updateProblemData(problemId, up);
        return file;
    }

    /**
     * 返回所有问题信息[普通用户方法]
     *
     * @param
     * @return
     */
    @Override
    public PageInfo<Problem> getAllProblem(Integer page, Integer userId) {
        if (page == null || page <= 0) page = 1;
        return getAllProblem(page, 1, userId, null);
    }

    /**
     * 返回所有问题信息 [管理员方法]
     *
     * @return
     */
    @Override
    public PageInfo<Problem> getAllProblem(Integer page) {
        if (page == null || page <= 0) page = 1;
        return getAllProblem(page, null, null, null);
    }


    /**
     * 根据问题problemId返回问题信息
     *
     * @param problemId
     * @return
     */
    @Override
    public Problem getProblem(Integer problemId) {
        Problem problem = new Problem();
        problem.setProblemId(problemId);
        problem = problemMapper.getProblem(problemId);
        List<Tag> tags = problemMapper.getProblemTags(problemId);
        problem.setTags(tags);
        return problem;
    }

    /**
     * 更新问题的可见性
     *
     * @param problem
     */
    @Override
    public void updateProblemPublic(Problem problem) {
        problemMapper.updateProblemPublic(problem);
    }

    /**
     * 删除问题
     *
     * @param problemId
     */
    @Override
    public String deleteProblem(Integer problemId) {
        try {
            dataMapper.deleteData(problemId);
            exampleMapper.deleteExamples(problemId);
            problemMapper.deleteByPrimaryKey(problemId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 保存[更新]标签
     *
     * @param tag
     * @return
     */
    @Override
    public Tag saveTag(Tag tag) {
        if (tag.getTagId() != null) {
            tagMapper.updateByPrimaryKey(tag);
        } else {
            tagMapper.insertSelective(tag);
        }
        return tag;
    }

    /**
     * 获取标签
     *
     * @param tagId
     * @return
     */
    @Override
    public Tag getTag(Integer tagId) {
        return tagMapper.selectByPrimaryKey(tagId);
    }

    /**
     * 获取标签列表[分页]
     *
     * @param page
     * @return
     */
    @Override
    public PageInfo<Tag> getTags(Integer page) {
        PageHelper.startPage(page, 10, true);
        List<Tag> tags = tagMapper.selectAll();
        return new PageInfo<>(tags, 10);
    }

    /**
     * 获取所有标签
     *
     * @return
     */
    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }

    /**
     * 删除标签
     *
     * @param tagId
     * @return
     */
    @Override
    public boolean deleteTag(Integer tagId) {
        try {
            tagMapper.deleteByPrimaryKey(tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存[更新]等级
     *
     * @param problemLevel
     * @return
     */
    @Override
    public ProblemLevel saveProblemLevel(ProblemLevel problemLevel) {
        if (problemLevel.getProblemLevelId() != null) {
            problemLevelMapper.updateByPrimaryKey(problemLevel);
        } else {
            problemLevelMapper.insertSelective(problemLevel);
        }
        return problemLevel;
    }

    /**
     * 获取标签
     *
     * @param ProblemLevelId
     * @return
     */
    @Override
    public ProblemLevel getProblemLevel(Integer ProblemLevelId) {
        return problemLevelMapper.selectByPrimaryKey(ProblemLevelId);
    }

    /**
     * 获取等级列表[分页]
     *
     * @param page
     * @return
     */
    @Override
    public PageInfo<ProblemLevel> getProblemLevels(Integer page) {
        if (page == null || page <= 0) page = 1;
        PageHelper.startPage(page, 10, true);
        List<ProblemLevel> problemLevels = problemLevelMapper.selectAll();
        return new PageInfo<>(problemLevels, 10);
    }

    /**
     * 删除等级
     *
     * @param ProblemLevelId
     * @return
     */
    @Override
    public boolean deleteProblemLevel(Integer ProblemLevelId) {
        try {
            problemLevelMapper.deleteByPrimaryKey(ProblemLevelId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取所有等级
     *
     * @return
     */
    @Override
    public List<ProblemLevel> getProblemLevels() {
        return problemLevelMapper.selectAll();
    }

    /**
     * 条件查询题目
     *
     * @param page
     * @param userId
     * @param problemQuery
     * @return
     */
    @Override
    public PageInfo<Problem> getProblemQuery(Integer page, Integer userId, ProblemQuery problemQuery) {
        if (page == null || page <= 0) page = 1;
        return getAllProblem(page, 1, userId, problemQuery);
    }

    /**
     * 获取题目列表
     *
     * @param page
     * @param problemPublic
     * @param userId
     * @param problemQuery
     * @return
     */
    private PageInfo<Problem> getAllProblem(Integer page, Integer problemPublic, Integer userId, ProblemQuery problemQuery) {
        if (problemQuery == null) problemQuery = new ProblemQuery();
        PageHelper.startPage(page, 20, true);
        List<Problem> allProblem = problemMapper.getAllProblem(problemPublic, problemQuery); //0 是私有题目，1是公开题目，null:是管理员查看所有题目
        for (Problem problem : allProblem) {
            Integer problemId = problem.getProblemId();
            ProblemSubmitInfo problemSubmitInfo = getProblemSubmitInfo(userId, problemId);
            List<Tag> tags = problemMapper.getProblemTags(problemId);
            problem.setTags(tags);
            problem.setProblemSubmitInfo(problemSubmitInfo);
        }
        return new PageInfo<>(allProblem, 20);
    }

    /**
     * 获取题目提交信息
     *
     * @param userId
     * @param problemId
     * @return
     */
    private ProblemSubmitInfo getProblemSubmitInfo(Integer userId, Integer problemId) {
        ProblemSubmitInfo problemSubmitInfo;
        String key = "user:" + userId + ":problemId:" + problemId + ":info";
        String json = null;
        try {
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            problemSubmitInfo = JSON.parseObject(json, ProblemSubmitInfo.class);
        } else {
            int allAcceptedTotal = problemMapper.getAllAcceptedTotal(problemId);
            int allTotal = problemMapper.getAllTotal(problemId);
            int myAcceptedTotal = problemMapper.getMyAcceptedTotal(userId, problemId);
            int myAllTotal = problemMapper.getMyAllTotal(userId, problemId);
            problemSubmitInfo = new ProblemSubmitInfo(allAcceptedTotal, allTotal, myAcceptedTotal, myAllTotal);
            int time = (int) (Math.random() * 60 * 60 + 60);
            redisUtil.set(key, JSON.toJSONString(problemSubmitInfo), time);
        }

        return problemSubmitInfo;
    }
}
