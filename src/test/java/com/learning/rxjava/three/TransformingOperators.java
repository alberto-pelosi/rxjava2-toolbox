package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

@MicronautTest
public class TransformingOperators {

    /**
     * For a given Observable<T>, the map() operator will transform a T emission into an R
     * emission using the provided Function<T,R> lambda
     */
    @Test
    void map() {
        Observable.just("a", "lol").map(String::length).subscribe(System.out::println);
    }

    /**
     * Map-like operator that casts elements
     */
    @Test
    void cast() {
    }

    /**
     * Append an element on the left (head)
     * See also  startWithArray()
     */
    @Test
    void startsWith() {
        Observable.just("1", "2", "3").startWith("0").subscribe(System.out::println);
    }

    @Test
    void defaultOrSwitchIfEmpty() {
        //default value
        Observable.empty().defaultIfEmpty("default").subscribe(System.out::println);
        //default Observable
        Observable.empty().switchIfEmpty(Observable.just("default")).subscribe(System.out::println);
    }

    /**
     * If you have a finite Observable<T> emitting items that implement Comparable<T>, you
     * can use sorted() to sort the emissions. Internally, it will collect all the emissions and then
     * re-emit them in their sorted order.
     * <p>
     * Of course, this can have some performance implications as it will collect all emissions in
     * memory before emitting them again. If you use this against an infinite Observable, you
     * may get an OutOfMemory error.
     */
    @Test
    void sorted() {
        Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                .sorted()
                .subscribe(System.out::println);

        Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                .sorted(Comparator.reverseOrder())
                .subscribe(System.out::println);

        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                "Epsilon")
                .sorted((x, y) -> Integer.compare(x.length(), y.length()))
                .subscribe(System.out::println);
    }

    /**
     * We can postpone emissions using the delay() operator. It will hold any received emissions
     * and delay each one for the specified time period
     */
    @Test
    void delay() {

    }

    /**
     * The repeat() operator will repeat subscription upstream after onComplete() a specified
     * number of times.
     * For instance, we can repeat the emissions twice for a given Observable by passing a long 2
     * as an argument for repeat()
     */
    @Test
    void repeat() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                "Epsilon").repeat(2)
                .subscribe(s -> System.out.println("Received: " + s));
    }

    /**
     * The scan() method is a rolling aggregator. For every emission, you add it to an
     * accumulation
     */
    @Test
    void scan() {
        Observable.just(1, 2, 3, 4, 5).scan(0, (acc, next) -> acc + next).subscribe(System.out::println);
    }
}
