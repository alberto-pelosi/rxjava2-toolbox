package com.learning.rxjava.six;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

@MicronautTest
public class Concurrency {

    /**
     * Note how both Observables fire emissions slowly as each one is slowed by 0-3 seconds in
     * the map() operation. More importantly, note how the first Observable firing Alpha, Beta,
     * Gamma must finish first and call onComplete() before firing the second Observable
     * emitting the numbers 1 through 6. If we fire both Observables at the same time rather than
     * waiting for one to complete before starting the other, we could get this operation done
     * much more quickly.
     */
    @Test
    void nonConcurrentExample() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                "Epsilon")
                .map(s -> intenseCalculation((s)))
                .subscribe(System.out::println);
        Observable.range(1, 6)
                .map(s -> intenseCalculation((s)))
                .subscribe(System.out::println);
    }

    /**
     * We can achieve this using the subscribeOn() operator, which suggests to the source to
     * fire emissions on a specified Scheduler. In this case, let us use
     * Schedulers.computation(), which pools a fixed number of threads appropriate for
     * computation operations. It will provide a thread to push emissions for each Observer.
     */
    @Test
    void subscribeOn() throws InterruptedException {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                "Epsilon")
                .subscribeOn(Schedulers.computation())
                .map(s -> intenseCalculation((s)))
                .subscribe(System.out::println);
        Observable.range(1,6)
                .subscribeOn(Schedulers.computation())
                .map(s -> intenseCalculation((s)))
                .subscribe(System.out::println);
        sleep(20000);
    }

    /**
     * When you start making reactive applications concurrent, a subtle
     * complication can creep in. By default, a non-concurrent application will
     * have one thread doing all the work from the source to the final Observer.
     * But having multiple threads can cause emissions to be produced faster
     * than an Observer can consume them (for instance, the zip() operator
     * may have one source producing emissions faster than the other). This can
     * overwhelm the program and memory can run out as backlogged
     * emissions are cached by certain operators. When you are working with a
     * high volume of emissions (more than 10,000) and leveraging concurrency,
     * you will likely want to use Flowables instead of Observables
     *
     * @throws InterruptedException
     */
    @Test
    void subscribeOnAndMerge() throws InterruptedException {
        Observable<String> source1 =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon")
                        .subscribeOn(Schedulers.computation())
                        .map(s -> intenseCalculation((s)));
        Observable<Integer> source2 =
                Observable.range(1,6)
                        .subscribeOn(Schedulers.computation())
                        .map(s -> intenseCalculation((s)));
        Observable.zip(source1, source2, (s,i) -> s + "-" + i)
                .subscribe(System.out::println);
        sleep(20000);
    }

    /**
     * Long task simulator
     *
     * @param value
     * @param <T>
     * @return value
     */
    public static <T> T intenseCalculation(T value) {
        try {
            sleep(ThreadLocalRandom.current().nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}
