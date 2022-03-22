package com.caiwillie.tools.leetcode2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.leetcode2anki.commander.ArgDto;
import com.caiwillie.tools.leetcode2anki.common.Constant;
import com.caiwillie.tools.leetcode2anki.model.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class LeetCodeConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^_(\\d{1,})_");

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

            String content = FileUtil.readUtf8String(contentFile);




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

}
