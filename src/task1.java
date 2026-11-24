public class task1 {

    // Reusable method for calculating ETX for 2 destinations
    public static double ETX2(double x, double y) {
        double p1 = 1.0 / x;
        double p2 = 1.0 / y;

        double f1 = 1 - p1;
        double f2 = 1 - p2;

        double caseFF = f1 * f2;

        double caseSF = p1 * f2 * (1 + y);
        double caseFS = f1 * p2 * (1 + x);
        double caseSS = p1 * p2;

        return (caseFF + caseSF + caseFS + caseSS) / (1 - caseFF);
    }

    public static void main(String[] args) {
        double x = 3;
        double y = 3;

        System.out.println(ETX2(x, y));
    }
}