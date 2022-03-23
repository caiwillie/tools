package com.caiwillie.tools.leetcode2anki.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.caiwillie.tools.leetcode2anki.commander.ArgDto;
import com.caiwillie.tools.leetcode2anki.common.Constant;
import com.caiwillie.tools.leetcode2anki.model.Question;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author caiwillie
 */
public class LeetCodeConverter {

    private static final Pattern SN_PATTERN = Pattern.compile("^_(\\d{1,})_");

    private static final Pattern CODE_PATTERN = Pattern.compile("//leetcode submit region begin\\(Prohibit modification and deletion\\)((.|\\n){0,})//leetcode submit region end\\(Prohibit modification and deletion\\)");

    private static final CodeFormatter CODE_FORMATTER = getCodeFormatter();

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

            code = formatCode(code);

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

    private static String formatCode(String code) {
        code = Optional.ofNullable(code).orElse("");

        TextEdit edit = CODE_FORMATTER.format(
                CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
                code, // source to format
                0, // starting position
                code.length(), // length
                0, // initial indentation
                System.getProperty("line.separator") // line separator
        );

        IDocument document = new Document(code);
        try {
            edit.apply(document);
        } catch (MalformedTreeException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return document.get();
    }

    private static CodeFormatter getCodeFormatter() {
        // take default Eclipse formatting options
        Map options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

        // initialize the compiler settings to be able to format 1.5 code
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);

        // change the option to wrap each enum constant on a new line
        options.put(
                DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
                DefaultCodeFormatterConstants.createAlignmentValue(
                        true,
                        DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
                        DefaultCodeFormatterConstants.INDENT_ON_COLUMN));

        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");

        return ToolFactory.createCodeFormatter(options);
    }

}
