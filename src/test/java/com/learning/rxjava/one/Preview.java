package com.learning.rxjava.one;


public class Preview {

    /**
     * - Data are events and events are data
     * - RxJava1 non dipende da Reactive Strams (che è uno standard per l'implementazione asincrona di stream), mentre RxJava2 sì.
     * - Un Observable pusha cose
     *
     * - Un Observable può pushare cose o eventi da ogni sorgente, tipo da un db o da un live Twitter feed.
     *
     * - In RxJava1 i tipi sono contenuti nel package rx, in RxJava2 in io.reactivex
     *
     * - Una volta che hai definito un Observable, serve un Observer registrato per far sì che l'Observable pushi gli eventi.
     *
     * - Tra un Observable e il suo Observer puoi infilare degli Operatori che applicano trasformazioni e restituscono Observable derivati dal precedente.
     *
     * - Observable e Stream sono molto simili, la differenza è che un Observable pusha elementi, lo Stream li pulla.
     *
     */
}
