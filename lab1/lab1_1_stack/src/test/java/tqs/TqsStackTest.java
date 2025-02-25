package tqs;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TqsStackTest {

    private TqsStack<Integer> stack = new TqsStack<>();

    @Test
    void popFromEmptyStackThrowsException() {
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    void peekFromEmptyStackThrowsException() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    void pushNullElementThrowsException() {
        assertThrows(NullPointerException.class, () -> stack.push(null));
    }

    @Test
    void pushAndPopMultipleElements() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assert stack.pop() == 3;
        assert stack.pop() == 2;
        assert stack.pop() == 1;
        assert stack.isEmpty();
    }


    @Test
    void sizeAfterMultiplePushAndPop() {
        stack.push(1);
        stack.push(2);
        stack.pop();
        stack.push(3);

        assert stack.size() == 2;
    }

    @Test
    void testPopTopN() {
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);
        stack.push(50);

        int result = stack.popTopN(3);

        assertEquals(30, result);
        assertEquals(2, stack.size());
    }
}
