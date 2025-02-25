package tqs;

import java.util.LinkedList;

public class TqsStack<T> {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    private final LinkedList<T> stack = new LinkedList<>();

    public void push(T item) {
        if (item == null) {
            throw new NullPointerException("Null values are not allowed in the stack");
        }
        stack.add(item);
    }


    public T pop() {
        T last = stack.getLast();
        stack.removeLast();
        return last;
    }

    public T peek() {
        return stack.getLast();
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public T popTopN(int n) {
       T top = null;

       for (int i = 0; i < n; i++) {
           top = stack.removeFirst();
       }
       return top;
    }

}