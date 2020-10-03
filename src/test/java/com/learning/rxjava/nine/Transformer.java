package com.learning.rxjava.nine;

import com.google.common.collect.ImmutableList;
import io.micronaut.test.annotation.MicronautTest;
import io.reactivex.*;
import org.junit.jupiter.api.Test;

@MicronautTest
public class Transformer {

    /**
     * To invoke a Transformer into an Observable chain, you pass it to the compose() operator.
     * When called on an Observable<T>, the compose() operator accepts
     * an ObservableTransformer<T,R> and returns the transformed Observable<R>
     * <p>
     * It is common for APIs to organize Transformers in a static factory class.
     * <p>
     * When you start creating your own Transformers and custom operators (covered later), an
     * easy way to shoot yourself in the foot is to share states between more than one subscription.
     * This can quickly create unwanted side effects and buggy applications and is one of the
     * reasons you have to tread carefully as you create your own operators.
     * <p>
     * A quick and easy way to create a new resource (such as AtomicInteger) for each
     * subscription is to wrap everything in Observable.defer(), including the
     * AtomicInteger instance. This way, a new AtomicInteger is created each time with the
     * returned indexing operations
     */

    @Test
    void compose() {
        Observable.just("Alpha", "Beta", "Gamma", "Delta",
                "Epsilon")
                .compose(toImmutableList())
                .subscribe(System.out::println);
        Observable.range(1, 10)
                .compose(toImmutableList())
                .subscribe(System.out::println);
    }

    /**
     * It is common for APIs to organize Transformers in a static factory class. In a real-world
     * application, you may store your toImmutableList() Transformer inside a
     * GuavaTransformers class. Then, you can invoke it by calling
     * compose(GuavaTransformers.toImmutableList()) in your Observable operation.
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, ImmutableList<T>>
    toImmutableList() {
        return new ObservableTransformer<T, ImmutableList<T>>() {
            @Override
            public ObservableSource<ImmutableList<T>>
            apply(Observable<T> upstream) {
                return (ObservableSource) upstream.collect(ImmutableList::<T>builder,
                        ImmutableList.Builder::add)
                        .map(ImmutableList.Builder::build).toObservable();
                // must turn Single into Observable
            }
        };
    }

    public static ObservableTransformer<String, String>
    joinToString(String separator) {
        return upstream -> upstream
                .collect(StringBuilder::new, (b, s) -> {
                    if (b.length() == 0)
                        b.append(s);
                    else
                        b.append(separator).append(s);
                })
                .map(StringBuilder::toString)
                .toObservable();
    }
}
