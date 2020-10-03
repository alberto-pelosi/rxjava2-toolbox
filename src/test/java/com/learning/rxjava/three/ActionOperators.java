package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

/**
 * To close this chapter, we will cover some helpful operators that can assist in debugging as
 * well as getting visibility into an Observable chain. These are the action or doOn operators
 */
@MicronautTest
public class ActionOperators {

    /**
     * These three operators: doOnNext(), doOnComplete(), and doOnError() are like putting
     * a mini Observer right in the middle of the Observable chain.
     * <p>
     * The doOnNext() operator allows you to peek at each emission coming out of an operator
     * and going into the next. This operator does not affect the operation or transform the
     * emissions in any way. We just create a side-effect for each event that occurs at that point in
     * the chain.
     * <p>
     * You can specify all three actions for onNext(), onComplete(), and
     * onError() using doOnEach() as well. The subscribe() method accepts
     * these three actions as lambda arguments or an entire Observer<T>. It is
     * like putting subscribe() right in the middle of your Observable chain!
     * There is also a doOnTerminate() operator, which fires for an
     * onComplete() or onError() event.
     */
    @Test
    void doOnNext() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .doOnNext(s -> System.out.println("Processing: " + s))
                .map(String::length)
                .subscribe(i -> System.out.println("Received: " + i));
    }

    /**
     * We use both operators to print when subscription and disposal occur, as shown in the
     * following code snippet. As you can predict, we see the subscribe event fire off first. Then,
     * the emissions go through, and then disposal is finally fired:
     */
    @Test
    void doOnSubscribe() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .doOnSubscribe(d -> System.out.println("Subscribing!"))
                .doOnDispose(() -> System.out.println("Disposing!"))
                .subscribe(i -> System.out.println("RECEIVED: " + i));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remember that Maybe and Single types do not have an onNext() event but rather an
     * onSuccess() operator to pass a single emission.
     */
    @Test
    void doOnSuccess() {
        Observable.just(5, 3, 7, 10, 2, 14)
                .reduce((total, next) -> total + next)
                .doOnSuccess(i -> System.out.println("Emitting: " + i))
                .subscribe(i -> System.out.println("Received: " + i));
    }
}
