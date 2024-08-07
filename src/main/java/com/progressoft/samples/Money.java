package com.progressoft.samples;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Represents monetary values with count, can store multiple banknote types with indefinite count
 * (restricted to{@link Integer} constraints).
 * <p>
 * This class internally uses {@link BigDecimal} for precise decimal point representations (due to sensitive monetary
 * data) and {@link TreeMap} to maintain a sorted order of banknotes (based on the default compareTo method within
 * {@link BigDecimal}).
 * <p>
 */
public class Money {
    // Define a list of predetermined monetary values
    public static final Money Zero = new Money(00.00, 1);
    public static final Money OnePiaster = new Money(00.01, 1);
    public static final Money FivePiasters = new Money(00.05, 1);
    public static final Money TenPiasters = new Money(00.10, 1);
    public static final Money TwentyFivePiasters = new Money(00.25, 1);
    public static final Money FiftyPiasters = new Money(00.50, 1);
    public static final Money OneDinar = new Money(01.00, 1);
    public static final Money FiveDinars = new Money(05.00, 1);
    public static final Money TenDinars = new Money(10.00, 1);
    public static final Money TwentyDinars = new Money(20.00, 1);
    public static final Money FiftyDinars = new Money(50.00, 1);

    private final TreeMap<BigDecimal, Integer> banknotes;

    /**
     * Constructs a new Money object with a specified banknote value and count.
     *
     * @param value The value of the banknote
     * @param count The number of banknotes
     * @throws IllegalArgumentException If either value or count is negative
     */
    private Money(double value, int count) {
        if (value < 0) {
            throw new IllegalArgumentException("Argument value must be a non-negative double");
        }

        if (count < 0) {
            throw new IllegalArgumentException("Argument count must be a non-negative integer");
        }

        this.banknotes = new TreeMap<>();
        this.banknotes.put(BigDecimal.valueOf(value), count);
    }

    /**
     * Constructs a new Money object given a map of banknote values and their respective count.
     *
     * @param banknotes A map having the key as the banknote monetary value and the value as the respective banknote
     *                  counts
     */
    private Money(Map<BigDecimal, Integer> banknotes) {
        this.banknotes = new TreeMap<>();
        this.banknotes.putAll(banknotes);
    }

    /**
     * Calculates the sum of the values for a given map.
     *
     * @param map The map to calculate the sum of the values
     * @return The total value as a {@link BigDecimal}
     */
    private BigDecimal getMapValuesSum(Map<BigDecimal, Integer> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total amount for this Money instance.
     *
     * @return the total amount as a double
     */
    public double amount() {
        return getMapValuesSum(banknotes).doubleValue();
    }

    /**
     * Multiplies the monetary value of this Money instance by a specified number.
     *
     * @param count The multiplier
     * @return A new {@link Money} instance with the result of the multiplication
     * @throws IllegalArgumentException If count is negative
     */
    public Money times(int count) {
        Map<BigDecimal, Integer> result = new TreeMap<>(banknotes);

        if (count < 0) {
            throw new IllegalArgumentException("Argument count must be a non-negative integer");
        }

        result.replaceAll((k, v) -> v * count);

        return new Money(result);
    }

    /**
     * Sums multiple Money instances.
     *
     * @param items The Money instances to sum to
     * @return A new {@link Money} Instance representing the total sum of all provided money objects
     */
    public static Money sum(Money... items) {
        return Arrays.stream(items).reduce(Money.Zero, Money::plus);
    }

    /**
     * Sums two Money instances.
     *
     * @param other The Money instance to add to
     * @return a new {@link Money} Instance representing the sum of the money param object with this object
     */
    public Money plus(Money other) {
        Map<BigDecimal, Integer> result = new TreeMap<>(banknotes);

        other.banknotes.forEach((key, value) -> result.merge(key, value, Integer::sum));

        return new Money(result);
    }

    /**
     * Subtracts two Money instances.
     * <p>
     * Unlike {@link Money#minusComplex(Money)}, this method relaxes the constraint where the banknotes 'other' has to
     * be exact, that is, 'other' is treated as a single decimal value rather than a collection of banknotes.
     *
     * @param other The Money instance to subtract from
     * @return A new {@link Money} Instance representing the subtraction of the money param object from this object
     * @throws IllegalArgumentException If there is insufficient change to perform the subtraction
     */
    public Money minus(Money other) {
        Map<BigDecimal, Integer> result = new TreeMap<>(banknotes);

        // Get the numeric value of 'other' (The type of banknotes received should be irrelevant)
        BigDecimal totalDeductible = BigDecimal.valueOf(other.amount());

        // Iterator starting from the largest banknote
        for (Map.Entry<BigDecimal, Integer> entry : this.banknotes.descendingMap().entrySet()) {
            if (entry.getKey().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            int remainder = totalDeductible.divideAndRemainder(entry.getKey())[0].intValue();
            int deductibleBanknotes = min(remainder, entry.getValue());


            // Update returned result
            result.put(entry.getKey(), entry.getValue() - deductibleBanknotes);

            // Update deductible value by the reducing its value in concurrence to the previous partial coverage
            totalDeductible = totalDeductible.subtract(
                    entry.getKey().multiply(BigDecimal.valueOf(deductibleBanknotes))
            );
        }

        if (totalDeductible.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Could not perform deduction; insufficient change");
        }

        return new Money(result);
    }

    /**
     * Subtracts two Money instances.
     *
     * @param other The Money instance to subtract from
     * @return A new {@link Money} Instance representing the subtraction of the money param object from this object
     * @throws IllegalArgumentException If there is insufficient change to perform the subtraction
     * @deprecated Use {@link Money#minus(Money)} minus(Money other) instead
     */
    @Deprecated
    public Money minusComplex(Money other) {
        Map<BigDecimal, Integer> result = new TreeMap<>(banknotes);

        // Directly deduct the 'banknotes' for both money objects, negative map values needs to be properly handled
        other.banknotes.forEach((key, value) -> result.merge(key, -value, Integer::sum));

        // Fetch banknotes whose values are negative
        Map<BigDecimal, Integer> unavailableBanknotes = result.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Iterate through negative entries (required banknotes that are unavailable to directly deduct)
        for (Map.Entry<BigDecimal, Integer> entry : unavailableBanknotes.entrySet()) {
            // The banknotes that satisfy the requirement (whose total value is exact to the missing banknotes)
            Map<BigDecimal, Integer> substituteBanknotes = new TreeMap<>();

            // The amount the needs to be satisfied
            BigDecimal unavailableAmount = entry.getKey().multiply(BigDecimal.valueOf(abs(entry.getValue())));

            // Iterator starting from the largest banknote
            for (Map.Entry<BigDecimal, Integer> entry1 : banknotes.descendingMap().entrySet()) {
                // Defensively checking for zero banknote values to prevent possible arithmetic exceptions
                if (entry1.getKey().compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }


                // The remaining amount to reach the exact requirement
                BigDecimal remainingAmount = unavailableAmount.subtract(getMapValuesSum(substituteBanknotes));


                // Check if remainingAmount is dividable with the banknote candidate value
                if (entry1.getKey().compareTo(remainingAmount) <= 0) {
                    // Add the candidate banknote for pending substitution (must not exceed its count)
                    substituteBanknotes.put(
                            entry1.getKey(),
                            min(
                                    entry1.getValue(),
                                    remainingAmount.divide(entry1.getKey(), RoundingMode.UNNECESSARY).intValue()
                            )
                    );
                }
            }

            // Checks if the candidate value is exact with unavailable banknotes
            if (getMapValuesSum(substituteBanknotes).compareTo(unavailableAmount) == 0) {
                result.put(entry.getKey(), 0);
                substituteBanknotes.forEach((key, value) -> result.merge(key, -value, Integer::sum));
            } else {
                // No candidate values were found for an unavailable banknotes
                throw new IllegalArgumentException("Could not perform deduction; insufficient change");
            }
        }

        return new Money(result);
    }

    @Override
    public int hashCode() {
        return Double.hashCode(amount());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Money money = (Money) obj;
        return Objects.equals(money.amount(), amount());
    }

    @Override
    public String toString() {
        return String.format("%.2f", amount());
    }
}
