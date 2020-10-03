package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

@MicronautTest
public class ReducingOperators {

    /**
     * Like most reduction operators, this should not be used on an infinite Observable
     */
    @Test
    void count() {
        Observable.just(1, 2, 3).count().subscribe(System.out::println);
    }

    /**
     * The reduce() operator is syntactically identical to scan(), but it only emits the final
     * accumulation when the source calls onComplete()
     */
    @Test
    void reduce() {
        Observable.just(1, 2, 3).reduce(0, (acc, next) -> acc + next).subscribe(System.out::println);
    }

    /**
     * The all() operator verifies that each emission qualifies with a specified condition and
     * return a Single<Boolean>. If they all pass, it will emit True. If it encounters one that fails,
     * it will immediately emit False.
     * <p>
     * If you call all() on an empty Observable, it will emit true due to the
     * principle of vacuous truth. You can read more about vacuous truth on
     * Wikipedia at https://en.wikipedia.org/wiki/Vacuous_truth
     */
    @Test
    void all() {
        Observable.just(1, 2, 3).all(e -> e > 5).subscribe(System.out::println);
    }

    /**
     * The any() method will check whether at least one emission meets a specific criterion and
     * return a Single<Boolean>
     */
    @Test
    void any() {

    }

    /**
     * The contains() operator will check whether a specific element (based on the
     * hashCode()/equals() implementation) ever emits from an Observable
     */
    @Test
    void contains() {

    }
}
