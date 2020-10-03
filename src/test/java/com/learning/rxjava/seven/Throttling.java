package com.learning.rxjava.seven;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class Throttling {

    /**
     * The buffer() and window() operators batch up emissions into collections or Observables
     * based on a defined scope, which regularly consolidates rather than omits
     * emissions.The throttle() operator, however, omits emissions when they occur rapidly.
     * This is helpful when rapid emissions are assumed to be redundant or unwanted, such as a
     * user clicking on a button repeatedly. For these situations, you can use the
     * throttleLast(), throttleFirst(), and throttleWithTimeout() operators to only let
     * the first or last element in a rapid sequence of emissions through. How you choose one of
     * the many rapid emissions is determined by your choice of operator, parameters, and
     * arguments.
     */

    @Test
    void throttleLast() throws InterruptedException {
        Observable<String> source1 = Observable.interval(100,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 100) // map to elapsed time
                .map(i -> "SOURCE 1: " + i)
                .take(10);
        Observable<String> source2 = Observable.interval(300,
                TimeUnit.MILLISECONDS).map(i -> (i + 1) * 300) // map to elapsed time
                .map(i -> "SOURCE 2: " + i)
                .take(3);
        Observable<String> source3 = Observable.interval(2000,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 2000) // map to elapsed time
                .map(i -> "SOURCE 3: " + i)
                .take(2);
        Observable.concat(source1, source2, source3).throttleLast(1, TimeUnit.SECONDS)
                .subscribe(System.out::println);
        sleep(6000);
    }

    @Test
    void throttleFirst() throws InterruptedException {
        Observable<String> source1 = Observable.interval(100,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 100) // map to elapsed time
                .map(i -> "SOURCE 1: " + i)
                .take(10);
        Observable<String> source2 = Observable.interval(300,
                TimeUnit.MILLISECONDS).map(i -> (i + 1) * 300) // map to elapsed time
                .map(i -> "SOURCE 2: " + i)
                .take(3);
        Observable<String> source3 = Observable.interval(2000,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 2000) // map to elapsed time
                .map(i -> "SOURCE 3: " + i)
                .take(2);
        Observable.concat(source1, source2, source3).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(System.out::println);
        sleep(6000);
    }

    /**
     * throttleWithTimout() (also called debounce()) accepts time interval arguments that
     * specify how long a period of inactivity (which means no emissions are coming from the
     * source) must be before the last emission can be pushed forward. I
     */
    @Test
    void throttleWithTimeout() throws InterruptedException {
        Observable<String> source1 = Observable.interval(100,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 100) // map to elapsed time
                .map(i -> "SOURCE 1: " + i)
                .take(10);
        Observable<String> source2 = Observable.interval(300,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .map(i -> "SOURCE 2: " + i)
                .take(3);
        Observable<String> source3 = Observable.interval(2000,
                TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 2000) // map to elapsed time
                .map(i -> "SOURCE 3: " + i)
                .take(2);
        Observable.concat(source1, source2, source3)
                .throttleWithTimeout(1, TimeUnit.SECONDS).subscribe(System.out::println);
        sleep(6000);
    }
}
