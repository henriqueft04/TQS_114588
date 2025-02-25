package tqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class _TqsStackTest_ {

    static final Logger logger = Logger.getLogger(_TqsStackTest_.class.getName());

    TqsStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new TqsStack<>();
    }

    @Test
    void pushTest() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);

        assert stack.size() == 5;
    }

    @Test
    void popTest() {
        stack.push(1);
        stack.push(2);

        assert stack.pop() == 2;
        assert stack.size() == 1;
    }

    @Test
    void peekTest() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);

        assert stack.peek() == 5;
    }

    @Test
    void isEmptyTest() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);

        assert !stack.isEmpty();
    }

    @Test
    void sizeTest() {
        stack.push(1);
        stack.push(2);

        assert stack.size() == 2;
    }


}
