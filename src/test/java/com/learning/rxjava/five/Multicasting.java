package com.learning.rxjava.five;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class Multicasting {

    /**
     * What happens here is that the Observable.range() source will yield two separate
     * emission generators, and each will coldly emit a separate stream for each Observer. Each
     * stream also has its own separate map() instance, hence each Observer gets different
     * random integers
     */
    @Test
    void coldObservable() {
        Observable<Integer> random = Observable.range(1, 3).map(i -> ThreadLocalRandom.current().nextInt(100000));
        random.subscribe(i -> System.out.println("Observer 1 " + i));
        random.subscribe(i -> System.out.println("Observer 2 " + i));
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This occurred because we multicast after Observable.range(), but the multicasting
     * happens before the map() operator. Even though we consolidated to one set of emissions
     * coming from Observable.range(), each Observer is still going to get a separate stream
     * at map()
     */
    @Test
    void hotObservable1() {
        ConnectableObservable<Integer> threeInts = Observable.range(1, 3).publish();

        Observable<Integer> threeRandoms = threeInts.map(i -> ThreadLocalRandom.current().nextInt(100000));

        threeRandoms.subscribe(i -> System.out.println("Observer 1 " + i));
        threeRandoms.subscribe(i -> System.out.println("Observer 2 " + i));

        threeInts.connect();
    }

    /**
     * Observer got the same three random integers, and we have effectively
     * multicast the entire operation right before the two Observers, as shown in the following
     * figure. We now have a single stream instance throughout the entire chain since map() is
     * now behind, not in front, of publish():
     */
    @Test
    void hotObservable2() {
        ConnectableObservable<Integer> threeRandoms = Observable.range(1, 3).map(i -> ThreadLocalRandom.current().nextInt(100000)).publish();

        threeRandoms.subscribe(i -> System.out.println("Observer 1 " + i));
        threeRandoms.subscribe(i -> System.out.println("Observer 2 " + i));

        threeRandoms.connect();
    }

    /**
     * The autoConnect() operator on ConnectableObservable can be quite handy. For a
     * given ConnectableObservable<T>, calling autoConnect() will return an
     * Observable<T> that will automatically call connect() after a specified number of
     * Observers are subscribed. Since our previous example had two Observers, we can
     * streamline it by calling autoConnect(2) immediately after publish().
     * <p>
     * This saved us the trouble of having to save ConnectableObservable and call its
     * connect() method later. Instead, it will start firing when it gets 2 subscriptions, which we
     * have planned and specified as an argument in advance. Obviously, this does not work well
     * when you have an unknown number of Observers and you want all of them to receive all
     * emissions.
     * <p>
     * Note that if you pass no argument for numberOfSubscribers, it will default to 1.
     * <p>
     * If you pass 0 to autoConnect() for the numberOfSubscribers argument, it will start
     * firing immediately and not wait for any Observers. This can be handy to start firing
     * emissions immediately without waiting for any Observers.
     */
    @Test
    void autoConnect() {
        Observable<Integer> threeRandoms = Observable.range(1, 3)
                .map(i -> ThreadLocalRandom.current().nextInt(100000))
                .publish()
                .autoConnect(2);
        //Observer 1 - print each random integer
        threeRandoms.subscribe(i -> System.out.println("Observer 1: " + i));
        //Observer 2 - sum the random integers, then print
        threeRandoms.reduce(0, (total, next) -> total + next)
                .subscribe(i -> System.out.println("Observer 2: " + i));
    }

    /**
     * The refCount() operator on ConnectableObservable is similar to
     * autoConnect(1), which fires after getting one subscription. But there is one important
     * difference; when it has no Observers anymore, it will dispose of itself and start over when a
     * new one comes in. It does not persist the subscription to the source when it has no more
     * Observers, and when another Observer follows, it will essentially "start over"
     */
    @Test
    void refCount() throws InterruptedException {
        Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS).publish().refCount();

        seconds.take(5).subscribe(l -> System.out.println("Observer 1: " + l));
        sleep(3000);

        seconds.take(2).subscribe(l -> System.out.println("Observer 2: " + l));
        sleep(3000);

        seconds.subscribe(l -> System.out.println("Observer 3: " + l));
        sleep(3000);
    }

    /**
     * You can also use an alias for publish().refCount() using the share() operator.
     * This will accomplish the same result
     */
    @Test
    void share() throws InterruptedException {
        Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS).share();

        seconds.take(5).subscribe(l -> System.out.println("Observer 1: " + l));
        sleep(3000);

        seconds.take(2).subscribe(l -> System.out.println("Observer 2: " + l));
        sleep(3000);

        seconds.subscribe(l -> System.out.println("Observer 3: " + l));
        sleep(3000);
    }

    /**
     * The replay() operator is a powerful way to hold onto previous emissions within a certain
     * scope and re-emit them when a new Observer comes in. It will return a
     * ConnectableObservable that will both multicast emissions as well as emit previous
     * emissions defined in a scope. Previous emissions it caches will fire immediately to a new
     * Observer so it is caught up, and then it will fire current emissions from that point forward.
     * <p>
     * Just note that this can get expensive with memory, as replay() will keep
     * caching all emissions it receives. If the source is infinite or you only care about the last
     * previous emissions, you might want to specify a bufferSize argument to limit only
     * replaying a certain number of last emissions.
     */
    @Test
    void replay1() throws InterruptedException {
        Observable<Long> seconds = Observable.interval(1, TimeUnit.SECONDS).replay().autoConnect();
        //Observer 1
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        sleep(3000);
        seconds.subscribe(l -> System.out.println("Observer 2: " + l));
        sleep(3000);
    }

    /**
     * There are other overloads for replay(), particularly a time-based window you can specify.
     * Here, we construct an Observable.interval() that emits every 300 milliseconds and
     * subscribe to it.
     * <p>
     * (Seems it doesn't work with TimeUnit.SECONDS...
     */
    @Test
    void replay2() throws InterruptedException {
        Observable<Long> seconds = Observable.interval(300, TimeUnit.MILLISECONDS).map(l -> (l + 1) * 300)
                // map to elapsed  milliseconds
                .replay(1, TimeUnit.SECONDS)
                .autoConnect();
        //Observer 1
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        sleep(2000);
        seconds.subscribe(l -> System.out.println("Observer 2: " + l));
        sleep(1000);
    }

    /**
     * You can also specify a bufferSize argument on top of a time interval, so only a certain
     * number of last emissions are buffered within that time period. If we modify our example to
     * only replay one emission that occurred within the last second, it should only replay 1800 to
     * Observer 2:
     */
    @Test
    void replay3() throws InterruptedException {
        Observable<Long> seconds =
                Observable.interval(300, TimeUnit.MILLISECONDS)
                        .map(l -> (l + 1) * 300).replay(1, 1, TimeUnit.SECONDS)
                        .autoConnect();
        seconds.subscribe(l -> System.out.println("Observer 1: " + l));
        sleep(2000);
        seconds.subscribe(l -> System.out.println("Observer 2: " + l));
        sleep(1000);
    }

    /**
     * When you want to cache all emissions indefinitely for the long term and do not need to
     * control the subscription behavior to the source with ConnectableObservable, you can
     * use the cache() operator. It will subscribe to the source on the first downstream Observer
     * that subscribes and hold all values indefinitely. This makes it an unlikely candidate for
     * infinite Observables or large amounts of data that could tax your memory.
     *
     * You can also call cacheWithInitialCapacity() and specify the number of elements to
     * be expected in the cache. This will optimize the buffer for that size of elements in advance.
     */
    @Test
    void cache() {
        Observable<Integer> cachedRollingTotals =
                Observable.just(6, 2, 5, 7, 1, 4, 9, 8, 3)
                        .scan(0, (total,next) -> total + next)
                        .cache();
        cachedRollingTotals.subscribe(System.out::println);
    }

    /**
     * Before we discuss Subjects, it would be remiss to not highlight, they have use cases but
     * beginners often use them for the wrong ones, and end up in convoluted situations. As you
     * will learn, they are both an Observer and an Observable, acting as a proxy mulitcasting
     * device (kind of like an event bus). They do have their place in reactive programming, but
     * you should strive to exhaust your other options before utilizing them. Erik Meijer, the
     * creator of ReactiveX, described them as the "mutable variables of reactive programming". Just
     * like mutable variables are necessary at times even though you should strive for
     * immutability, Subjects are sometimes a necessary tool to reconcile imperative paradigms
     * with reactive ones.
     *
     * See Also PublishSubject, BehaviourSubject, ReplaySubject, AsyncSubject, UnicastSubject
     */
    @Test
    void subects() {

    }
}
