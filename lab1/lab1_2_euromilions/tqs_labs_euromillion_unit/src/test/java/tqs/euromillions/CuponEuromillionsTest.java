package tqs.euromillions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.util.Iterator;

public class CuponEuromillionsTest {

    private CuponEuromillions coupon;

    @BeforeEach
    public void setUp() {
        coupon = new CuponEuromillions();
        coupon.appendDip(new Dip(new int[]{10, 20, 30, 40, 50}, new int[]{1, 2}));
        coupon.appendDip(new Dip(new int[]{5, 15, 25, 35, 45}, new int[]{3, 4}));
    }

    @DisplayName("Ensure dips are appended correctly")
    @Test
    public void testAppendDip() {
        assertEquals(2, coupon.countDips(), "Should contain 2 dips after appending.");
    }

    @DisplayName("Ensure dip retrieval by index is correct")
    @Test
    public void testGetDipByIndex() {
        Dip dip = coupon.getDipByIndex(0);
        assertEquals(new Dip(new int[]{10, 20, 30, 40, 50}, new int[]{1, 2}), dip, "First dip should match.");
    }

    @DisplayName("Handle out-of-bounds dip retrieval")
    @Test
    public void testGetDipByIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> coupon.getDipByIndex(5), "Should throw exception for invalid index.");
    }

    @DisplayName("Ensure formatting outputs correct structure")
    @Test
    public void testFormat() {
        String expectedFormat = "Dip #1:N[ 10 20 30 40 50] S[  1  2]\nDip #2:N[  5 15 25 35 45] S[  3  4]\n";
        assertEquals(expectedFormat, coupon.format(), "Formatted output should match expected format.");
    }

    @DisplayName("Ensure iterator correctly iterates through dips")
    @Test
    public void testIterator() {
        Iterator<Dip> iterator = coupon.iterator();
        assertTrue(iterator.hasNext(), "Iterator should have elements.");
        assertEquals(new Dip(new int[]{10, 20, 30, 40, 50}, new int[]{1, 2}), iterator.next(), "First dip should match.");
    }
}
