package tqs;

import static java.util.Arrays.asList;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Calculator {
    private final Deque<Number> stack = new LinkedList<Number>();
    private static final List<String> OPS = asList("-", "+", "*", "/");

    public void push(Object arg) {
        if (arg instanceof Number) {
            stack.add((Number) arg);
        } else if (OPS.contains(arg)) {
            Number y = stack.removeLast();
            Number x = stack.isEmpty() ? 0 : stack.removeLast();
            Double val = null;
            if (arg.equals("-")) {
                val = x.doubleValue() - y.doubleValue();
            } else if (arg.equals("+")) {
                val = x.doubleValue() + y.doubleValue();
            } else if (arg.equals("*")) {
                val = x.doubleValue() * y.doubleValue();
            } else if (arg.equals("/")) {
                if (y.doubleValue() == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                val = x.doubleValue() / y.doubleValue();
            }
            push(val);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + arg);
        }
    }

    public Number value() {
        return stack.getLast();
    }
}