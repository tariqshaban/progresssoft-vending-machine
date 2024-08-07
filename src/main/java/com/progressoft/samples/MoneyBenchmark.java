package com.progressoft.samples;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.progressoft.samples.Money.*;


public class MoneyBenchmark {

    /**
     * Benchmarks the performance of the optimized {@link Money#minus(Money)} method.
     * <p>
     * The benchmark first allocates 500k of one piaster (equivalent to 5k dinars), and attempts to call
     * {@link Money#minus(Money)} multiple times for a thousand times.
     * <p>
     * The benchmark consists of two main operations:
     * <ul>
     *   <li>Invoke {@link Money#minus(Money)} several times</li>
     *   <li>Invoke {@link Money#minus(Money)} one time, however, which multiple unique banknotes within</li>
     * </ul>
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1)
    @Warmup(iterations = 5, time = 1)
    public void benchmarkMinus() {
        Money initial = OnePiaster.times(500_000);
        for (int _ : IntStream.range(0, 1000).boxed().collect(Collectors.toList())) {
            initial = initial.minus(OnePiaster)
                    .minus(FivePiasters)
                    .minus(TenPiasters)
                    .minus(TwentyFivePiasters)
                    .minus(FiftyPiasters);

            initial = initial.minus(
                    OnePiaster
                            .plus(FivePiasters)
                            .plus(TenPiasters)
                            .plus(TwentyFivePiasters)
                            .plus(FiftyPiasters)
            );
        }
    }

    /**
     * Benchmarks the performance of the deprecated {@link Money#minusComplex(Money)} method.
     * <p>
     * The benchmark first allocates 500k of one piaster (equivalent to 5k dinars), and attempts to call
     * {@link Money#minus(Money)} multiple times for a thousand times.
     * <p>
     * The benchmark consists of two main operations:
     * <ul>
     *   <li>Invoke {@link Money#minus(Money)} several times</li>
     *   <li>Invoke {@link Money#minus(Money)} one time, however, which multiple unique banknotes within</li>
     * </ul>
     */
    @SuppressWarnings("deprecation")
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 20, time = 1)
    @Warmup(iterations = 5, time = 1)
    public void benchmarkMinusComplex() {
        Money initial = OnePiaster.times(500_000);
        for (int _ : IntStream.range(0, 1000).boxed().collect(Collectors.toList())) {
            initial = initial.minusComplex(OnePiaster)
                    .minusComplex(FivePiasters)
                    .minusComplex(TenPiasters)
                    .minusComplex(TwentyFivePiasters)
                    .minusComplex(FiftyPiasters);

            initial = initial.minusComplex(
                    OnePiaster
                            .plus(FivePiasters)
                            .plus(TenPiasters)
                            .plus(TwentyFivePiasters)
                            .plus(FiftyPiasters)
            );
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}