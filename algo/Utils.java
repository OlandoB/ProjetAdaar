
public class Utils {
    public static String append_operator(String res, char operator, int n) {
        String s = res;
        for (int i = 0; i < n; i++) {
            s += operator;
        }
        return s;
    }
}
