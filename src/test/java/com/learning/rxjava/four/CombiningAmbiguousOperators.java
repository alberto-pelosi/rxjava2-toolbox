package com.learning.rxjava.four;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class CombiningAmbiguousOperators {

    /**
     * The Observable.amb() factory (amb stands for ambiguous) will accept an
     * Iterable<Observable<T>> and emit the emissions of the first Observable that emits,
     * while the others are disposed of. The first Observable with an emission is the one whose
     * emissions go through. This is helpful when you have multiple sources for the same data or
     * events and you want the fastest one to win.
     * <p>
     * Here, we have two interval sources and we combine them with the Observable.amb()
     * factory. If one emits every second while the other every 300 milliseconds, the latter is going
     * to win because it will emit first
     */
    @Test
    void amb() {
        Observable<String> source1 =
                Observable.interval(1, TimeUnit.SECONDS)
                        .take(2)
                        .map(l -> l + 1) // emit elapsed seconds
                        .map(l -> "Source1: " + l + " seconds");
        //emit every 300 milliseconds
        Observable<String> source2 = Observable.interval(300, TimeUnit.MILLISECONDS).map(l -> (l + 1) * 300)
// emit elapsed milliseconds
                .map(l -> "Source2: " + l + "milliseconds");
        //emit Observable that emits first
        Observable.amb(Arrays.asList(source1, source2))
                .subscribe(i -> System.out.println("RECEIVED: " +
                        i));
        //keep application alive for 5 seconds
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
