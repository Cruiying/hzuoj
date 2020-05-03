package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.mapper.language.LanguageMapper;
import com.hqz.hzuoj.service.LanguageService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/10 10:40
 * @Description: TODO
 */
@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LanguageMapper languageMapper;

    /**
     * 根据ID获取编程语言
     *
     * @param languageId
     * @return
     */
    @Override
    public Language getLanguage(Integer languageId) {
        Language language;
        String key = "language:" + languageId + ":info";
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isNotBlank(json)) {
            language = JSON.parseObject(json, Language.class);
        } else {
            language = languageMapper.selectByPrimaryKey(languageId);
            int time = (int) (Math.random() * 60 * 60 * 24 + 10);
            //将数据写入redis中，避免缓存击穿
            redisUtil.set(key, JSON.toJSONString(language),time);
        }
        return language;
    }

    @Override
    public Language getLanguage(String languageName) {
        return null;
    }

    /**
     * 获取所有编程语言
     *
     * @return
     */
    @Override
    public List<Language> getLanguages() {
        String key = "language:all:info";
        List<Language> languages;
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            languages = JSON.parseArray(json,Language.class);
        } else {
            languages = languageMapper.selectAll();
            for (int i = 0; i < languages.size(); i++) {
                Language language = languages.get(i);
                language.setLanguageCompileCmd("");
                language.setLanguageRuntimeCmd("");
            }
            int time = (int) (Math.random() * 60 * 60 * 24 + 10);
            //将数据写入redis中，避免缓存击穿
            redisUtil.set(key, JSON.toJSONString(languages),time);
        }
        return languages;
    }
}
