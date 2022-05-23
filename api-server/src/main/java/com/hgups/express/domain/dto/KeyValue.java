package com.hgups.express.domain.dto;

public class KeyValue <K, V>{
    public K key;
    public V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
