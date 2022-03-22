
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    @Test
    void test1() {

        Map<Integer, String> map = new HashMap<>();

        Integer i1 = new Integer(1);
        map.put(i1, "3");
        Integer i2 = new Integer(1);
        map.put(i2, "4");
        boolean b = (i1 == i2);

        return;
    }
}
