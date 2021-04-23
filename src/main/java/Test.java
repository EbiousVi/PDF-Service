
import java.io.IOException;
import java.util.UUID;

public class Test {
    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 100; i++) {
            String x = UUID.randomUUID().toString();
            System.out.println(x.length());
        }

    }
}
