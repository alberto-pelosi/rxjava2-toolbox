package com.learning.rxjava.two;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.ResourceObserver;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class ObservableAndSubscriber {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    static int start = 1;
    static int count = 5;

    /**
     * The Observable is a push-based,
     * composable iterator. For a given Observable<T>, it pushes items (called emissions) of type
     * T through a series of operators until it finally arrives at a final Observer.
     * <p>
     * - onNext(): This passes each item one at a time from the source Observable all
     * the way down to the Observer.
     * - onComplete(): This communicates a completion event all the way down to the
     * Observer, indicating that no more onNext() calls will occur.
     * - onError(): This communicates an error up the chain to the Observer, where
     * the Observer typically defines how to handle it. Unless a retry() operator is
     * used to intercept the error, the Observable chain typically terminates, and no
     * more emissions will occur
     */
    @Test
    void createObservable() {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Alpha");
            emitter.onNext("Beta");
            emitter.onNext("Gamma");
            emitter.onNext("Delta");
            emitter.onNext("Epsilon");
            emitter.onComplete();
        });

        // s -> System.out.println(s) invoked on onNext of subscriber
        source.subscribe(s -> System.out.println(s));
    }

    /**
     * The subscriber in RxJava1 is the observer in RxJava2
     */
    @Test
    void just() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.subscribe(s -> System.out.println(s));
    }

    @Test
    void observer() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        Observer<Integer> myObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                //do nothing with Disposable, disregard for now
            }

            @Override
            public void onNext(Integer value) {
                System.out.println("RECEIVED: " + value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Done!");
            }
        };
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(myObserver);
    }

    /**
     * Implementing an Observer is a bit verbose and cumbersome. Thankfully, the
     * subscribe() method is overloaded to accept lambda arguments for our three events. This
     * is likely what we will want to use for most cases, and we can specify three lambda
     * parameters separated by commas: the onNext lambda, the onError lambda, and the
     * onComplete lambda.
     * <p>
     * It is critical to note that most of the subscribe() overload variants (including the
     * shorthand lambda ones we just covered) return a Disposable that we did not do anything
     * with. disposables allow us to disconnect an Observable from an Observer so emissions
     * are terminated early, which is critical for infinite or long-running Observables.
     */
    @Test
    void observerSubscribe() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta",
                        "Epsilon");
        source.map(String::length).filter(i -> i >= 5)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        Throwable::printStackTrace,
                        () -> System.out.println("Done!"));
    }

    /**
     * - Cold Observables are much like a music CD that can be replayed to each listener, so each
     * person can hear all the tracks at any time.
     * <p>
     * - Hot Observable is more like a radio station. It broadcasts the same emissions to all Observers
     * at the same time. If an Observer subscribes to a hot Observable, receives some emissions,
     * and then another Observer comes in afterwards, that second Observer will have missed
     * those emissions. Logically, hot Observables often represent events rather than finite datasets.
     * <p>
     * - ConnectableObservable: A helpful form of hot Observable is ConnectableObservable.
     * It will take any Observable, even if it is cold, and make it hot so that all emissions are played to all
     * Observers at once. To do this conversion, you simply need to call publish() on any
     * Observable, and it will yield a ConnectableObservable. But subscribing will not start
     * the emissions yet. You need to call its connect() method to start firing the emissions.
     * See Also Multicasting.
     * <p>
     * For now, remember that ConnectableObservable is hot, and therefore, if new
     * subscriptions occur after connect() is called, they will miss emissions that were fired
     * previously
     */
    @Test
    void connectableObservable() {
        //Observable definition
        ConnectableObservable<String> source = Observable.just("Alpha", "Beta").publish();

        //Observers definition
        source.subscribe(s -> System.out.println("Observer 1 " + s));
        source.subscribe(s -> System.out.println("Observer 2 " + s));

        //fire!
        source.connect();
    }

    /**
     * To emit a consecutive range of integers, you can use Observable.range(). This will emit
     * each number from a start value and increment each emission until the specified count is
     * reached. These numbers are all passed through the onNext() event, followed by the
     * onComplete()
     */
    @Test
    void range() {
        Observable.range(1, 10)
                .subscribe(s -> System.out.println("RECEIVED: " + s));
    }

    /**
     * It will emit a consecutive long emission (starting at 0) at every
     * specified time interval
     */
    @Test
    void interval() {
        Observable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS);
        source.subscribe(s -> System.out.println(s));
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * RxJava Observables are much more robust and expressive than Futures, but if you have
     * existing libraries that yield Futures, you can easily turn them into Observables via
     * Observable.future()
     */
    @Test
    void future() {
    }


    /**
     * Although this may not seem useful yet, it is sometimes helpful to create an Observable
     * that emits nothing and calls onComplete()
     */
    @Test
    void empty() {
        Observable<String> empty = Observable.empty();
    }

    /**
     * never calls onComplete(), forever leaving observers waiting for
     * emissions but never actually giving any
     */
    @Test
    void never() {

    }

    /**
     * you can create an
     * Observable that immediately calls onError()
     */
    @Test
    void error() {

    }

    /**
     * To remedy this problem of Observable sources not capturing state changes, you can create
     * a fresh Observable for each subscription. This can be achieved using
     * Observable.defer(), which accepts a lambda instructing how to create an Observable
     * for every subscription. Because this creates a new Observable each time, it will reflect any
     * changes driving its parameters
     */
    @Test
    void defer() {

        Observable<Integer> source = Observable.defer(() -> Observable.range(start, count));

        source.subscribe(s -> System.out.println("Observer 1: " + s));

        count = 10;

        source.subscribe(s -> System.out.println("Observer 2: " + s));
    }

    /**
     * If you need to perform a calculation or action and then emit it, you can use
     * Observable.just() (or Single.just() or Maybe.just(), which we will learn about
     * later). But sometimes, we want to do this in a lazy or deferred manner. Also, if that
     * procedure throws an error, we want it to be emitted up the Observable chain through
     * onError() rather than throw the error at that location in traditional Java fashion
     */
    @Test
    void fromCallable() {
        try {
            Observable<Integer> source = Observable.just(1 / 0);

            source.subscribe(System.out::println, e -> System.out.println(e));
        } catch (Exception e) {
            logger.info("Exception not managed by Observer");
        }

        Observable<Integer> fromCallableSource = Observable.fromCallable(() -> 1 / 0);
        fromCallableSource.subscribe(System.out::println, e -> System.out.println("Exception managed by Observer: " + e));
    }

    /**
     * Single<T> is essentially an Observable<T> that will only emit one item
     * <p>
     * interface SingleObserver<T> {
     * void onSubscribe(Disposable d);
     * void onSuccess(T value);
     * void onError(Throwable error);
     * }
     */
    @Test
    void single() {
        Single.just(1).subscribe(s -> System.out.println(s));
    }

    /**
     * Maybe is just like a Single except that it allows no emission to occur at all.
     * <p>
     * public interface MaybeObserver<T> {
     * void onSubscribe(Disposable d);
     * void onSuccess(T value);
     * void onError(Throwable e);
     * void onComplete();
     * }
     */
    @Test
    void maybe() {
        //no emission
        Maybe<Integer> emptySource = Maybe.empty();
        emptySource.subscribe(s -> System.out.println("Process 2 received: " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Process 2 done!"));
    }

    /**
     * Completable is simply concerned with an action being executed, but it does not receive
     * any emissions. Logically, it does not have onNext() or onSuccess() to receive emissions,
     * but it does have onError() and onComplete():
     * <p>
     * interface CompletableObserver<T> {
     * void onSubscribe(Disposable d);
     * void onComplete();
     * void onError(Throwable error);
     * }
     */
    @Test
    void completable() {
        Completable.fromRunnable(() -> System.out.println("Do something"))
                .subscribe(() -> System.out.println("Done!"));
    }

    /**
     * When you subscribe() to an Observable to receive emissions, a stream is created to
     * process these emissions through the Observable chain. Of course, this uses resources. When
     * we are done, we want to dispose of these resources so that they can be garbage-collected.
     * Thankfully, the finite Observables that call onComplete() will typically dispose of
     * themselves safely when they are done. But if you are working with infinite or long-running
     * Observables, you likely will run into situations where you want to explicitly stop the
     * emissions and dispose of everything associated with that subscription. As a matter of fact,
     * you cannot trust the garbage collector to take care of active subscriptions that you no longer
     * need, and explicit disposal is necessary in order to prevent memory leaks.
     * <p>
     * public interface Disposable {
     * void dispose();
     * boolean isDisposed();
     * }
     */
    @Test
    void disposing() {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        Disposable disposable =
                seconds.subscribe(l -> System.out.println("Received: " + l));
        //sleep 5 seconds
        try {
            sleep(5000);
            disposable.dispose();
            //sleep 5 seconds to prove
            //there are no more emissions
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Earlier, I shied away from talking about the onSubscribe() method in the Observer, but
     * now we will address it. You may have noticed that Disposable is passed in the
     * implementation of an Observer through the onSubscribe() method. This method was
     * added in RxJava 2.0, and it allows the Observer to have the ability to dispose of the
     * subscription at any time.
     */
    @Test
    void handlingDisposableWithinObserver() {
        Observer<Integer> myObserver = new Observer<Integer>() {
            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable disposable) {
                this.disposable = disposable;
            }

            @Override
            public void onNext(Integer value) {
                //has access to Disposable
            }

            @Override
            public void onError(Throwable e) {
                //has access to Disposable
            }

            @Override
            public void onComplete() {
                //has access to Disposable
            }
        };
    }

    /**
     * If you do not want to
     * explicitly handle the Disposable and want RxJava to handle it for you (which is probably
     * a good idea until you have reason to take control), you can extend ResourceObserver as
     * your Observer, which uses a default Disposable handling. Pass this to subscribeWith()
     * instead of subscribe(), and you will get the default Disposable returned
     */
    @Test
    void autodisposedObserver() {
        Observable<Long> source =
                Observable.interval(1, TimeUnit.SECONDS);
        ResourceObserver<Long> myObserver = new
                ResourceObserver<>() {
                    @Override
                    public void onNext(Long value) {
                        System.out.println(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Done!");
                    }
                };
        //capture Disposable
        Disposable disposable = source.subscribeWith(myObserver);
    }

    /**
     * If you have several subscriptions that need to be managed and disposed of, it can be helpful
     * to use CompositeDisposable
     */
    @Test
    void compositeDisposable() {
        Observable<Long> seconds =
                Observable.interval(1, TimeUnit.SECONDS);
        //subscribe and capture disposables
        Disposable disposable1 =
                seconds.subscribe(l -> System.out.println("Observer 1: " +
                        l));
        Disposable disposable2 =
                seconds.subscribe(l -> System.out.println("Observer 2: " +
                        l));
        CompositeDisposable disposables = new CompositeDisposable();
        disposables.addAll(disposable1, disposable2);
        try {
            sleep(5000);
            //dispose all disposables
            disposables.dispose();
            //sleep 5 seconds to prove
            //there are no more emissions
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * tbd
     */

    void disposalWithObservableCreate() {

    }
}
