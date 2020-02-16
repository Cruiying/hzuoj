package com.hqz.hzuoj.service;

import com.hqz.hzuoj.bean.Language;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/10 10:37
 * @Description: TODO
 */
public interface LanguageService {
    Language getLanguage(Integer languageId);

    Language getLanguage(String languageName);

    List<Language> getLanguages();
}
