import de.hunsicker.jalopy.Jalopy;
import org.junit.jupiter.api.Test;

public class JalopyTest {

    @Test
    void test() {
        StringBuffer sb = new StringBuffer();
        Jalopy jalopy = new Jalopy();
        jalopy.setEncoding("UTF-8");
        jalopy.setInput("public class ArgDto { private File codePath; private File contentPath; public ArgDto(File codePath, File contentPath) { this.codePath = codePath; this.contentPath = contentPath; } }", "A.java");
        jalopy.setOutput(sb);
        jalopy.format();
        return;

    }
}
