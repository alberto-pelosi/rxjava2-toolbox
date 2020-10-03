package com.learning.rxjava.four;

import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@MicronautTest
public class CombiningMergingOperators {

    /**
     * The Observable.merge() operator will take two or more Observable<T> sources
     * emitting the same type T and then consolidate them into a single Observable<T>.
     * <p>
     * This is just an implementation detail, and you should use
     * Observable.concat() if you explicitly want to fire elements of each Observable
     * sequentially and keep their emissions in a sequential order.
     * <p>
     */
    @Test
    void merge() {
        Observable<String> source1 =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        Observable<String> source2 =
                Observable.just("Zeta", "Eta", "Theta");
        Observable.merge(source1, source2)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }

    /**
     * If you have more than four Observable<T> sources, you can use the
     * Observable.mergeArray()
     * <p>
     * Since RxJava 2.0 was written for JDK 6+
     * and has no access to a @SafeVarargs annotation, you will likely get some type safety
     * warnings.
     * I could achieve the preceding example
     * in a more type-safe way by putting all these sources in List<Observable<T>> and passing
     * them to Observable.merge().
     * <p>
     * To summarize, Observable.merge() will combine multiple Observable<T> sources
     * emitting the same type T and consolidate into a single Observable<T>. It works on infinite
     * Observables and does not necessarily guarantee that the emissions come in any order. If
     * you care about the emissions being strictly ordered by having each Observable source
     * fired sequentially, you will likely want to use Observable.concat()
     */
    @Test
    void mergeArray() {
        Observable<String> source1 =
                Observable.just("Alpha", "Beta");
        Observable<String> source2 =
                Observable.just("Gamma", "Delta");
        Observable<String> source3 =
                Observable.just("Epsilon", "Zeta");
        Observable<String> source4 =
                Observable.just("Eta", "Theta");
        Observable<String> source5 =
                Observable.just("Iota", "Kappa");
        Observable.mergeArray(source1, source2, source3, source4,
                source5).subscribe(i -> System.out.println("RECEIVED: " + i));

        List<Observable<String>> sources = Arrays.asList(source1, source2, source3, source4, source5);

        Observable.merge(sources)
                .subscribe(i -> System.out.println("RECEIVED: " + i));
    }

    /**
     * One of the most powerful and critical operators in RxJava is flatMap(). If you have to
     * invest time in understanding any RxJava operator, this is the one. It is an operator that
     * performs a dynamic Observable.merge() by taking each emission and mapping it to an
     * Observable. Then, it merges the emissions from the resulting Observables into a single
     * stream.
     * <p>
     * The simplest application of flatMap() is to map one emission to many emissions.
     */
    @Test
    void flatMap1() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.flatMap(s -> Observable.fromArray(s.split("")))
                .subscribe(System.out::println);
    }

    @Test
    void flatMap2() {
        Observable<String> source =
                Observable.just("521934/2342/FOXTROT", "21962/12112/78886/TANGO", "283242/4542/WHISKEY/2348562");
        source.flatMap(s -> Observable.fromArray(s.split("/")))
                .filter(s -> s.matches("[0-9]+")) //use regex to filter integers
                .map(Integer::valueOf)
                .subscribe(System.out::println);
    }

    @Test
    void flatMap3() {
        Observable<Integer> intervalArguments = Observable.just(2, 3, 10, 7);
        intervalArguments.flatMap(i ->
                Observable.interval(i, TimeUnit.SECONDS).map(i2 -> i + "s interval: " + ((i2 + 1) * i) + " seconds elapsed")
        ).subscribe(System.out::println);
        try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Note that there are many flavors and variants of flatMap(), accepting a number of
     * overloads that we will not get into deeply for the sake of brevity. We can pass a second
     * combiner argument, which is a BiFunction<T,U,R> lambda, to associate the originally
     * emitted T value with each flat-mapped U value and turn both into an R value.
     * <p>
     * We can also use flatMapIterable() to map each T emission into an Iterable<R>
     * instead of an Observable<R>. It will then emit all the R values for each Iterable<R>,
     * saving us the step and overhead of converting it into an Observable. There are also
     * flatMap() variants that map to Singles (flatMapSingle()), Maybes
     * (flatMapMaybe()), and Completables (flatMapCompletable()). A lot of these
     * overloads also apply to concatMap()
     */
    @Test
    void flatMap4() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.flatMap(s -> Observable.fromArray(s.split("")),
                (s, r) ->
                        s + "-" + r)
                .subscribe(System.out::println);
    }

    @Test
    void flatMap5() {
        Observable<String> source =
                Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        source.flatMap(s -> Observable.fromArray(s.split("")),
                (s, r) ->
                        s + "-" + r)
                .subscribe(System.out::println);
    }


}
