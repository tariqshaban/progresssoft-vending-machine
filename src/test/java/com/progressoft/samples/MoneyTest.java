package com.progressoft.samples;


import org.junit.Test;

import static com.progressoft.samples.Money.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MoneyTest {
    @Test
    public void timesTest() {
        assertThrows(IllegalArgumentException.class, () -> OnePiaster.times(-1));
        assertEquals(Zero, OnePiaster.times(0));
        assertEquals(OneDinar, OnePiaster.times(100));
        assertEquals(OneDinar, FivePiasters.times(20));
        assertEquals(OneDinar, FivePiasters.times(20));
        assertEquals(OneDinar, FivePiasters.times(20));
        assertEquals(OneDinar, TenPiasters.times(10));
        assertEquals(OneDinar, TwentyFivePiasters.times(4));
        assertEquals(OneDinar, FiftyPiasters.times(2));
        assertEquals(TenDinars, OneDinar.times(10));
    }

    @Test
    public void plusTest() {
        assertEquals(Zero, Zero.plus(Zero));
        assertEquals(OnePiaster, OnePiaster.plus(Zero));
        assertEquals(FiftyPiasters, TwentyFivePiasters.plus(TwentyFivePiasters));
        assertEquals(OneDinar, FiftyPiasters.plus(FiftyPiasters));
        assertEquals(TenDinars, FiveDinars.plus(FiveDinars));
        assertEquals(TwentyDinars, TenDinars.plus(TenDinars));
        assertEquals(FiftyDinars, TenDinars.times(4).plus(TenDinars));
    }

    @Test
    public void sumTest() {
        assertEquals(Zero, sum());
        assertEquals(Zero, sum(Zero));
        assertEquals(Zero, sum(Zero, Zero));
        assertEquals(OnePiaster, sum(OnePiaster));
        assertEquals(FivePiasters, sum(OnePiaster, OnePiaster, OnePiaster, OnePiaster, OnePiaster));
        assertEquals(TenPiasters, sum(FivePiasters, FivePiasters));
        assertEquals(TwentyFivePiasters, sum(TenPiasters, TenPiasters, FivePiasters));
        assertEquals(FiftyPiasters, sum(TenPiasters, TenPiasters, TenPiasters, TenPiasters, TenPiasters));
        assertEquals(FiftyPiasters, sum(TwentyFivePiasters, TwentyFivePiasters));
    }

    @Test
    public void minusTest() {
        assertThrows(IllegalArgumentException.class, () -> Zero.minus(OneDinar));
        assertEquals(Zero, Zero.minus(Zero));
        assertEquals(Zero, OnePiaster.minus(OnePiaster));
        assertEquals(OneDinar, FiveDinars.plus(OneDinar).minus(FiveDinars));
        assertThrows(IllegalArgumentException.class, () -> TenDinars.minus(OneDinar));
        assertEquals(FiveDinars, OneDinar.times(10).minus(FiveDinars));
    }

    @Test
    public void complexTest() {
        Money oneHundredsFifty = FiftyDinars.times(2)
                .plus(FiveDinars.times(5))
                .plus(TwentyDinars)
                .plus(OneDinar.times(5));
        assertEquals(150, oneHundredsFifty.amount(), 2);
        Money seventyThree = FiftyDinars.plus(TwentyDinars).plus(OneDinar.times(3));
        Money seventySeven = oneHundredsFifty.minus(seventyThree);
        assertEquals(77, seventySeven.amount(), 2);
        assertEquals(TenDinars.times(7).plus(OneDinar.times(7)), seventySeven);
    }

    @Test
    public void amountTest() {
        assertEquals(0, Zero.amount(), 2);
        assertEquals(0.01, OnePiaster.amount(), 2);
        assertEquals(0.05, FivePiasters.amount(), 2);
        assertEquals(0.10, TenPiasters.amount(), 2);
        assertEquals(0.25, TwentyFivePiasters.amount(), 2);
        assertEquals(0.50, FiftyPiasters.amount(), 2);
        assertEquals(1.00, OneDinar.amount(), 2);
        assertEquals(5.00, FiveDinars.amount(), 2);
        assertEquals(10.00, TenDinars.amount(), 2);
        assertEquals(20.00, TwentyDinars.amount(), 2);
        assertEquals(50.00, FiftyDinars.amount(), 2);
    }

    @Test
    public void toStringTest() {
        assertEquals("0.00", Zero.toString());
        assertEquals("0.01", OnePiaster.toString());
        assertEquals("0.05", FivePiasters.toString());
        assertEquals("0.10", TenPiasters.toString());
        assertEquals("0.25", TwentyFivePiasters.toString());
        assertEquals("0.50", FiftyPiasters.toString());
        assertEquals("1.00", OneDinar.toString());
        assertEquals("5.00", FiveDinars.toString());
        assertEquals("10.00", TenDinars.toString());
        assertEquals("20.00", TwentyDinars.toString());
        assertEquals("50.00", FiftyDinars.toString());
        assertEquals("0.76", FiftyPiasters.plus(TwentyFivePiasters).plus(OnePiaster).toString());
    }

    /**
     * Externals test cases to enhance coverage quality.
     */
    @Test
    public void supplementaryTest() {
        // Tests if the 'minus' method can successfully dispatch multiple banknote types when necessary
        //
        // i.e. The vending machine has 1 of five dinars and 8 of one dinar, if a change of 10 of one dinar is
        // requested, can the machine return 1 of five dinars and 5 of one dinar? (since a 1 of ten dinars is
        // unavailable)
        assertEquals(OneDinar.times(3),
                FiveDinars.plus(OneDinar.times(8))
                        .minus(TenDinars)
        );

        // Tests if the 'minus' method can successfully substitute smaller denomination banknotes for larger ones when
        // exact change is unavailable.
        //
        // i.e. The vending machine has 1 of five dinars and 2 of ten piasters and 1 of five piasters and 1 of one
        // piaster, if a change of 5 of five piasters and 1 of one piaster is requested, can the machine return 2 of ten
        // piasters and 1 of five piasters and 1 of one piaster? (since a 5 of five piasters is unavailable)
        assertEquals(
                FiveDinars,
                FiveDinars.plus(TenPiasters.times(2)).plus(FivePiasters).plus(OnePiaster)
                        .minus(FivePiasters.times(5).plus(OnePiaster))
        );

        // Tests if the 'minus' method can successfully substitute larger denomination banknotes for smaller ones when
        // exact change is unavailable.
        //
        // i.e. The vending machine has 1 of five dinars and 5 of five piasters and 1 of one piaster, if a change of 2
        // of ten piasters and 1 of five piasters and 1 of one piaster is requested, can the machine return 5 of five
        // piasters and 1 of one piaster? (since a 5 of five piasters is unavailable)
        assertEquals(
                FiveDinars,
                FiveDinars.plus(FivePiasters.times(5)).plus(OnePiaster)
                        .minus(TenPiasters.times(2).plus(FivePiasters).plus(OnePiaster))
        );

        // Tests if the 'minus' method can successfully substitute smaller denomination banknotes for larger ones when
        // exact change is unavailable.
        //
        // i.e. The vending machine has 1 of five dinars and 5 of five piasters and 1 of one piaster, if a change of 2
        // of ten piasters and 1 of five piasters and 1 of one piaster is requested, can the machine return 5 of five
        // piasters and 1 of one piaster? (since a 5 of five piasters is unavailable)
        assertEquals(
                Zero,
                TwentyFivePiasters
                        .minus(TenPiasters.plus(TenPiasters).plus(FivePiasters))
        );

        // Tests if the 'minus' method can successfully substitute larger denomination banknotes for smaller ones when
        // exact change is unavailable.
        //
        // i.e. The vending machine has 1 of five dinars and 5 of five piasters and 1 of one piaster, if a change of 2
        // of ten piasters and 1 of five piasters and 1 of one piaster is requested, can the machine return 5 of five
        // piasters and 1 of one piaster? (since a 5 of five piasters is unavailable)
        assertEquals(
                Zero,
                TenPiasters.plus(TenPiasters).plus(FivePiasters)
                        .minus(TwentyFivePiasters)
        );
    }
}