import de.hunsicker.jalopy.Jalopy;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.jupiter.api.Test;

public class FormatterTest {

    @Test
    void test() {
        StringBuffer sb = new StringBuffer();
        Jalopy jalopy = new Jalopy();
        jalopy.setEncoding("UTF-8");
        jalopy.setInput("    class Solution {\n" +
                "        public int[] twoSum(int[] nums, int target) {\n" +
                "            Map<Integer, Integer> map = new HashMap<>();\n" +
                "            int i = 0;\n" +
                "            while (i < nums.length) {\n" +
                "                int num = nums[i];\n" +
                "                if (map.containsKey(target - num)) {\n" +
                "                    return new int[]{i,\n" +
                "                            map.get(target - num)};\n" +
                "                } else {\n" +
                "                    map.put(num, i);\n" +
                "                }\n" +
                "                i++;\n" +
                "            } return new int[]{-1, -1};\n" +
                "        }\n" +
                "    }", "A.java");
        jalopy.setOutput(sb);
        jalopy.format();
        return;
    }

    @Test
    void test2() {
        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

        // retrieve the source to format
        String source = "public class TestFormatter{public static void main(String[] args){System.out.println(\"Hello World\");}}";

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
