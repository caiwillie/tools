package com.caiwillie.tools.leetcode2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.leetcode2anki.commander.ArgDto;
import com.caiwillie.tools.leetcode2anki.common.Constant;
import com.caiwillie.tools.leetcode2anki.model.Question;
import de.hunsicker.jalopy.Jalopy;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class LeetCodeConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^_(\\d{1,})_");

    private static final Pattern CODE_PATTERN = Pattern.compile("//leetcode submit region begin\\(Prohibit modification and deletion\\)((.|\\n){0,})//leetcode submit region end\\(Prohibit modification and deletion\\)");

    public List<Question> convert(ArgDto arg) {
        List<Question> ret = new ArrayList<>();

        TreeMap<Integer, File> codeMap = getFileMap(arg.getCodePath(), Constant.JAVA);

        TreeMap<Integer, File> contentMap = getFileMap(arg.getContentPath(), Constant.MD);

        for (Map.Entry<Integer, File> entry : codeMap.entrySet()) {
            Integer sn = entry.getKey();
            File codeFile = entry.getValue();
            if(!contentMap.containsKey(sn)) {
                throw new RuntimeException(StrUtil.format("路径 {} 对应的描述文件不存在", codeFile.getAbsolutePath()));
            }

            File contentFile = contentMap.get(sn);

            String codeTemp = FileUtil.readUtf8String(codeFile);

            String code = ReUtil.getGroup1(CODE_PATTERN, codeTemp);

            String code2 = formatCode(code);

            String content = FileUtil.readUtf8String(contentFile);

            Question question = new Question();
            question.setSn(String.valueOf(sn));
            question.setCode(code);
            question.setContent(content);

            ret.add(question);
        }

        return ret;
    }

    private TreeMap<Integer, File> getFileMap(File root, String suffix) {
        TreeMap<Integer, File> ret = new TreeMap<>();
        List<File> list = FileUtil.loopFiles(root, 1, path -> {
            if(!path.isFile()) {
                // 不是文件，返回false
                return false;
            }

            if(!StrUtil.equals(FileNameUtil.extName(path), suffix)) {
                // 后缀名不是符合, 过滤
                return false;
            }

            return true;
        });

        if(CollUtil.isEmpty(list)) {
            return ret;
        }

        for (File file : list) {
            String name = FileNameUtil.getName(file);
            String snStr = ReUtil.getGroup1(SN_PATTERN, name);
            if(snStr == null) {
                continue;
            }

            int sn = Integer.parseInt(snStr);
            ret.put(sn, file);
        }

        return ret;
    }


    private String formatCode(String code) {
        StringBuffer sb = new StringBuffer();
        Jalopy jalopy = new Jalopy();
        jalopy.setEncoding("UTF-8");
        jalopy.setInput(code, "A.java");
        jalopy.setOutput(sb);
        jalopy.format();
        return sb.toString();
    }

}
