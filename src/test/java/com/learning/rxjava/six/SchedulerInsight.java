package com.learning.rxjava.six;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

@MicronautTest
public class SchedulerInsight {

    /**
     * Typically in Java, you use an ExecutorService as your thread pool. However, RxJava implements its
     * own concurrency abstraction called Scheduler.
     *
     * For a given Observer, a Scheduler will provide a thread from its pool that
     * will push the emissions. When onComplete() is called, the operation will be disposed of
     * and the thread will be given back to the pool, where it may be persisted and reused by
     * another Observer.
     */

    // SCHEDULER TYPES

    /**
     * COMPUTATION
     * <p>
     * Schedulers.computation(). This will maintain a fixed number of threads based
     * on the processor count available to your Java session, making it appropriate for
     * computational tasks. Computational tasks (such as math, algorithms, and complex logic)
     * may utilize cores to their fullest extent. Therefore, there is no benefit in having more worker
     * threads than available cores to perform such work, and the computational Scheduler will
     * ensure that.
     * ##########
     * IO
     * <p>
     * IO tasks such as reading and writing databases, web requests, and disk storage are less
     * expensive on the CPU and often have idle time waiting for the data to be sent or come back.
     * This means you can create threads more liberally, and Schedulers.io() is appropriate for
     * this. It will maintain as many threads as there are tasks and will dynamically grow, cache,
     * and reduce the number of threads as needed.
     * ##########
     * New Thread
     * <p>
     * The Schedulers.newThread() factory will return a Scheduler that does not pool
     * threads at all. It will create a new thread for each Observer and then destroy the thread
     * when it is done. This is different than Schedulers.io() because it does not attempt to
     * persist and cache threads for reuse.
     * This may be helpful in cases where you want to create, use, and then destroy a thread
     * immediately so it does not take up memory. But for complex applications generally, you
     * will want to use Schedulers.io()
     * ##########
     * Single
     * <p>
     * When you want to run tasks sequentially on a single thread, you can invoke
     * Schedulers.single(). This is backed by a single-threaded implementation appropriate
     * for event looping. It can also be helpful to isolate fragile, non-threadsafe operations to a
     * single thread
     * ##########
     * Trampoline
     * <p>
     * Schedulers.trampoline() is an interesting Scheduler. In practicality, you will not
     * invoke it often as it is used primarily in RxJava's internal implementation. Its pattern is also
     * borrowed for UI Schedulers such as RxJavaFX and RxAndroid. It is just like default
     * scheduling on the immediate thread, but it prevents cases of recursive scheduling where a
     * task schedules a task while on the same thread. Instead of causing a stack overflow error, it
     * will allow the current task to finish and then execute that new scheduled task afterward.
     * ##########
     * <p>
     * Executor Service
     * <p>
     * You can build a Scheduler off a standard Java ExecutorService. You may choose to do
     * this in order to have more custom and fine-tuned control over your thread management
     * policies. For example, say, we want to create a Scheduler that uses 20 threads. We can create
     * a new fixed ExecutorService specified with this number of threads. Then, you can wrap
     * it inside a Scheduler implementation by calling Schedulers.from()
     */

    /**
     * Having multiple Observers to the same Observable with subscribeOn() will result in
     * each one getting its own thread (or have them waiting for an available thread if none are
     * available). In the Observer, you can print the executing thread's name by calling
     * Thread.currentThread().getName()
     *
     * @throws InterruptedException
     */
    @Test
    void understandSubscribeOn1() throws InterruptedException {
        Observable<Integer> lengths =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon")
                        .subscribeOn(Schedulers.computation())
                        .map(Concurrency::intenseCalculation)
                        .map(String::length);
        lengths.subscribe(i ->
                System.out.println("Received " + i + " on thread " +
                        Thread.currentThread().getName()));
        lengths.subscribe(i ->
                System.out.println("Received " + i + " on thread " +
                        Thread.currentThread().getName()));
        sleep(10000);
    }

    /**
     * if we want only one thread to serve both Observers, we can multicast this operation
     */
    @Test
    void understandSubscribeOn2() throws InterruptedException {
        Observable<Integer> lengths =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon")
                        .subscribeOn(Schedulers.computation())
                        .map(SchedulerInsight::intenseCalculation)
                        .map(String::length)
                        .publish()
                        .autoConnect(2);
        lengths.subscribe(i ->
                System.out.println("Received " + i + " on thread " +
                        Thread.currentThread().getName()));
        lengths.subscribe(i ->
                System.out.println("Received " + i + " on thread " +
                        Thread.currentThread().getName()));
        sleep(10000);
    }

    @Test
    void SubscribeOn3() throws InterruptedException {
        Observable.fromCallable(() ->
                getResponse("https://api.github.com/users/thomasnield/starred")
        ).subscribeOn(Schedulers.io())
                .subscribe(System.out::println);
        sleep(10000);
    }

    /**
     * It is important to note that subscribeOn() will have no practical effect with certain
     * sources (and will keep a worker thread unnecessarily on standby until that operation
     * terminates). This might be because these Observables already use a specific Scheduler,
     * and if you want to change it, you can provide a Scheduler as an argument. For
     * example, Observable.interval() will use Schedulers.computation() and will
     * ignore any subscribeOn()you specify otherwise.
     */


    /**
     * The subscribeOn() operator instructs the source Observable which Scheduler to emit
     * emissions on. If subscribeOn() is the only concurrent operation in an Observable chain,
     * the thread from that Scheduler will work the entire Observable chain, pushing emissions
     * from the source all the way to the final Observer. The observeOn() operator, however,
     * will intercept emissions at that point in the Observable chain and switch them to a
     * different Scheduler going forward.
     * Unlike subscribeOn(), the placement of observeOn() matters. It will leave all operations
     * upstream on the default or subscribeOn()-defined Scheduler, but will switch to a
     * different Scheduler downstream. Here, I can have an Observable emit a series of strings
     * that are /-separated values and break them up on an IO Scheduler. But after that, I can
     * switch to a computation Scheduler to filter only numbers and calculate their sum, as
     * shown in the following code snippet:
     *
     * You might use observeOn() for a situation like the one emulated earlier. If you want to
     * read one or more data sources and wait for the response to come back, you will want to do
     * that part on Schedulers.io() and will likely leverage subscribeOn() since that is the
     * initial operation. But once you have that data, you may want to do intensive computations
     * with it, and Scheduler.io() may no longer be appropriate. You will want to constrain
     * these operations to a few threads that will fully utilize the CPU. Therefore, you use
     * observeOn() to redirect data to Schedulers.computation()
     */
    @Test
    void observeOn() throws InterruptedException {
//Happens on IO Scheduler
        Observable.just("WHISKEY/27653/TANGO", "6555/BRAVO",
                "232352/5675675/FOXTROT")
                .subscribeOn(Schedulers.io())
                .flatMap(s -> Observable.fromArray(s.split("/")))
                //Happens on Computation Scheduler
                .observeOn(Schedulers.computation())
                .filter(s -> s.matches("[0-9]+"))
                .map(Integer::valueOf)
                .reduce((total, next) -> total + next)
                .subscribe(i -> System.out.println("Received " + i
                        + " on thread "
                        + Thread.currentThread().getName()));
        sleep(1000);
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

    private static String getResponse(String path) {
        try {
            return new Scanner(new URL(path).openStream(),
                    "UTF-8").useDelimiter("\\A").next();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
