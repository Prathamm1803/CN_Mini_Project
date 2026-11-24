public class task3 {

    public static void main(String[] args) {
        double x = 2.0, y = 3.0, z = 2.0;

        double a = 3.0, b = 2.0, c = 3.0;

        double systemETX = calculateSystemETX(x, y, z, a, b, c);
        System.out.println("System ETX (Source -> Relays -> Destination) = " + systemETX);
    }

    public static double calculateSystemETX(double x, double y, double z, double a, double b, double c) {
        double p1 = 1.0 / x;
        double p2 = 1.0 / y;
        double p3 = 1.0 / z;

        double f1 = 1.0 - p1;
        double f2 = 1.0 - p2;
        double f3 = 1.0 - p3;

        double caseFFF = f1 * f2 * f3;

        double caseSFF = p1 * f2 * f3 * (1.0 + a);
        double caseFSF = f1 * p2 * f3 * (1.0 + b);
        double caseFFS = f1 * f2 * p3 * (1.0 + c);

        double caseSSF = p1 * p2 * f3 * (1.0 + Math.min(a, b));
        double caseSFS = p1 * f2 * p3 * (1.0 + Math.min(a, c));
        double caseFSS = f1 * p2 * p3 * (1.0 + Math.min(b, c));

        double caseSSS = p1 * p2 * p3 * (1.0 + Math.min(a, Math.min(b, c)));

        double expectedETX = (caseFFF + caseSFF + caseFSF + caseFFS + caseSSF + caseSFS + caseFSS + caseSSS) / (1.0 - caseFFF);
        return expectedETX;
    }
}