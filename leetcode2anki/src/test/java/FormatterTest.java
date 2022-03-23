import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class FormatterTest {

    @Test
    void test2() {

        // retrieve the source to format
        String source = "\n" +
                "    class Solution {\n" +
                "        public int[] twoSum(int[] nums, int target) {\n" +
                "            Map<Integer, Integer>  map = new HashMap<>();\n" +
                "            int i = 0;\n" +
                "            while (i < nums.length) {\n" +
                "                int num = nums[i];\n" +
                "                if(map.containsKey(target - num)) {\n" +
                "                    return new int[] {i, map.get(target- num)};\n" +
                "                } else {\n" +
                "                    map.put(num, i);\n" +
                "                }\n" +
                "                i++;\n" +
                "            }\n" +
                "            return new int[]{-1, -1};\n" +
                "        }\n" +
                "    }\n" +
                "    ";


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

        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

        final TextEdit edit = codeFormatter.format(
                CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
                source, // source to format
                0, // starting position
                source.length(), // length
                0, // initial indentation
                System.getProperty("line.separator") // line separator
        );

        IDocument document = new Document(source);
        try {
            edit.apply(document);
        } catch (MalformedTreeException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // display the formatted string on the System out
        System.out.println(document.get());
    }


}
