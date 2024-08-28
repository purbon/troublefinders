package com.purbon.kafka;

public class Tuple<T> {

    public T key;
    public T value;

    public Tuple(T key, T value) {
        this.key = key;
        this.value = value;
    }
}
