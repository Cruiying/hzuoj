class Common {
    /**
     * 返回用户等级对应颜色
     * @param username
     * @param rating
     * @returns {string}
     */
    getUserRatingColor(username, rating) {
        if (rating == null || rating == undefined) return " ";
        if (rating >= 3500) return "<span style='color: red !important;'><b>" + "<span style='color:black'>" + username.substr(0, 1) + "</span>" + username.substr(1,username.length) + "</b></span>";
        if (rating >= 3000) return "<span style='color: red !important;'><b>" + username + "</b></span>";
        if (rating >= 2600) return "<span style='color: rgb(254, 76, 97)!important;'><b>" + username + "</b></span>";
        if (rating >= 2300) return "<span style='color: #FF8C00 !important;'><b>" + username + "</b></span>";
        if (rating >= 2100) return "<span style='color: #FBBD08 !important;'><b>" + username + "</b></span>";
        if (rating >= 1900) return "<span style='color: #a0a !important;'><b>" + username + "</b></span>";
        if (rating >= 1600) return "<span style='color: blue !important;'><b>" + username + "</b></span>";
        if (rating >= 1400) return "<span style='color: #03A89E !important'><b>" + username + "</b></span>";
        if (rating >= 1200) return "<span style='color: green !important'><b>" + username + "</b></span>";
        if (rating < 1200) return "<span style='color: gray !important'><b>" + username + "</b></span>";
    }

    /**
     * 格式化日期
     * @param time
     * @returns {string}
     */
    getDateFormat(time) {
        let date = new Date(time);
        return date.format("yyyy-MM-dd hh:mm:ss").toString();
    }

    /**
     * 根据分数返回分数对应的颜色
     * @param score
     * @returns {string}
     */
    getColorFromScore(score) {
        if (score == undefined || score == null) score = 0;
        try {
            if (score < 60) {
                return "<span class=\"score\" style='color: #ff0000'>" + score + "</span>"
            } else {
                let scoreB = score - 60;
                let p = score / 100;
                let pr = scoreB / 40;
                if (pr > 1) {
                    pr = 1;
                }
                let r = parseInt(200 - pr * 200).toString(16);
                if (r.length < 2) {
                    r = '0' + r;
                }
                let g = parseInt(p * 187).toString(16);
                if (g.length < 2) {
                    g = '0' + g;
                }
                return "<span class=\"score\" style='color: " + ('#' + r + g + '00') + "'>" + score + "</span>"
            }
        } catch (e) {
            return "<span class=\"score\" style='color: #00bb00'>" + score + "</span>"
        }
    }

    /**
     * 根据测评结果返回对于的颜色
     * @param obj
     * @returns {string}
     */
    getJudgeResult(obj) {
        if (obj == 'TLE') {
            return "<span class=\"status time_limit_exceeded\" style=\"color: sandybrown !important;\"><i class=\"icon clock\"></i>Time Limit Exceeded</span>\n";
        }
        if (obj == 'MLE') {
            return "<span class=\"status memory_limit_exceeded\" style=\"color: sandybrown !important;\"><i class=\"bug icon\"></i>Memory Limit Exceeded</span>\n";
        }
        if (obj == 'OLE') {
            return "<span class=\"status output_limit_exceeded\" style=\"color: sandybrown !important;\"><i class=\"icon print\"></i>Output Limit Exceeded</span>\n";
        }
        if (obj == 'AC') {
            return "<span class=\"status accepted\" style=' color: forestgreen !important;'><i class=\"icon checkmark\"></i>Accepted</span>";
        }
        if (obj == 'WA') {
            return "<span class=\"status wrong_answer\" style=' color: red !important;'><i class=\"icon remove\"></i>Wrong Answer</span>";
        }
        if (obj == 'RE') {
            return "<span class=\"status runtime_error\" style='color: darkorchid !important'><i class=\"icon bomb\"></i>Runtime Error </span>";
        }
        if (obj == 'CE') {
            return "<span class=\"status compile_error\" style=' color: rgb(0, 68, 136) !important;'><i class=\"icon code\"></i>Compile Error</span>";
        }
        if (obj == 'queue') {
            return "<span class=\"status waiting\" style=' color: #6cf !important'><i class=\"icon hourglass half\"></i>Queue </span>";
        }
        if (obj == 'Running') {
            return "<span class=\"status waiting\"  style=' color: #6cf !important'><i class=\"icon hourglass spinner\"></i>Running </span>";
        }
        if (obj == 'compile') {
            return "<span class=\"status waiting\"  style=' color: #6cf !important'><i class=\"icon hourglass spinner\"></i>Compile </span>";
        }
        if (obj == 'PD') {
            return "<span class=\"status waiting\"  style=' color: #6cf !important'><i class=\"icon spinner\"></i>Pending </span>";
        }
        if (obj == 'SE') {
            return "<span class=\"status system_error\" style='color: grey !important;'><i class=\"icon server\"></i>System Error</span>";
        }
        return "<span class=\"status\" style='color:#25bb9b;'>已提交</span>";
    }

    /**
     *
     * @param mss
     * @returns {string}
     */
    formatDuring(mss) {
        const days = Math.round(mss / (1000 * 60 * 60 * 24));
        const hours = Math.round((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.round((mss % (1000 * 60 * 60)) / (1000 * 60));
        let ans = "";
        if (days > 0) ans += days + " 天 ";
        if (hours > 0) ans += hours + " 小时 ";
        if (minutes > 0) ans += minutes + " 分钟 ";
        return ans;
    }

    getRuntimeTime(time) {
        if (time == undefined || time == null || time == '') {
            time = 0;
        }
        return time + " ms";
    }

    getRuntimeMemory(memory) {
        if (undefined ==  memory || memory == null || memory == '') {
            memory = 0;
        }
        if (memory >= 1024) {
            return Math.round(memory / 1024 * 100) / 100 + " MB";
        } else {
            return memory + " KB";
        }
    }

    getRankScore(score) {
        if (score == undefined) score = 0;
        if (score > 80) return "<span style='color: rgb(57, 191, 156)'>" + score + "</span>";
        if (score > 60) return "<span style='color: rgb(255, 92, 28)'>" + score + "</span>";
        if (score > 40) return "<span style='color: rgb(255, 92, 28)'>" + score + "</span>";
        if (score >= 0) return "<span style='color: rgb(209, 42, 42)'>" + score + "</span>";
    }

    getPage(pageNum, total) {
        let page = {begin: 1, end: 1, page: 1, total: 1};
        page.begin = 1;
        page.end = total;
        page.page = pageNum;
        page.total = total;
        if ((page.end - page.begin) <= 4) {
            return page;
        } else {
            if ((pageNum - page.begin) >= 2) {
                if ((pageNum + 2) >= total) {
                    page.end = total;
                    page.begin = page.end - 4;
                } else {
                    page.end = pageNum + 2;
                    page.begin = pageNum - 2;
                }
            } else {
                if ((pageNum - 2) >= 1) {
                    page.begin = pageNum - 2;
                    page.end = page.begin + 4;
                } else {
                    page.begin = 1;
                    page.end = page.begin + 4;
                }
            }
        }
        return page;
    }

    getRatingColor(rating) {
        if (rating == null || rating == undefined) return " ";
        if (rating >= 3000) return "<span style='color: red !important;'><b>" + rating + "</b></span>";
        if (rating >= 2600) return "<span style='color: rgb(254, 76, 97)!important;'><b>" + rating + "</b></span>";
        if (rating >= 2300) return "<span style='color: #FF8C00 !important;'><b>" + rating + "</b></span>";
        if (rating >= 2100) return "<span style='color: #FBBD08 !important;'><b>" + rating + "</b></span>";
        if (rating >= 1900) return "<span style='color: #a0a !important;'><b>" + rating + "</b></span>";
        if (rating >= 1600) return "<span style='color: blue !important;'><b>" + rating + "</b></span>";
        if (rating >= 1400) return "<span style='color: #03A89E !important'><b>" + rating + "</b></span>";
        if (rating >= 1200) return "<span style='color: green !important'><b>" + rating + "</b></span>";
        if (rating < 1200) return "<span style='color: gray !important'><b>" + rating + "</b></span>";

    }
}

const common = new Common();
