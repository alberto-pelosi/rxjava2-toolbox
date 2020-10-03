package com.learning.rxjava.seven;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class Switching {

    /**
     * , there is a powerful operator called switchMap(). Its usage feels like flatMap(),
     * but it has one important behavioral difference: it will emit from the latest Observable
     * derived from the latest emission and dispose of any previous Observables that were
     * processing. In other words, it allows you to cancel an emitting Observable and switch to a
     * new one, preventing stale or redundant processing
     */
    @Test
    void concatMap() throws InterruptedException {
        Observable<String> items = Observable.just("Alpha", "Beta",
                "Gamma", "Delta", "Epsilon",
                "Zeta", "Eta", "Theta", "Iota");
        //delay each String to emulate an intense calculation
        Observable<String> processStrings = items.concatMap(s ->
                Observable.just(s)
                        .delay(randomSleepTime(),
                                TimeUnit.MILLISECONDS)
        );
        //run processStrings every 5 seconds, and kill each
        //previous instance to start next
        Observable.interval(5, TimeUnit.SECONDS)
                .switchMap(i ->
                        processStrings
                                .doOnDispose(() ->
                                        System.out.println("Disposing! Starting next"))
                ).subscribe(System.out::println);
        //keep application alive for 20 seconds
        sleep(20000);
    }

    public static int randomSleepTime() {
        //returns random sleep time between 0 to 2000 milliseconds
        return ThreadLocalRandom.current().nextInt(2000);
    }
}
