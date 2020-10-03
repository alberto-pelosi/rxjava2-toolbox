package com.learning.rxjava.four;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class CombiningZipOperators {

    @Test
    void zip() {
        Observable<String> o1 = Observable.just("a", "b", "c");
        Observable<String> o2 = Observable.just("1", "2", "3");
        Observable.zip(o1, o2, (a, b) -> a + " - " + b).subscribe(System.out::println);
    }

    /**
     * The Observable.combineLatest() factory is somewhat similar to zip(), but for every
     * emission that fires from one of the sources, it will immediately couple up with the latest
     * emission from every other source. It will not queue up unpaired emissions for each source,
     * but rather cache and pair the latest one
     */
    @Test
    void combineLatest() {
        Observable<Long> source1 =
                Observable.interval(300, TimeUnit.MILLISECONDS);
        Observable<Long> source2 =
                Observable.interval(1, TimeUnit.SECONDS);
        Observable.combineLatest(source1, source2,
                (l1, l2) -> "SOURCE 1: " + l1 + " SOURCE 2: " + l2)
                .subscribe(System.out::println);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Similar to Observable.combineLatest(), but not exactly the same, is the
     * withLatestfrom() operator. It will map each T emission with the latest values from other
     * Observables and combine them, but it will only take one emission from each of the other
     * Observables:
     */
    @Test
    void withLatestFrom() {
        Observable<Long> source1 =
                Observable.interval(300, TimeUnit.MILLISECONDS);
        Observable<Long> source2 =
                Observable.interval(1, TimeUnit.SECONDS);
        source2.withLatestFrom(source1,
                (l1, l2) -> "SOURCE 2: " + l1 + " SOURCE 1: " + l2
        ).subscribe(System.out::println);
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
