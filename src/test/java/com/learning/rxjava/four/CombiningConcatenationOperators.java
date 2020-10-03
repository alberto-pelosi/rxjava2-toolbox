package com.learning.rxjava.four;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

@MicronautTest
public class CombiningConcatenationOperators {

    /**
     * Concatenation is remarkably similar to merging, but with an important nuance: it will fire
     * elements of each provided Observable sequentially and in the order specified.
     * However, it is often a poor choice for infinite Observables, as an infinite Observable will
     * indefinitely hold up the queue.
     * <p>
     * You can also use the concatWith()
     */
    @Test
    void concat() {

    }

    /**
     * Just as there is flatMap(), which dynamically merges Observables derived off each
     * emission, there is a concatenation counterpart called concatMap(). You should prefer this
     * operator if you care about ordering and want each Observable mapped from each
     * emission to finish before starting the next one. More specifically, concatMap() will merge
     * each mapped Observable sequentially and fire it one at a time. It will only move to the
     * next Observable when the current one calls onComplete().
     * <p>
     * Again, it is unlikely that you will ever want to use concatMap() to map to infinite
     * Observables. As you can guess, this would result in subsequent Observables never firing.
     * You will likely want to use flatMap() instead
     */
    @Test
    void concatMap() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.concatMap(s -> Observable.fromArray(s.split(""))).subscribe(System.out::println);
    }
}
