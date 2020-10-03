package com.learning.rxjava.three;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

@MicronautTest
public class CollectionOperators {

    /**
     * For a given Observable<T>, it will collect
     * incoming emissions into a List<T> and then push that entire List<T> as a single emission
     * (through Single<List<T>>)
     */
    @Test
    void toList() {
        Observable.just(1, 2, 3).toList().subscribe(e -> e.forEach(System.out::println));
    }

    @Test
    void toSortedList() {
        Observable.just(3, 2, 1).toSortedList().subscribe(e -> e.forEach(System.out::println));
    }

    /**
     * By default, toMap() will use HashMap. You can also provide a third lambda argument that
     * provides a different map implementation.
     * <p>
     * Note that if I have a key that maps to multiple emissions, the last emission for that key is
     * going to replace subsequent ones
     */
    @Test
    void toMap() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .toMap(s -> s.charAt(0), String::length)
                .subscribe(s -> System.out.println("Received: " + s));
    }

    /**
     * If you want a given key to map to multiple emissions, you can use toMultiMap() instead
     */
    @Test
    void toMultiMap() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .toMultimap(String::length)
                .subscribe(s -> System.out.println("Received: " + s));
    }

    /**
     * When none of the collection operators have what you need, you can always use the
     * collect() operator to specify a different type to collect items into. For instance, there is no
     * toSet() operator to collect emissions into a Set<T>, but you can quickly use collect()
     * to effectively do this.
     *
     * Use collect() instead of reduce() when you are putting emissions into a mutable object,
     * and you need a new mutable object seed each time.
     *
     * Again, the collect() operator is helpful to collect emissions into any arbitrary type that
     * RxJava does not provide out of the box.
     */
    @Test
    void collect() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
                .collect(HashSet::new, HashSet::add)
                .subscribe(s -> System.out.println("Received: " + s));
    }
}
