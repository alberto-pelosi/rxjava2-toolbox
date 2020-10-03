package com.learning.rxjava.eight;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import static com.learning.rxjava.six.Concurrency.intenseCalculation;
import static java.lang.Thread.sleep;

@MicronautTest
public class Backpressure {

    /**
     * The Flowable is a backpressured variant of the Observable that
     * tells the source to emit at a pace specified by the downstream operations.
     */
    @Test
    void flowable() throws InterruptedException {
        Flowable.range(1, 999_999_999)
                .map(MyItem::new)   
                .observeOn(Schedulers.io())
                .subscribe(myItem -> {
                    sleep(50);
                    System.out.println("Received MyItem " +
                            myItem.id);
                });
        sleep(Long.MAX_VALUE);
    }

    /**
     * Instead of an Observer, the Flowable uses a Subscriber to consume emissions and
     * events at the end of a Flowable chain
     */
    @Test
    void subscriber() throws InterruptedException {
        Flowable.range(1,1000)
                .doOnNext(s -> System.out.println("Source pushed "
                        + s))
                .observeOn(Schedulers.io())
                .map(i -> intenseCalculation(i))
                .subscribe(s -> System.out.println("Subscriber received " + s),
                Throwable::printStackTrace,
                () -> System.out.println("Done!")
        );
        sleep(20000);
    }

    static final class MyItem {
        final int id;

        MyItem(int id) {
            this.id = id;
            System.out.println("Constructing MyItem " + id);
        }

    }
}
