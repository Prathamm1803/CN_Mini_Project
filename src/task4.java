public class task4 {

    public static void main(String[] args) {
        double etx = calculateTwoRelayETX(2.0, 3.0, 3.0, 2.0);
        System.out.println("ETX (0 -> {1,2} -> 4) = " + etx);
    }

    public static double calculateTwoRelayETX(double x, double y, double a, double b) {
        double p1 = 1.0 / x;
        double p2 = 1.0 / y;

        double f1 = 1.0 - p1;
        double f2 = 1.0 - p2;

        double caseFF = f1 * f2;                         
        double caseSF = p1 * f2 * (1.0 + a);              
        double caseFS = f1 * p2 * (1.0 + b);              
        double caseSS = p1 * p2 * (1.0 + Math.min(a, b));

        double expectedETX = (caseFF + caseSF + caseFS + caseSS) / (1.0 - caseFF);
        return expectedETX;
    }
}