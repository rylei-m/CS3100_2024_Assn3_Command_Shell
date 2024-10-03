package com.example;

public class Assn2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            Help.printHelp();
            return;
        }

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-fib":
                        int fibNum = Integer.parseInt(args[++i]);
                        System.out.println("Fibonacci of " + fibNum + " is " + Fib.fibonacci(fibNum));
                        break;
                    case "-fac":
                        int facNum = Integer.parseInt(args[++i]);
                        System.out.println("Factorial of " + facNum + " is " + Fac.factorial(facNum));
                        break;
                    case "-e":
                        int eIterations = Integer.parseInt(args[++i]);
                        System.out.println("Value of e using " + eIterations + " iterations is " + E.valE(eIterations));
                        break;
                    default:
                        System.out.println("Unknown command line argument: " + args[i]);
                        Help.printHelp();
                        return;
                }
            }
        } catch (Exception e) {
            Help.printHelp();
        }
    }
}
