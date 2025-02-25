package tqs.euromillions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import java.util.ArrayList;

public class EuromillionsDrawTest {

    private CuponEuromillions sampleCoupon;
    private EuromillionsDraw testDraw;

    @BeforeEach
    public void setUp() {
        sampleCoupon = new CuponEuromillions();
        sampleCoupon.appendDip(Dip.generateRandomDip());
        sampleCoupon.appendDip(Dip.generateRandomDip());
        sampleCoupon.appendDip(new Dip(new int[]{1, 2, 3, 48, 49}, new int[]{1, 9}));
    }

    @DisplayName("Ensure draw results are correctly stored")
    @Test
    public void testGetDrawResults() {
        Dip winningDip = new Dip(new int[]{10, 20, 30, 40, 50}, new int[]{1, 2});
        testDraw = new EuromillionsDraw(winningDip);

        assertEquals(winningDip, testDraw.getDrawResults(), "Draw results should match the provided Dip.");
    }

    @DisplayName("Generate a random draw and verify it contains 5 numbers and 2 stars")
    @Test
    public void testGenerateRandomDraw() {
        EuromillionsDraw randomDraw = EuromillionsDraw.generateRandomDraw();
        Dip drawResults = randomDraw.getDrawResults();

        assertEquals(5, drawResults.getNumbersColl().size(), "Random draw should have exactly 5 numbers.");
        assertEquals(2, drawResults.getStarsColl().size(), "Random draw should have exactly 2 stars.");
    }

    @DisplayName("Ensure correct matching of numbers and stars in a bet")
    @Test
    public void testCompareBetWithDrawToGetResults() {
        Dip winningDip, matchesFound;

        winningDip = sampleCoupon.getDipByIndex(2);
        testDraw = new EuromillionsDraw(winningDip);
        matchesFound = testDraw.findMatchesFor(sampleCoupon).get(2);

        assertEquals(winningDip, matchesFound, "Expected the bet and the matches found to be equal.");

        testDraw = new EuromillionsDraw(new Dip(new int[]{9, 10, 11, 12, 13}, new int[]{2, 3}));
        matchesFound = testDraw.findMatchesFor(sampleCoupon).get(2);

        assertEquals(new Dip(), matchesFound, "Expected an empty Dip when there are no matches.");
    }

    @DisplayName("Handle empty player coupon (no bets)")
    @Test
    public void testFindMatchesForEmptyCoupon() {
        CuponEuromillions emptyCoupon = new CuponEuromillions();
        testDraw = new EuromillionsDraw(Dip.generateRandomDip());

        ArrayList<Dip> matches = testDraw.findMatchesFor(emptyCoupon);
        assertTrue(matches.isEmpty(), "No matches should be found for an empty coupon.");
    }

    @DisplayName("Ensure no partial match affects results")
    @Test
    public void testFindPartialMatch() {
        Dip winningDip = new Dip(new int[]{5, 10, 15, 20, 25}, new int[]{3, 4});
        CuponEuromillions partialCoupon = new CuponEuromillions();
        partialCoupon.appendDip(new Dip(new int[]{5, 10, 15, 40, 50}, new int[]{1, 2})); // Some numbers match, stars don't

        testDraw = new EuromillionsDraw(winningDip);
        Dip matches = testDraw.findMatchesFor(partialCoupon).get(0);

        assertEquals(3, matches.getNumbersColl().size(), "Should have 3 matching numbers.");
        assertEquals(0, matches.getStarsColl().size(), "Should have 0 matching stars.");
    }
}
