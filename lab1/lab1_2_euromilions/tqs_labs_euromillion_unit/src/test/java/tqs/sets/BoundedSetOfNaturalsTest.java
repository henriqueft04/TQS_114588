/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.sets.BoundedSetOfNaturals;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;


    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);
        setB = new BoundedSetOfNaturals(10);
        setB.add(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
    }


    @AfterEach
    public void tearDown() {
        setA = setB = setC = null;
    }

    @Test
    public void testAddElement() {

        setA.add(99);
        assertTrue(setA.contains(99), "add: added element not found in set.");
        assertEquals(1, setA.size());

        setB.add(11);
        assertTrue(setB.contains(11), "add: added element not found in set.");
        assertEquals(7, setB.size(), "add: elements count not as expected.");
    }

    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};

        // must fail with exception
        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @Test
    public void testIntersects() {
        BoundedSetOfNaturals setX = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals setY = BoundedSetOfNaturals.fromArray(new int[]{3, 4, 5});
        BoundedSetOfNaturals setZ = BoundedSetOfNaturals.fromArray(new int[]{6, 7, 8});

        assertTrue(setX.intersects(setY), "Sets should intersect on element 3.");
        assertFalse(setX.intersects(setZ), "Sets should not intersect.");
    }

    @Test
    public void testAddMaxAllowedElements() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(3);
        set.add(1);
        set.add(2);
        set.add(3);
        assertEquals(3, set.size(), "Set deve ter 3 elementos.");
    }

    @Test
    public void testIntersectionWithEmptySet() {
        BoundedSetOfNaturals setA = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals emptySet = new BoundedSetOfNaturals(5);

        assertFalse(setA.intersects(emptySet), "A set não deve intersetar com um conjunto vazio.");
    }

    @Test
    public void testContainsElement() {
        BoundedSetOfNaturals set = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30});
        assertTrue(set.contains(10), "Set não deve ter o 10.");
        assertFalse(set.contains(99), "Set não deve ter o 99.");
    }

    @Test
    public void testIntersectionWithDisjointSet() {
        BoundedSetOfNaturals setA = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals setB = BoundedSetOfNaturals.fromArray(new int[]{4, 5, 6});

        assertFalse(setA.intersects(setB), "Disjoint sets não devem intersetar.");
    }

    @Test
    public void testAddDuplicateElement() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(5);
        set.add(1);
        assertThrows(IllegalArgumentException.class, () -> set.add(1), "Should throw exception for duplicate.");
    }

    @Test
    public void testAddNegativeNumber() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(5);
        assertThrows(IllegalArgumentException.class, () -> set.add(-5), "Should throw exception for negative numbers.");
    }

    @Test
    public void testAddZero() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(5);
        assertThrows(IllegalArgumentException.class, () -> set.add(0), "Should throw exception for zero.");
    }

    @Test
    public void testAddArrayWithDuplicate() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(5);
        assertThrows(IllegalArgumentException.class, () -> set.add(new int[]{1, 2, 2}), "Should reject duplicate in array.");
    }

    @Test
    public void testAddArrayWithNegative() {
        BoundedSetOfNaturals set = new BoundedSetOfNaturals(5);
        assertThrows(IllegalArgumentException.class, () -> set.add(new int[]{1, -3, 4}), "Should reject negative numbers.");
    }

    @Test
    public void testEqualsNull() {
        BoundedSetOfNaturals set = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        assertNotEquals(null, set, "A set should not be equal to null.");
    }

    @Test
    public void testEqualsDifferentClass() {
        BoundedSetOfNaturals set = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        assertNotEquals("String", set, "A set should not be equal to a String.");
    }

    @Test
    public void testEqualsDifferentSize() {
        BoundedSetOfNaturals setA = new BoundedSetOfNaturals(5);
        BoundedSetOfNaturals setB = new BoundedSetOfNaturals(10);
        setA.add(new int[]{1, 2, 3});
        setB.add(new int[]{1, 2, 3});
        assertEquals(setA, setB, "Sets with same elements should be equal regardless of maxSize.");
    }

    @Test
    public void testHashCodeSameSet() {
        BoundedSetOfNaturals setA = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals setB = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        assertEquals(setA.hashCode(), setB.hashCode(), "Equal sets must have the same hash code.");
    }

    @Test
    public void testHashCodeDifferentSet() {
        BoundedSetOfNaturals setA = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals setB = BoundedSetOfNaturals.fromArray(new int[]{4, 5, 6});
        assertNotEquals(setA.hashCode(), setB.hashCode(), "Different sets should have different hash codes.");
    }

}
