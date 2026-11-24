public class task2 {

    public static void main(String[] args) {
        double x = 2;
        double y = 3;
        double z = 2;

        double p1 = 1.0 / x;
        double p2 = 1.0 / y;
        double p3 = 1.0 / z;

        double f1 = 1 - p1;
        double f2 = 1 - p2;
        double f3 = 1 - p3;

        // Three destination cases
        double caseFFF = f1 * f2 * f3;

        double caseSFF = p1 * f2 * f3 * (1 + task1.ETX2(y, z));
        double caseFSF = f1 * p2 * f3 * (1 + task1.ETX2(x, z));
        double caseFFS = f1 * f2 * p3 * (1 + task1.ETX2(x, y));

        double caseSSF = p1 * p2 * f3 * (1 + z);
        double caseSFS = p1 * f2 * p3 * (1 + y);
        double caseFSS = f1 * p2 * p3 * (1 + x);

        double caseSSS = p1 * p2 * p3;

        double expectedETX =
                (caseFFF + caseSFF + caseFSF + caseFFS
               + caseSSF + caseSFS + caseFSS + caseSSS)
               / (1 - caseFFF);

        System.out.println("ETX = " + expectedETX);
    }
}