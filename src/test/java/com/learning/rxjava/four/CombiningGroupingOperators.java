package com.learning.rxjava.four;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import org.junit.jupiter.api.Test;

@MicronautTest
public class CombiningGroupingOperators {

    @Test
    void groupBy() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        Observable<GroupedObservable<Integer, String>> byLengths = source.groupBy(String::length);
        byLengths.flatMapSingle(Observable::toList).subscribe(System.out::println);
    }
}
