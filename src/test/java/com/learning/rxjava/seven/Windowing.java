package com.learning.rxjava.seven;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class Windowing {

    /**
     * The window() operators are almost identical to buffer(), except that they buffer into
     * other Observables rather than collections. This results in an
     * Observable<Observable<T>> that emits Observables.
     * <p>
     * The window() operator is also convenient to work with if you want to use operators to
     * transform each batch.
     * Just like buffer(), you can cut-off each batch using fixed sizing, a time interval, or a
     * boundary from another Observable.
     */

    @Test
    void fixedSizeWindowing() {
        Observable.range(1, 50)
                .window(8).flatMapSingle(obs -> obs.reduce("", (total, next) -> total
                + (total.equals("") ? "" : "|") + next))
                .subscribe(System.out::println);
    }

    @Test
    void timeBasedWindowing() throws InterruptedException {
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .window(1, TimeUnit.SECONDS)
                .flatMapSingle(obs -> obs.reduce("", (total,
                                                      next) -> total
                        + (total.equals("") ? "" : "|") + next))
                .subscribe(System.out::println);
        sleep(5000);
    }

    @Test
    void boundaryBasedWindowing() throws InterruptedException {
        Observable<Long> cutOffs =
                Observable.interval(1, TimeUnit.SECONDS);
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .window(cutOffs)
                .flatMapSingle(obs -> obs.reduce("", (total, next) ->
                        total
                                + (total.equals("") ? "" : "|") + next))
                .subscribe(System.out::println);
        sleep(5000);
    }
}
