package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.contest.*;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.TestPoint;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.mapper.contest.*;
import com.hqz.hzuoj.mapper.language.LanguageMapper;
import com.hqz.hzuoj.mapper.result.JudgeResultMapper;
import com.hqz.hzuoj.mapper.submit.SubmitMapper;
import com.hqz.hzuoj.mapper.submit.TestPointMapper;
import com.hqz.hzuoj.mapper.user.UserMapper;
import com.hqz.hzuoj.service.SubmitService;
import com.hqz.hzuoj.service.mq.MessageSender;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:24
 * @Description: TODO
 */
@SuppressWarnings("AlibabaAvoidNewDateGetTime")
@Service
@Transactional
public class SubmitServiceImpl implements SubmitService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TestPointMapper testPointMapper;

    @Autowired
    private SubmitMapper submitMapper;

    @Autowired
    private LanguageMapper languageMapper;

    @Autowired
    private JudgeResultMapper judgeResultMapper;

    @Autowired
    private ContestSubmitMapper contestSubmitMapper;

    @Autowired
    private ContestApplyMapper contestApplyMapper;

    @Autowired
    private ContestUserRatingMapper contestUserRatingMapper;

    @Autowired
    private ContestRankMapper contestRankMapper;

    @Autowired
    private ContestRankInfoMapper contestRankInfoMapper;

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private MessageSender sender;

    @Value("${submitQueue}")
    private String submitQueue;

    //是否获得更新比赛排名标志
    private volatile boolean isRank = false;
    //是否更新完成
    private volatile boolean isFinish = false;

    /**
     * 每3小时更新一次提交，更新时间为100天内的测评记录
     */
    @Scheduled(cron = "0 0 0/3 * * ?")
    public void timerToNow() throws InterruptedException {
        Date date = new Date();
        long time = date.getTime();
        date.setTime(time - (long) 1000 * 3600 * 24 * 100);
        List<Submit> submits = submitMapper.getCheckSubmits(date);
        if (submits != null) {
            for (Submit submit : submits) {
                if ("PD".equals(submit.getJudgeResult().getJudgeName())
                        || "queue".equals(submit.getJudgeResult().getJudgeName())
                        || "Running".equals(submit.getJudgeResult().getJudgeName())
                        || "SE".equals(submit.getJudgeResult().getJudgeName())) {
                    startSubmit(submit);
                }
            }
        }
    }

    @Override
    public String restartSubmit(Integer submitId) {
        Submit submit = submitMapper.getSubmit(submitId);
        startSubmit(submit);
        return "success";
    }

    public void startSubmit(Submit submit) {
        if (submit == null) {
            return;
        }
        redisUtil.del("submit:" + submit.getSubmitId() + ":info");
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName("queue");
        if (judgeResult == null) {
            return;
        }
        submit.setJudgeResult(judgeResult);
        submit.setSubmitScore(0);
        submit.setSubmitRuntimeTime(0);
        submit.setSubmitRuntimeMemory(0);
        testPointMapper.deleteSubmitTestPoint(submit.getSubmitId());
        submitMapper.restartSubmit(submit);
        Map<String, Object> map = new HashMap<>();
        map.put("submitId", submit.getSubmitId());
        sender.sendQueue(submitQueue, map);
    }

    @Override
    public String restartSubmits() {
        try {
            timerToNow();
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * 获取提交信息
     *
     * @param submitId
     * @return
     */
    @Override
    public Submit getSubmit(Integer submitId) {
        Submit submit;
        String key = "submit:" + submitId + ":info";
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断redis中是否存在
        if (StringUtils.isNotBlank(json)) {
            //如果redis中存在，直接将json转换为Submit对象
            submit = JSON.parseObject(json, Submit.class);
        } else {
            //如果redis中没有数据，查询数据库
            submit = submitMapper.getSubmit(submitId);
            if (submit != null) {
                List<TestPoint> testPoints = testPointMapper.getTestPoint(submitId);
                submit.setTestPoints(testPoints);
                String judgeName = submit.getJudgeResult().getJudgeName();
                if (!"PD".equals(judgeName) && !"queue".equals(judgeName) && !"Running".equals(judgeName) && !"compile".equals(judgeName)) {
                    int time = (int) (Math.random() * 60 * 60 * 24 + 10);
                    //将数据写入redis中，避免缓存击穿
                    redisUtil.set(key, JSON.toJSONString(submit), time);
                }
            }
        }
        return submit;
    }

    /**
     * 保存提交信息
     *
     * @param submit
     * @return
     */
    @Override
    public Submit saveSubmit(Submit submit) {
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName("queue");
        if (judgeResult == null) {
            return null;
        }
        submit.setSubmitScore(0);
        submit.setJudgeResult(judgeResult);
        Language language = languageMapper.selectByPrimaryKey(submit.getLanguage().getLanguageId());
        submit.setLanguage(language);
        User user = userMapper.getUser(submit.getUser().getUserId());
        SubmitQuery submitQuery = new SubmitQuery();
        submitQuery.setUserId(user.getUserId());
        submitQuery.setProblemId(submit.getProblem().getProblemId());
        PageHelper.startPage(1, 1, false);
        List<Submit> submits = submitMapper.getSubmits(submitQuery);
        user.setUserSubmitTotal(user.getUserSubmitTotal() + 1);
        if (submits == null || submits.size() <= 0) {
            user.setUserChallengeTotal(user.getUserChallengeTotal() + 1);
        }
        userMapper.updateByPrimaryKey(user);
        //保存提交信息
        submitMapper.saveSubmit(submit);
        return submit;
    }


    /**
     * 获取提交列表
     *
     * @param page
     * @param submitQuery
     * @return
     */
    @Override
    public PageInfo<Submit> getSubmits(Integer page, SubmitQuery submitQuery) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (submitQuery == null) {
            submitQuery = new SubmitQuery();
        }
        int size = 20;
        PageHelper.startPage(page, size, true);
        submitQuery.setStart((page - 1) * size);
        submitQuery.setSize(size);
        List<Integer> submitIds = submitMapper.getSubmitIds(submitQuery);
        PageInfo<Integer> p = new PageInfo<>(submitIds, size);
        List<Integer> list = new ArrayList<>();
        for (Integer submitId : submitIds) {
            list.add(submitId);
        }
        List<Submit> submits = submitMapper.getSubmitsBySubmitId(list);
        PageInfo<Submit> pageInfo = new PageInfo<>(submits, size);
        pageInfo.setPages(p.getPages());
        pageInfo.setTotal(p.getTotal());
        pageInfo.setPageNum(p.getPageNum());
        pageInfo.setSize(p.getSize());
        return pageInfo;
    }

    /**
     * 获取测评记录列表[管理员方法]
     *
     * @param page
     * @return
     */
    @Override
    public PageInfo<Submit> getSubmits(Integer page) {
        if (page == null || page <= 0) {
            page = 1;
        }
        int size = 20;
        return getSubmits(page, new SubmitQuery());
    }

    /**
     * 保存自测信息
     *
     * @param testCode
     * @return
     */
    @Override
    public TestCode saveTestCode(TestCode testCode) {
        testCode.setTestId(new Date().getTime());
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setJudgeName("queue");
        testCode.setJudgeResult(judgeResult);
        Language language = languageMapper.selectByPrimaryKey(testCode.getLanguage());
        testCode.setLanguage(language);
        return testCode;
    }

    /**
     * 比赛提交
     *
     * @param contestSubmit
     * @return
     */
    @Override
    public ContestSubmit saveContestSubmit(ContestSubmit contestSubmit) {
        Submit saveSubmit = saveSubmit(contestSubmit.getSubmit());
        if (saveSubmit == null) {
            return null;
        }
        contestSubmit.setSubmit(saveSubmit);
        contestSubmitMapper.saveContestSubmit(contestSubmit);
        return contestSubmit;
    }

    /**
     * 获取比赛测评记录
     *
     * @param contestSubmit
     * @return
     */
    @Override
    public ContestSubmit getContestSubmit(ContestSubmit contestSubmit) {
        ContestSubmit saveContestSubmit = contestSubmitMapper.getContestSubmit(contestSubmit);
        if (saveContestSubmit == null) {
            return null;
        }
        Contest contest = saveContestSubmit.getContest();
        if (contest.getContestStatus() == 1) {
            saveContestSubmit.getSubmit().setSubmitRuntimeMemory(0);
            saveContestSubmit.getSubmit().setSubmitRuntimeTime(0);
            saveContestSubmit.getSubmit().setSubmitScore(0);
            saveContestSubmit.getSubmit().setSubmitCompileInfo("");
            saveContestSubmit.getSubmit().setSubmitCodeLength(0);
            saveContestSubmit.getSubmit().getJudgeResult().setJudgeName("已提交");
        } else {
            List<TestPoint> testPoints = testPointMapper.getTestPoint(saveContestSubmit.getSubmit().getSubmitId());
            saveContestSubmit.getSubmit().setTestPoints(testPoints);
        }
        return saveContestSubmit;
    }

    /**
     * 返回比赛测评记录列表
     *
     * @param page
     * @param contestId
     * @return
     */
    @Override
    public PageInfo<ContestSubmit> getContestSubmits(Integer page, Integer contestId, SubmitQuery submitQuery) {
        if (submitQuery == null) {
            submitQuery = new SubmitQuery();
        }
        PageHelper.startPage(page, 20, true);
        List<ContestSubmit> contestSubmits = contestSubmitMapper.getContestSubmits(contestId, submitQuery);
        contestMapper.updateAllContestStatus(new Date());
        Contest contest = contestMapper.getContest(contestId);
        if (contestSubmits != null) {
            for (ContestSubmit contestSubmit : contestSubmits) {
                contestSubmit.getSubmit().setSubmitRuntimeMemory(0);
                contestSubmit.getSubmit().setSubmitRuntimeTime(0);
                contestSubmit.getSubmit().setSubmitScore(0);
                contestSubmit.getSubmit().setSubmitCodeLength(0);
                contestSubmit.getSubmit().setSubmitCodeLength(0);
                contestSubmit.getSubmit().setSubmitCompileInfo("");
                contestSubmit.getSubmit().setSubmitCode("");
                if (contest != null && contest.getContestStatus() == 1 && "OI".equals(contest.getContestType().getContestTypeName())) {
                    contestSubmit.getSubmit().getJudgeResult().setJudgeName("已提交");
                }
            }
        }
        return new PageInfo<>(contestSubmits, 20);
    }

    /**
     * 获取比赛排名信息
     *
     * @param page
     * @param contest
     * @return
     */
    @Override
    public PageInfo<ContestRank> getContestRanks(Integer page, Contest contest, ContestRankQuery contestRankQuery) {
        if (contest == null) {
            return null;
        }
        Integer contestId = contest.getContestId();
        if (isRank) {
            //没有获得更新权利
            if (isFinish) {
                //更新完成
                return getContestRank(page, contestId, contestRankQuery);
            } else {
                //没有更新完成
                return null;
            }
        }
        synchronized (this) {
            isRank = true;
            //获得更新权利
            try {
                isFinish = false;
                updateContestRank(contestId);
                isFinish = true;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRank = false;
            }
            PageInfo<ContestRank> contestRank = getContestRank(page, contestId, contestRankQuery);
            List<ContestRank> list = contestRank.getList();
            if (null != list && !list.isEmpty()) {
                for (ContestRank rank : list) {
                    ContestApply contestApply = rank.getContestApply();
                    ContestUserRating contestUserRating = contestMapper.getContestUserRating(contestApply);
                    rank.setContestUserRating(contestUserRating);
                }
            }
            return contestRank;
        }
    }

    /**
     * 获取比赛排名 [分页]
     *
     * @param page
     * @param contestId
     * @return
     */
    private PageInfo<ContestRank> getContestRank(Integer page, Integer contestId, ContestRankQuery contestRankQuery) {
        int size = 20;
        if (page == null || page <= 0) {
            page = 1;
        }
        if (contestRankQuery == null) {
            contestRankQuery = new ContestRankQuery();
        }
        PageHelper.startPage(page, size, true);
        List<ContestRank> contestRanks = contestRankMapper.getContestRanks(contestId, contestRankQuery);
        if (contestRanks != null) {
            for (ContestRank contestRank : contestRanks) {
                List<ContestRankInfo> contestRankInfos = contestRankInfoMapper.getContestRankInfos(contestRank.getContestRankId());
                contestRank.setContestRankInfos(contestRankInfos);

                ContestUserRating contestUserRating = contestMapper.getContestUserRating(contestRank.getContestApply());
                contestRank.setContestUserRating(contestUserRating);
            }
        }
        return new PageInfo<>(contestRanks, size);
    }

    /**
     * 更新比赛排名信息
     *
     * @param contestId
     */
    private void updateContestRank(Integer contestId) {
        //当前榜单
        List<ContestRank> contestRanks = getContestRanks(contestId);
        //数据库榜单
        List<ContestRank> contestRankList = contestRankMapper.getContestRanksAndContestRankInfos(contestId);

        Map<Integer, ContestRank> nowContestRankMap = getContestRankMap(contestRanks);

        Map<Integer, ContestRank> oldContestRankMap = getContestRankMap(contestRankList);
        //遍历新榜单
        for (Map.Entry<Integer, ContestRank> map : nowContestRankMap.entrySet()) {
            Integer contestApplyId = map.getKey();
            ContestRank nowContestRank = map.getValue();
            ContestRank oldContestRank = oldContestRankMap.get(contestApplyId);
            if (oldContestRank != null) {
                //已经存在该记录
                //判断数据库记录和当前记录是否相等
                if (!compare(nowContestRank, oldContestRank)) {
                    //两个比赛排名比赛相等
                    //更新数据库记录
                    nowContestRank.setContestRankId(oldContestRank.getContestRankId());
                    nowContestRank.setContestId(contestId);
                    contestRankMapper.updateByPrimaryKey(nowContestRank);
                    for (int i = 0; i < oldContestRank.getContestRankInfos().size(); i++) {
                        ContestRankInfo nowContestRankInfo = nowContestRank.getContestRankInfos().get(i);
                        ContestRankInfo oldContestRankInfo = oldContestRank.getContestRankInfos().get(i);
                        nowContestRankInfo.setContestRankInfoId(oldContestRankInfo.getContestRankInfoId());
                        nowContestRankInfo.setContestRankId(nowContestRank.getContestRankId());
                        contestRankInfoMapper.updateByPrimaryKey(nowContestRankInfo);
                    }
                }
            } else {
                //不存在该记录
                //插入数据库
                nowContestRank.setContestId(contestId);
                contestRankMapper.saveContestRank(nowContestRank);
                for (ContestRankInfo contestRankInfo : nowContestRank.getContestRankInfos()) {
                    contestRankInfo.setContestRankId(nowContestRank.getContestRankId());
                    contestRankInfoMapper.saveContestRankInfo(contestRankInfo);
                }
            }
        }
        //遍历旧榜单
        for (Map.Entry<Integer, ContestRank> map : oldContestRankMap.entrySet()) {
            Integer contestApplyId = map.getKey();
            ContestRank oldContestRank = map.getValue();
            ContestRank nowContestRank = nowContestRankMap.get(contestApplyId);
            if (nowContestRank == null) {
                //新榜单不存在该记录，旧榜单有该记录，删除旧记录
                contestRankInfoMapper.deleteContestRankInfos(oldContestRank.getContestRankId());
                contestRankMapper.deleteContestRank(oldContestRank.getContestRankId());
            }
        }
    }

    /**
     * 判断两个排名是否想等
     *
     * @param nowContestRank
     * @param oldContestRank
     * @return
     */
    private boolean compare(ContestRank nowContestRank, ContestRank oldContestRank) {
        //判断排名是否相等
        if (!nowContestRank.getRank().equals(oldContestRank.getRank())) {
            return false;
        }
        //判断通过数量是否相等
        if (!nowContestRank.getAcceptedTotal().equals(oldContestRank.getAcceptedTotal())) {
            return false;
        }
        //判断总分是否相等
        if (!nowContestRank.getTotalScore().equals(oldContestRank.getTotalScore())) {
            return false;
        }
        //判断总罚时是否相等
        if (!nowContestRank.getPunishTime().equals(oldContestRank.getPunishTime())) {
            return false;
        }
        //判断比赛详细信息是否相等
        if (nowContestRank.getContestRankInfos().size() != oldContestRank.getContestRankInfos().size()) {
            return false;
        }
        for (int i = 0; i < nowContestRank.getContestRankInfos().size(); i++) {
            ContestRankInfo nowContestRankInfo = nowContestRank.getContestRankInfos().get(i);
            for (int j = 0; j < oldContestRank.getContestRankInfos().size(); j++) {
                ContestRankInfo oldContestRankInfo = oldContestRank.getContestRankInfos().get(i);
                if (!nowContestRankInfo.getProblemId().equals(oldContestRankInfo.getProblemId())) {
                    continue;
                }
                //判断题目是否通过
                if (!nowContestRankInfo.isAccepted().equals(oldContestRankInfo.isAccepted())) {
                    return false;
                }
                //判断是否一血

                if (!nowContestRankInfo.isFirstAccepted().equals(oldContestRankInfo.isFirstAccepted())) {
                    return false;
                }
                //判断提交数量是否相同

                if (!nowContestRankInfo.getSubmitTotal().equals(oldContestRankInfo.getSubmitTotal())) {
                    return false;
                }
                //判断罚时是否相等

                if (!nowContestRankInfo.getPunishTime().equals(oldContestRankInfo.getPunishTime())) {
                    return false;
                }
                //判断分数是否相等
                if (!nowContestRankInfo.getScore().equals(oldContestRankInfo.getScore())) {
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * 将比赛排名列表，转map<key,value> 键为比赛报名Id，值为比赛排名信息
     *
     * @param contestRanks
     * @return
     */
    private Map<Integer, ContestRank> getContestRankMap(List<ContestRank> contestRanks) {
        Map<Integer, ContestRank> contestRankMap = new HashMap<>();
        if (contestRanks == null) {
            return contestRankMap;
        }
        for (ContestRank contestRank : contestRanks) {
            contestRankMap.put(contestRank.getContestApply().getContestApplyId(), contestRank);
        }
        return contestRankMap;
    }

    /**
     * 获取比赛全部排名信息
     *
     * @param contestId
     * @return
     */
    @Override
    public List<ContestRank> getContestRanks(Integer contestId) {
        List<ContestApply> contestApplyList = contestApplyMapper.selectAllByContest(contestId);
        List<ContestProblem> contestProblems = contestSubmitMapper.getContestProblemAll(contestId);
        List<ContestSubmit> contestSubmits = contestSubmitMapper.getRankContestSubmits(contestId);
        if (contestSubmits == null || contestApplyList == null || contestProblems == null) {
            return null;
        }
        if (contestSubmits.size() <= 0) {
            return null;
        }
        Contest contest = contestSubmits.get(0).getContest();
        List<ContestRank> contestRanks = getContestRanks(contestApplyList, contestProblems, contestSubmits, contest);
        if ("ACM".equals(contest.getContestType().getContestTypeName())) {
            contestRanks = getACMContestRank(contestRanks);
        } else if ("OI".equals(contest.getContestType().getContestTypeName())) {
            contestRanks = getOIContestRank(contestRanks);
        } else if ("IOI".equals(contest.getContestType().getContestTypeName())) {
            contestRanks = getIOIContestRank(contestRanks);
        } else if ("乐多".equals(contest.getContestType().getContestTypeName())) {
            contestRanks = getLeDouContestRank(contestRanks);
        }
        return contestRanks;
    }

    /**
     * 计算比赛Rating值[管理员方法]
     *
     * @param contestId
     */
    @Override
    public synchronized String calculateContestRating(Integer contestId) {
        Contest contest = contestMapper.getContest(contestId);
        if (contest == null || contest.getContestId() == null || contest.getContestStatus() != 2) {
            return "fail";
        }
        List<ContestRank> contestRanks = getContestRanks(contestId);
        if (contestRanks == null || contestRanks.size() <= 1) {
            return "fail";
        }
        contest.setContestRankIsFinish(false);
        this.contestMapper.updateContestRankIsFinish(contest);
        List<ContestUserRating> contestUserRatings = contestUserRatingMapper.getContestUserRatings(contestId);
        contestUserRatingMapper.updateContestUserRatingStatus(contestId, 1);
        if (contestUserRatings != null) {
            for (ContestUserRating contestUserRating : contestUserRatings) {
                contestUserRatingMapper.updateUserRating(contestUserRating.getUserId(), -1 * contestUserRating.getRank());
                contestUserRatingMapper.deleteContestUserRating(contestUserRating.getId());
            }
        }
        List<ContestUserRating> contestUserRatingList = new ArrayList<>();
        for (int i = 0; i < contestRanks.size(); i++) {
            double s = 0.0;
            double e = 0.0;
            for (int j = 0; j < contestRanks.size(); j++) {
                if (i != j) {
                    int ra = contestRanks.get(i).getRank();
                    int rb = contestRanks.get(j).getRank();
                    if (ra < rb) {
                        s += 1.0;
                    }
                    if (ra == rb) {
                        s += 0.5;
                    }
                    int ar = contestRanks.get(i).getContestApply().getUser().getUserRating();
                    int br = contestRanks.get(j).getContestApply().getUser().getUserRating();
                    e += 1.0 / (1.0 + Math.pow(10.0, (1.0 * (br - ar) / 400.0)));
                }
            }
            ContestUserRating contestUserRating = new ContestUserRating();
            contestUserRating.setContestId(contestId);
            contestUserRating.setUserId(contestRanks.get(i).getContestApply().getUser().getUserId());
            int r = (int) (32.0 * (s - e));
            contestUserRating.setRank(r);
            contestUserRatingList.add(contestUserRating);
        }
        for (ContestUserRating contestUserRating : contestUserRatingList) {
            contestUserRatingMapper.updateUserRating(contestUserRating.getUserId(), contestUserRating.getRank());
            contestUserRatingMapper.saveContestUserRating(contestUserRating);
        }
        contestUserRatingMapper.updateContestUserRatingStatus(contestId, 2);
        this.contestMapper.updateContestRankIsFinish(contest);
        return "success";
    }

    /**
     * 获取未排序的比赛榜单
     *
     * @param contestApplyList
     * @param contestProblems
     * @param contestSubmits
     * @param contest
     * @return
     */
    private List<ContestRank> getContestRanks(List<ContestApply> contestApplyList, List<ContestProblem> contestProblems, List<ContestSubmit> contestSubmits, Contest contest) {
        List<ContestRank> contestRanks = new ArrayList<>();
        boolean isACM = "ACM".equals(contest.getContestType().getContestTypeName());
        boolean isLeDuo = "乐多".equals(contest.getContestType().getContestTypeName());
        boolean isCF = "CF".equals(contest.getContestType().getContestTypeName());
        boolean isIOI = "IOI".equals(contest.getContestType().getContestTypeName());
        boolean isOI = "OI".equals(contest.getContestType().getContestTypeName());
        Map<Integer, Boolean> firstAcceptedMap = new HashMap<>();
        for (ContestProblem contestProblem : contestProblems) {
            firstAcceptedMap.put(contestProblem.getProblem().getProblemId(), true);
        }
        Map<Integer, ContestRank> rankMap = new HashMap<>();
        Map<Integer, ContestApply> contestApplyMap = new HashMap<>();
        //初始化
        for (ContestApply contestApply : contestApplyList) {
            contestApplyMap.put(contestApply.getUserId(), contestApply);
        }
        //遍历提交
        for (ContestSubmit contestSubmit : contestSubmits) {
            Submit submit = contestSubmit.getSubmit();
            int s = submit.getProblem().getProblemId();
            if ("SE".equals(submit.getJudgeResult().getJudgeName()) || "CE".equals(submit.getJudgeResult().getJudgeName())) {
                continue;
            }
            boolean accepted = "AC".equals(submit.getJudgeResult().getJudgeName());
            ContestRank contestRank = rankMap.get(submit.getUser().getUserId());
            if (contestRank == null) {
                contestRank = new ContestRank();
                contestRank.setContestApply(contestApplyMap.get(submit.getUser().getUserId()));
                List<ContestRankInfo> contestRankInfos = new ArrayList<>();
                //遍历题目
                for (ContestProblem contestProblem : contestProblems) {
                    ContestRankInfo contestRankInfo = new ContestRankInfo();
                    contestRankInfo.setContestProblemScore(contestProblem.getContestProblemScore());
                    contestRankInfo.setProblemId(contestProblem.getProblem().getProblemId());
                    contestRankInfos.add(contestRankInfo);
                }
                contestRank.setContestRankInfos(contestRankInfos);
                rankMap.put(submit.getUser().getUserId(), contestRank);
            }
            List<ContestRankInfo> contestRankInfos = contestRank.getContestRankInfos();
            //遍历
            for (ContestRankInfo contestRankInfo : contestRankInfos) {
                int p = contestRankInfo.getProblemId();
                if (p == s) {
                    if (isACM) {
                        calculateACMRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
                    } else if (isCF) {
                        calculateCFRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
                    } else if (isIOI) {
                        calculateIOIRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
                    } else if (isOI) {
                        calculateOIRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
                    } else if (isLeDuo) {
                        calculateLeDuoRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
                    } else {
                        return null;
                    }
                }
            }
            contestRank.setContestRankInfos(contestRankInfos);
            rankMap.put(submit.getUser().getUserId(), contestRank);
        }
        for (Map.Entry<Integer, ContestRank> map : rankMap.entrySet()) {
            contestRanks.add(map.getValue());
        }
        //计算总分，总罚时，通过题目数量
        for (ContestRank contestRank : contestRanks) {
            for (ContestRankInfo contestRankInfo : contestRank.getContestRankInfos()) {
                contestRank.setTotalScore(contestRank.getTotalScore() + contestRankInfo.getScore());
                contestRank.setPunishTime(contestRank.getPunishTime() + contestRankInfo.getPunishTime());
                contestRank.setAcceptedTotal(contestRank.getAcceptedTotal() + (contestRankInfo.isAccepted() ? 1 : 0));
            }
        }

        return contestRanks;
    }

    /**
     * ACM计分规则
     *
     * @param contestRankInfo
     * @param contest
     * @param submit
     * @param firstAcceptedMap
     * @param accepted
     */
    private void calculateACMRankRule(ContestRankInfo contestRankInfo, Contest contest, Submit submit, Map<Integer, Boolean> firstAcceptedMap, boolean accepted) {
        // ACM赛制
        if (accepted) {
            //如果是正确，
            if (contestRankInfo.isAccepted()) {
                //如果该用户前面已经通过该题，不再增加罚时
                return;
            } else {
                //否则该用户是第一次通过该题目
                contestRankInfo.setAccepted(true);
                //增加罚时
                long punishTime = submit.getSubmitTime().getTime() - contest.getContestStart().getTime();
                contestRankInfo.setPunishTime(contestRankInfo.getSubmitTotal() * 20 * 60 * 1000 + punishTime);
                //判断是否是一血
                boolean firstAccepted = firstAcceptedMap.get(submit.getProblem().getProblemId());
                //增加提交次数
                contestRankInfo.setSubmitTotal(contestRankInfo.getSubmitTotal() + 1);
                if (firstAccepted) {
                    //如果该问题是该用户第一个通过
                    contestRankInfo.setFirstAccepted(true);
                    //将该问题设为false，表示该问题
                    firstAcceptedMap.put(submit.getProblem().getProblemId(), false);
                }
            }
        } else if (!contestRankInfo.isAccepted()) {
            //设置为未通过
            contestRankInfo.setAccepted(false);
            //增加错误提交次数
            contestRankInfo.setSubmitTotal(contestRankInfo.getSubmitTotal() + 1);
        }
    }

    /**
     * IOI计分规则
     *
     * @param contestRankInfo
     * @param contest
     * @param submit
     * @param firstAcceptedMap
     * @param accepted
     */
    private void calculateIOIRankRule(ContestRankInfo contestRankInfo, Contest contest, Submit submit, Map<Integer, Boolean> firstAcceptedMap, boolean accepted) {
        // IOI赛制
        if (accepted) {
            //用户正确通过该题目
            contestRankInfo.setAccepted(true);
            //增加罚时
            long punishTime = submit.getSubmitTime().getTime() - contest.getContestStart().getTime();
            contestRankInfo.setPunishTime(contestRankInfo.getSubmitTotal() * 20 * 60 * 1000 + punishTime);
            //增加提交次数
            contestRankInfo.setSubmitTotal(contestRankInfo.getSubmitTotal() + 1);
        } else {
            //用户没有通过该题目
            contestRankInfo.setAccepted(false);
            //增加错误提交次数
            contestRankInfo.setSubmitTotal(contestRankInfo.getSubmitTotal() + 1);
        }
        contestRankInfo.setScore(submit.getSubmitScore() * contestRankInfo.getContestProblemScore() / 100);
    }

    /**
     * OI计分规则
     *
     * @param contestRankInfo
     * @param contest
     * @param submit
     * @param firstAcceptedMap
     * @param accepted
     */
    private void calculateOIRankRule(ContestRankInfo contestRankInfo, Contest contest, Submit submit, Map<Integer, Boolean> firstAcceptedMap, boolean accepted) {
        //OI计分规则
        calculateIOIRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
        contestRankInfo.setScore(submit.getSubmitScore() * contestRankInfo.getContestProblemScore() / 100);

    }

    /**
     * 乐多计分规则
     *
     * @param contestRankInfo
     * @param contest
     * @param submit
     * @param firstAcceptedMap
     * @param accepted
     */
    private void calculateLeDuoRankRule(ContestRankInfo contestRankInfo, Contest contest, Submit submit, Map<Integer, Boolean> firstAcceptedMap, boolean accepted) {
        //乐多赛制计分规则
        calculateIOIRankRule(contestRankInfo, contest, submit, firstAcceptedMap, accepted);
        long up = 1;
        long dw = 1;
        for (int i = 1; i <= contestRankInfo.getSubmitTotal() - 1; i++) {
            up = up * 19;
            dw = dw * 20;
            if (up * 10 <= dw * 7) {
                dw = 10;
                up = 7;
                break;
            }
        }
        contestRankInfo.setScore((int) (submit.getSubmitScore() * contestRankInfo.getContestProblemScore() / 100 * up / dw));
    }

    /**
     * CF计分规则
     *
     * @param contestRankInfo
     * @param contest
     * @param submit
     * @param firstAcceptedMap
     * @param accepted
     */
    private void calculateCFRankRule(ContestRankInfo contestRankInfo, Contest contest, Submit submit, Map<Integer, Boolean> firstAcceptedMap, boolean accepted) {
    }

    /**
     * 根据ACM赛制规则进行排序
     *
     * @param contestRanks
     * @return
     */
    private List<ContestRank> getACMContestRank(List<ContestRank> contestRanks) {
        contestRanks.sort(new Comparator<ContestRank>() {
            @Override
            public int compare(ContestRank o1, ContestRank o2) {
                if (!o1.getAcceptedTotal().equals(o2.getAcceptedTotal())) {
                    return o2.getAcceptedTotal() - o1.getAcceptedTotal();
                } else {
                    long punishTime = o1.getPunishTime() - o2.getPunishTime();
                    if (punishTime > 0L) {
                        return 1;
                    }
                    if (punishTime < 0L) {
                        return -1;
                    }
                    return 0;
                }
            }
        });
        contestRanks.get(0).setRank(1);
        for (int i = 1; i < contestRanks.size(); i++) {
            ContestRank now = contestRanks.get(i);
            ContestRank prv = contestRanks.get(i - 1);
            if (!now.getAcceptedTotal().equals(prv.getAcceptedTotal()) || !now.getPunishTime().equals(prv.getPunishTime())) {
                now.setRank(prv.getRank() + 1);
            } else {
                now.setRank(prv.getRank());
            }
        }
        return contestRanks;
    }

    /**
     * 根据OI赛制规则进行排序
     *
     * @param contestRanks
     * @return
     */
    private List<ContestRank> getOIContestRank(List<ContestRank> contestRanks) {
        return getIOIContestRank(contestRanks);
    }

    /**
     * 根据乐多赛制规则进行排序
     *
     * @param contestRanks
     * @return
     */
    private List<ContestRank> getLeDouContestRank(List<ContestRank> contestRanks) {
        return getIOIContestRank(contestRanks);
    }

    /**
     * 根据IOI赛制规则进行排序
     *
     * @param contestRanks
     * @return
     */
    private List<ContestRank> getIOIContestRank(List<ContestRank> contestRanks) {
        contestRanks.sort(new Comparator<ContestRank>() {
            @Override
            public int compare(ContestRank o1, ContestRank o2) {
                return o2.getTotalScore() - o1.getTotalScore();
            }
        });
        contestRanks.get(0).setRank(1);
        for (int i = 1; i < contestRanks.size(); i++) {
            ContestRank now = contestRanks.get(i);
            ContestRank prv = contestRanks.get(i - 1);
            if (!now.getTotalScore().equals(prv.getTotalScore())) {
                now.setRank(prv.getRank() + 1);
            } else {
                now.setRank(prv.getRank());
            }
        }
        return contestRanks;
    }

    /**
     * 根据CF赛制规则进行排序
     *
     * @param contestRanks
     * @return
     */
    private List<ContestRank> getCFContestRank(List<ContestRank> contestRanks) {
        return null;
    }

    @Override
    public String getContestExcel(Integer contestId) throws IOException {
        Contest contest = contestMapper.getContest(contestId);
        List<ContestRank> contestRanks = getContestRanks(contestId);
        if (null == contestRanks) {
            throw new RuntimeException("比赛不存在");
        }
        String directory = "/hzuoj/contest/excel/";

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("排名");

        cell = row.createCell(1);
        cell.setCellValue("参赛者");

        cell = row.createCell(2);
        cell.setCellValue("学校");

        cell = row.createCell(3);
        cell.setCellValue("通过");

        if ("ACM".equals(contest.getContestType().getContestTypeName())) {
            cell = row.createCell(4);
            cell.setCellValue("罚时");
        } else {
            cell = row.createCell(4);
            cell.setCellValue("总分");
        }
        int i = 5;
        Integer count = 1;
        List<ContestRankInfo> contestRankInfos = contestRanks.get(0).getContestRankInfos();
        for (ContestRankInfo ContestRankInfo : contestRankInfos) {
            cell = row.createCell(i);
            cell.setCellValue("题目" + count);
            count++;
            i++;
        }
        for (int j = 0; j < contestRanks.size(); j++) {
            row = sheet.createRow(j + 1);
            int k = 0;
            cell = row.createCell(k);
            cell.setCellValue(contestRanks.get(j).getRank());
            k++;
            cell = row.createCell(k);
            cell.setCellValue(contestRanks.get(j).getContestApply().getUser().getUsername());
            k++;
            cell = row.createCell(k);
            cell.setCellValue(contestRanks.get(j).getContestApply().getUser().getSchool());
            k++;
            cell = row.createCell(k);
            cell.setCellValue(contestRanks.get(j).getAcceptedTotal());
            k++;
            if ("ACM".equals(contest.getContestType().getContestTypeName())) {
                cell = row.createCell(k);
                cell.setCellValue(getPunishTime(contestRanks.get(j).getPunishTime()));
                k++;
            } else {
                cell = row.createCell(k);
                cell.setCellValue(contestRanks.get(j).getTotalScore());
                k++;
            }
            contestRankInfos = contestRanks.get(j).getContestRankInfos();
            for (ContestRankInfo contestRankInfo : contestRankInfos) {
                cell = row.createCell(k);
                k++;
                if ("ACM".equals(contest.getContestType().getContestTypeName())) {
                    cell.setCellValue(getPunishTime(contestRankInfo.getPunishTime()));
                } else {
                    cell.setCellValue(contestRankInfo.getScore());

                }
            }

        }
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = directory + UUID.randomUUID() + ".xlsx";
        FileOutputStream os = new FileOutputStream(new File(fileName));
        try {
            wb.write(os);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            os.flush();
            os.close();
            wb.close();
        }
        return null;
    }

    private String getPunishTime(Long punishTime) {
        long days = Math.round(punishTime / (1000 * 60 * 60 * 24));
        long hours = Math.round(punishTime % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
        long minutes = Math.round((punishTime % (1000 * 60 * 60)) / (1000 * 60));
        long seconds = Math.round((punishTime % (1000 * 60))) / 1000;
        String ans = "";
        if (days > 0) {
            ans += days + ":";
        }
        if (hours < 10) {
            ans += "0";
        }
        ans += hours + ":";
        if (minutes < 10) {
            ans += "0";
        }
        ans += minutes + ":";
        if (seconds < 10) {
            ans += "0";
        }
        ans += seconds;
        return ans;

    }
}
