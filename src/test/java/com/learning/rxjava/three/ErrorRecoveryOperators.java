package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

@MicronautTest
public class ErrorRecoveryOperators {

    @Test
    void onErrorReturnItem() {
        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> 10 / i)
                .onErrorReturnItem(-1)
                .subscribe(i -> System.out.println("RECEIVED: " + i), e -> System.out.println("RECEIVED ERROR: " + e)
                );
    }

    /**
     * On error returns an Observable, not an item.
     * <p>
     * The placement of onErrorReturn() matters. If we put it before the map() operator, the
     * error would not be caught because it happened after onErrorReturn(). To intercept the
     * emitted error, it must be downstream from where the error occurred.
     */
    @Test
    void onErrorReturn() {

        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> 10 / i).map(k -> k * 2)
                .onErrorReturn(e -> -1)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        e -> System.out.println("RECEIVED ERROR: " + e)
                );

        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> 10 / i)
                .onErrorReturn(e -> -1)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        e -> System.out.println("RECEIVED ERROR: " + e)
                );

        //without onErrorReturn
        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> {
                    try {
                        return 10 / i;
                    } catch (ArithmeticException e) {
                        return -1;
                    }
                })
                .subscribe(i -> System.out.println("RECEIVED: " +
                                i),
                        e -> System.out.println("RECEIVED ERROR: " + e)
                );
    }

    /**
     * Similar to onErrorReturn() and onErrorReturnItem(), onErrorResumeNext() is very
     * similar. The only difference is that it accepts another Observable as a parameter to emit
     * potentially multiple values, not a single value, in the event of an exception.
     */
    @Test
    void onErrorResumeNext() {
        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> 10 / i)
                .onErrorResumeNext(Observable.just(-1).repeat(3))
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        e -> System.out.println("RECEIVED ERROR: " + e)
                );
    }

    /**
     * You can also provide Predicate<Throwable> or BiPredicate<Integer,Throwable> to
     * conditionally control when retry() is attempted. The retryUntil() operator will allow
     * retries while a given BooleanSupplier lambda is false. There is also an advanced
     * retryWhen() operator that supports advanced composition for tasks such as delaying
     * retries
     */
    @Test
    void retry() {
        Observable.just(5, 2, 4, 0, 3, 2, 8)
                .map(i -> 10 / i)
                .retry(2)
                .subscribe(i -> System.out.println("RECEIVED: " + i),
                        e -> System.out.println("RECEIVED ERROR: " + e)
                );
    }
}
