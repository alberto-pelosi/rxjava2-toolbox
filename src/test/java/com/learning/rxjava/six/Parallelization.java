package com.learning.rxjava.six;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.learning.rxjava.six.Concurrency.intenseCalculation;
import static java.lang.Thread.sleep;

@MicronautTest
public class Parallelization {

    /**
     * Parallelization, also called parallelism or parallel computing, is a broad term that can be
     * used for any concurrent activity (including what we covered). But for the purposes of
     * RxJava, let's define it as processing multiple emissions at a time for a given Observable. If
     * we have 1000 emissions to process in a given Observable chain, we might be able to get
     * work done faster if we process eight emissions at a time instead of one. If you recall, the
     * Observable contract dictates that emissions must be pushed serially down an Observable
     * chain and never race each other due to concurrency. As a matter of fact, pushing eight
     * emissions down an Observable chain at a time would be downright catastrophic and
     * wreak havoc.
     * This seems to put us at odds with what we want to accomplish, but thankfully, RxJava gives
     * you enough operators and tools to be clever. While you cannot push items concurrently on
     * the same Observable, you are allowed to have multiple Observables running at once, each
     * having its own single thread pushing items through. As we have done throughout this
     * chapter, we created several Observables running on different threads/schedulers and even
     * combined them. You actually have the tools already, and the secret to achieving
     * parallelization is in the flatMap() operator, which is, in fact, a powerful concurrency
     * operator.
     */

    @Test
    void slowSerialObserver() {
        Observable.range(1, 10)
                .map(i -> intenseCalculation(i))
                .subscribe(i -> System.out.println("Received " + i +
                        " "
                        + LocalTime.now()));
    }

    /**
     * The flatMap() operator will merge multiple Observables derived off each
     * emission even if they are concurrent. If a light bulb has not gone off yet, read on. In
     * flatMap(), let's wrap each emission into Observable.just(), use subscribeOn() to
     * emit it on Schedulers.computation(), and then map it to the intenseCalculation()
     */
    @Test
    void fastParallelObserver() throws InterruptedException {
        Observable.range(1, 10)
                .flatMap(i -> Observable.just(i).subscribeOn(Schedulers.computation()).map(i2 -> intenseCalculation(i2)))
                .subscribe(i -> System.out.println("Received " + i +
                        " "
                        + LocalTime.now() + " on thread "
                        + Thread.currentThread().getName()));
        sleep(20000);
    }

    /**
     * The example here is not necessarily optimal, however. Creating an Observable for each
     * emission might create some unwanted overhead. There is a leaner way to achieve
     * parallelization, although it has a few more moving parts. If we want to avoid creating
     * excessive Observable instances, maybe we should split the source Observable into a
     * fixed number of Observables where emissions are evenly divided and distributed through
     * each one. Then, we can parallelize and merge them with flatMap(). Even better, since I
     * have eight cores on my computer, maybe it would be ideal that I have eight Observables for
     * eight streams of calculations.
     * We can achieve this using a groupBy() trick
     */
    @Test
    void optimizedParallelObserver() throws InterruptedException {
        int coreCount = Runtime.getRuntime().availableProcessors();
        AtomicInteger assigner = new AtomicInteger(0);
        Observable.range(1, 10).groupBy(i -> assigner.incrementAndGet() % coreCount)
                .flatMap(grp -> grp.observeOn(Schedulers.io())
                        .map(i2 -> intenseCalculation(i2))
                )
                .subscribe(i -> System.out.println("Received " + i + " "
                        + LocalTime.now() + " on thread "
                        + Thread.currentThread().getName()));
        sleep(20000);
    }

    /**
     * One last concurrency operator that we need to cover is unsubscribeOn(). When you
     * dispose an Observable, sometimes, that can be an expensive operation depending on the
     * nature of the source. For instance, if your Observable is emitting the results of a database
     * query using RxJava-JDBC, (https://github.com/davidmoten/rxjava-jdbc) it can be
     * expensive to stop and dispose that Observable because it needs to shut down the JDBC
     * resources it is using.
     * This can cause the thread that calls dispose() to become busy.
     *
     * Let's add unsubscribeOn() and specify to unsubscribe on Schedulers.io(). You should
     * put unsubscribeOn() wherever you want all operations upstream to be affected.
     *
     * Now you will see that disposal is being done by the IO Scheduler, whose thread is
     * identified by the name RxCachedThreadScheduler-1. This allows the main thread to
     * kick off disposal and continue without waiting for it to complete.
     */
    @Test
    void unsubscribeOn() throws InterruptedException {
        Disposable d = Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(() -> System.out.println("Disposing on thread "
                                + Thread.currentThread().getName()))
                .unsubscribeOn(Schedulers.io())
                .subscribe(i -> System.out.println("Received " +
                        i));
        sleep(3000);
        d.dispose();
        sleep(3000);
    }

}
