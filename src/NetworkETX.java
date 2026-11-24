public class NetworkETX {

    // Link ETX values from topology:
    // Hop 1: Source (0) -> Relays (1, 2, 3)
    static final double ETX_0_1 = 2.0;
    static final double ETX_0_2 = 3.0;
    static final double ETX_0_3 = 2.0;

    // Hop 2: Relays -> Destination 4
    static final double ETX_1_4 = 3.0;
    static final double ETX_2_4 = 2.0;

    // Hop 2: Relays -> Destination 5
    static final double ETX_2_5 = 2.0;
    static final double ETX_3_5 = 3.0;

    static final double INF = Double.POSITIVE_INFINITY;

    public static void main(String[] args) {
        // Only valid multicast-capable forwarding sets that can reach both {4, 5}
        String[] sets = {
            "{2}",
            "{1, 2}",
            "{1, 3}",
            "{2, 3}",
            "{1, 2, 3}"
        };

        // Case 1: ETX to reach Destination 4 only
        double[] etxDest4 = {
            ETX_0_2 + ETX_2_4,                                              // {2}
            task4.calculateTwoRelayETX(ETX_0_1, ETX_0_2, ETX_1_4, ETX_2_4), // {1, 2}
            ETX_0_1 + ETX_1_4,                                              // {1, 3} -> only relay 1 reaches 4
            ETX_0_2 + ETX_2_4,                                              // {2, 3} -> only relay 2 reaches 4
            calcThreeRelaySingleDest(ETX_0_1, ETX_0_2, ETX_0_3, ETX_1_4, ETX_2_4, INF) // {1, 2, 3}
        };

        // Case 2: ETX to reach Destination 5 only
        double[] etxDest5 = {
            ETX_0_2 + ETX_2_5,                                              // {2}
            ETX_0_2 + ETX_2_5,                                              // {1, 2} -> only relay 2 reaches 5
            ETX_0_3 + ETX_3_5,                                              // {1, 3} -> only relay 3 reaches 5
            task4.calculateTwoRelayETX(ETX_0_2, ETX_0_3, ETX_2_5, ETX_3_5), // {2, 3}
            calcThreeRelaySingleDest(ETX_0_1, ETX_0_2, ETX_0_3, INF, ETX_2_5, ETX_3_5) // {1, 2, 3}
        };

        // Case 3: ETX to reach BOTH Destinations {4, 5}
        double[] etxBoth = {
            ETX_0_2 + task1.ETX2(ETX_2_4, ETX_2_5), // {2} -> Relay 2 broadcasts to {4,5} using task1
            calcSet12Multicast(),                   // {1, 2}
            calcSet13Multicast(),                   // {1, 3}
            calcSet23Multicast(),                   // {2, 3}
            calcSet123Multicast()                   // {1, 2, 3}
        };

        // Print Comparison Table
        System.out.println("=================================================================");
        System.out.printf("%-15s | %-13s | %-13s | %-15s%n",
                "Forwarding Set", "Dest {4}", "Dest {5}", "Multicast {4,5}");
        System.out.println("-----------------------------------------------------------------");

        for (int i = 0; i < sets.length; i++) {
            System.out.printf("%-15s | %-13.3f | %-13.3f | %-15.3f%n",
                    sets[i], etxDest4[i], etxDest5[i], etxBoth[i]);
        }
        System.out.println("=================================================================\n");

        // Find and print optimal forwarding set for each case
        printBest("Destination {4} only", sets, etxDest4);
        printBest("Destination {5} only", sets, etxDest5);
        printBest("Multicast {4, 5}    ", sets, etxBoth);
    }

    // 3-relay broadcast for a single destination (handles relays with INF link to dest)
    public static double calcThreeRelaySingleDest(
            double x, double y, double z,
            double a, double b, double c) {

        double p1 = 1.0 / x, p2 = 1.0 / y, p3 = 1.0 / z;
        double f1 = 1.0 - p1, f2 = 1.0 - p2, f3 = 1.0 - p3;

        double failProb = f1 * f2 * f3
                + (Double.isInfinite(a) ? p1 * f2 * f3 : 0.0)
                + (Double.isInfinite(b) ? f1 * p2 * f3 : 0.0)
                + (Double.isInfinite(c) ? f1 * f2 * p3 : 0.0);

        double numerator = 1.0;

        if (!Double.isInfinite(a)) numerator += p1 * f2 * f3 * a;
        if (!Double.isInfinite(b)) numerator += f1 * p2 * f3 * b;
        if (!Double.isInfinite(c)) numerator += f1 * f2 * p3 * c;

        numerator += p1 * p2 * f3 * Math.min(a, b);
        numerator += p1 * f2 * p3 * Math.min(a, c);
        numerator += f1 * p2 * p3 * Math.min(b, c);
        numerator += p1 * p2 * p3 * Math.min(a, Math.min(b, c));

        return numerator / (1.0 - failProb);
    }

    // Multicast {4, 5} using Forwarding Set {1, 2}
    public static double calcSet12Multicast() {
        double p1 = 1.0 / ETX_0_1, p2 = 1.0 / ETX_0_2;
        double f1 = 1.0 - p1, f2 = 1.0 - p2;

        double hop2Relay2Both = task1.ETX2(ETX_2_4, ETX_2_5);
        double unicastTo5 = ETX_0_2 + ETX_2_5;

        double caseFF = f1 * f2;
        double caseSF = p1 * f2 * (1.0 + ETX_1_4 + unicastTo5);
        double caseFS = f1 * p2 * (1.0 + hop2Relay2Both);
        double caseSS = p1 * p2 * (1.0 + hop2Relay2Both);

        return (caseFF + caseSF + caseFS + caseSS) / (1.0 - caseFF);
    }

    // Multicast {4, 5} using Forwarding Set {1, 3}
    public static double calcSet13Multicast() {
        double p1 = 1.0 / ETX_0_1, p3 = 1.0 / ETX_0_3;
        double f1 = 1.0 - p1, f3 = 1.0 - p3;

        double caseFF = f1 * f3;
        double caseSF = p1 * f3 * (1.0 + ETX_1_4 + (ETX_0_3 + ETX_3_5));
        double caseFS = f1 * p3 * (1.0 + ETX_3_5 + (ETX_0_1 + ETX_1_4));
        double caseSS = p1 * p3 * (1.0 + ETX_1_4 + ETX_3_5);

        return (caseFF + caseSF + caseFS + caseSS) / (1.0 - caseFF);
    }

    // Multicast {4, 5} using Forwarding Set {2, 3}
    public static double calcSet23Multicast() {
        double p2 = 1.0 / ETX_0_2, p3 = 1.0 / ETX_0_3;
        double f2 = 1.0 - p2, f3 = 1.0 - p3;

        double hop2Relay2Both = task1.ETX2(ETX_2_4, ETX_2_5);
        double unicastTo4 = ETX_0_2 + ETX_2_4;

        double caseFF = f2 * f3;
        double caseSF = p2 * f3 * (1.0 + hop2Relay2Both);
        double caseFS = f2 * p3 * (1.0 + ETX_3_5 + unicastTo4);
        double caseSS = p2 * p3 * (1.0 + hop2Relay2Both);

        return (caseFF + caseSF + caseFS + caseSS) / (1.0 - caseFF);
    }

    // Multicast {4, 5} using Forwarding Set {1, 2, 3}
    public static double calcSet123Multicast() {
        double p1 = 1.0 / ETX_0_1, p2 = 1.0 / ETX_0_2, p3 = 1.0 / ETX_0_3;
        double f1 = 1.0 - p1, f2 = 1.0 - p2, f3 = 1.0 - p3;

        double subETX4 = task4.calculateTwoRelayETX(ETX_0_1, ETX_0_2, ETX_1_4, ETX_2_4);
        double subETX5 = task4.calculateTwoRelayETX(ETX_0_2, ETX_0_3, ETX_2_5, ETX_3_5);
        double hop2Relay2Both = task1.ETX2(ETX_2_4, ETX_2_5);

        double caseFFF = f1 * f2 * f3;
        double caseSFF = p1 * f2 * f3 * (1.0 + ETX_1_4 + subETX5);
        double caseFSF = f1 * p2 * f3 * (1.0 + hop2Relay2Both);
        double caseFFS = f1 * f2 * p3 * (1.0 + ETX_3_5 + subETX4);

        double caseSSF = p1 * p2 * f3 * (1.0 + hop2Relay2Both);
        double caseSFS = p1 * f2 * p3 * (1.0 + ETX_1_4 + ETX_3_5);
        double caseFSS = f1 * p2 * p3 * (1.0 + hop2Relay2Both);

        double caseSSS = p1 * p2 * p3 * (1.0 + hop2Relay2Both);

        return (caseFFF + caseSFF + caseFSF + caseFFS
              + caseSSF + caseSFS + caseFSS + caseSSS)
              / (1.0 - caseFFF);
    }

    private static void printBest(String label, String[] sets, double[] values) {
        int bestIdx = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[bestIdx]) {
                bestIdx = i;
            }
        }
        System.out.printf("Best for %s -> Set %-9s (Min ETX = %.3f)%n",
                label, sets[bestIdx], values[bestIdx]);
    }
}