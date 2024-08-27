package com.purbon.kafka;

public class Tuple<T> {

    T key;
    T value;

    public Tuple(T key, T value) {
        this.key = key;
        this.value = value;
    }
}
