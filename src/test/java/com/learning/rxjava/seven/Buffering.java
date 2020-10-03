package com.learning.rxjava.seven;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@MicronautTest
public class Buffering {

    /**
     * It is not uncommon to run into situations where an Observable is producing emissions
     * faster than an Observer can consume them.
     *
     * Of course, the ideal way to handle bottlenecks is to leverage backpressure using Flowable
     * instead of Observable. But not every source of emissions can be backpressured. You cannot instruct Observable.interval()
     * (or even Flowable.interval()) to slow down because the emissions are logically timesensitive. Asking it to slow down would make those time-based emissions inaccurate. User
     * input events, such as button clicks, logically cannot be backpressured either because you
     * cannot programmatically control the user.
     */

    /**
     * BUFFERING
     * <p>
     * The buffer() operator will gather emissions within a certain scope and emit each batch as
     * a list or another collection type.
     */

    @Test
    void fixedSizeBuffering1() {
        Observable.range(1, 50)
                .buffer(8)
                .subscribe(System.out::println);
    }

    /**
     * You can also supply a second bufferSupplier lambda argument to put items in another
     * collection besides a list, such as HashSet
     */
    @Test
    void fixedSizeBuffering2() {
        Observable.range(1, 50)
                .buffer(8, HashSet::new)
                .subscribe(System.out::println);
    }

    /**
     * To make things more interesting, you can also provide a skip argument that specifies how
     * many items should be skipped before starting a new buffer. If skip is equal to count, the
     * skip has no effect. But if they are different, you can get some interesting behaviors. For
     * instance, you can buffer 2 emissions but skip 3 before the next buffer starts, as shown here
     */
    @Test
    void fixedSizeBuffering3() {
        Observable.range(1, 100)
                .buffer(2, 4)
                .subscribe(System.out::println);
    }

    /**
     * If you make skip less than count, you can get some interesting rolling buffers. If you
     * buffer items into a size of 3 but have skip of 1, you will get rolling buffers.
     */
    @Test
    void rollingBuffer() {
        Observable.range(1, 10)
                .buffer(3, 1)
                .subscribe(System.out::println);
    }

    /**
     * Definitely play with the skip argument for buffer() , and you may find surprising use
     * cases for it. For example, I sometimes use buffer(2,1) to emit the "previous" emission
     * and the next emission together, as shown here. I also use filter() to omit the last list ,
     * which only contains 10
     */
    @Test
    void rollingBuffer2() {
        Observable.range(1, 10)
                .buffer(2, 1)
                .filter(c -> c.size() == 2)
                .subscribe(System.out::println);
    }

    /**
     * You can use buffer() at fixed time intervals by providing a long and TimeUnit. To
     * buffer emissions into a list at 1-second intervals, you can run the following code. Note that
     * we are making the source emit every 300 milliseconds, and each resulting buffered list will
     * likely contain three or four emissions due to the one-second interval cut-offs
     */
    @Test
    void timeBasedBuffering1() throws InterruptedException {
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .buffer(1, TimeUnit.SECONDS)
                .subscribe(System.out::println);
        sleep(4000);
    }

    /**
     * Here, we buffer emissions every 1 second, but we limit the buffer size to 2
     */
    @Test
    void timeBasedBuffering2() throws InterruptedException {
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .buffer(1, TimeUnit.SECONDS, 2)
                .subscribe(System.out::println);
        sleep(5000);
    }

    /**
     * The most powerful variance of buffer() is accepting another Observable as a boundary
     * argument. It does not matter what type this other Observable emits. All that matters is
     * every time it emits something, it will use the timing of that emission as the buffer cut-off.
     */
    @Test
    void boundaryBasedBuffering() throws InterruptedException {
        Observable<Long> cutOffs =
                Observable.interval(1, TimeUnit.SECONDS);
        Observable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> (i + 1) * 300) // map to elapsed time
                .buffer(cutOffs)
                .subscribe(System.out::println);
        sleep(5000);
    }
}
