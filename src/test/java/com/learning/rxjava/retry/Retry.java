package com.learning.rxjava.retry;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@MicronautTest
public class Retry {

    public static final int EXPONENT_START = 1;
    public static final int RETRY_COUNT = 3;
    public static final int EXPONENTATION_BASE = 2;
    AtomicInteger ai = new AtomicInteger(3);


    @Test
    void infiniteRetry() {
        Observable<List<Integer>> source = Observable.defer(() -> Observable.just(items())).retry();

        source.subscribe(System.out::println, System.out::println);
    }

    @Test
    void finiteRetry() {
        Observable<List<Integer>> source = Observable.defer(() -> Observable.just(items())).retry(1);

        source.subscribe(System.out::println, System.out::println);
    }

    @Test
    void retryWithBackoff() throws InterruptedException {
        Observable<List<Integer>> source = Observable.defer(() -> Observable.just(items()))
                .retryWhen(errors -> errors.zipWith(Observable.range(EXPONENT_START, RETRY_COUNT), (n, i) -> i)
                        .flatMap(retryCount -> Observable.timer((long) Math.pow(EXPONENTATION_BASE, retryCount), TimeUnit.SECONDS)));
        source.subscribe(System.out::println);

        Thread.sleep(15000);
    }

    @Test
    void retry2() throws InterruptedException {
        Observable.just(1,2,10,0,5).map(e -> 10 / e).retryWhen(errors -> errors
                .zipWith(Observable.range(EXPONENT_START, RETRY_COUNT), (n, i) -> i).flatMap(retryCount -> Observable.timer((long) Math.pow(EXPONENTATION_BASE, retryCount), TimeUnit.SECONDS)))
                .subscribe(System.out::println);
        Thread.sleep(150000);
    }

    List<Integer> items() {
        List<Integer> items = new ArrayList<>();
        int counter = ai.decrementAndGet();
        System.out.println(counter);
        if (counter <= 0) counter = 0;
        items.add(1 / (1 - counter));
        items.add(2 / (2 - counter));
        items.add(3);
        items.add(4);
        items.add(5);
        return items;
    }
}
