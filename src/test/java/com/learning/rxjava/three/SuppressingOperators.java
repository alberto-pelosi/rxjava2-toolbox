package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@MicronautTest
public class SuppressingOperators {

    @Test
    void filter() {
        Observable.just("a", "b", "delta").filter(s -> s.length() > 2).subscribe(System.out::println);
    }

    @Test
    void take() {
        Observable.just("a", "b", "delta", "a").take(2).subscribe(System.out::println);
        Observable.interval(1, TimeUnit.SECONDS).take(3, TimeUnit.SECONDS).subscribe(System.out::println);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void skip() {
        Observable.range(0, 100).skip(90).subscribe(System.out::println);
    }

    /**
     * Like take but with predicate
     */
    @Test
    void takeWhile() {
    }

    /**
     * Like skip but with predicate
     */
    @Test
    void skipWhile() {
    }

    /**
     * Keep in mind that if you have a wide, diverse spectrum of unique values, distinct() can
     * use a bit of memory. Imagine that each subscription results in a HashSet that tracks
     * previously captured unique values.
     */
    @Test
    void distinct() {
        Observable.just("1", "2", "3", "s").distinct().subscribe(System.out::println);
    }

    @Test
    void distinctUntilChanged() {
        Observable.just(1, 2, 2, 2, 3, 4, 5).distinctUntilChanged().subscribe(System.out::println);
    }

    @Test
    void elementAt() {
        Observable.just(1, 2, 3, 4).elementAt(3).subscribe(System.out::println);
    }
}
