function getColorFromScore(score) {
    //红200-0  绿0-187
    try {
        if (score < 60) {
            return '#ff0000';
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
            return '#' + r + g + '00';//红绿蓝
        }
    } catch (e) {
        return '00bb00';
    }
}
function getJudgeResult(obj) {
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
    if (obj == 'PD') {
        return "<span class=\"status waiting\"  style=' color: #6cf !important'><i class=\"icon spinner\"></i>Pending </span>";
    }
    if (obj == 'SE') {
        return "<span class=\"status system_error\" style='color: grey !important;'><i class=\"icon server\"></i>System Error</span>";
    }
    return "<span class=\"status wrong_answer\" style=' color: red !important;'><i class=\"icon remove\"></i>Wrong Answer</span>";
}