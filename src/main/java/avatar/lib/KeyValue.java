/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avatar.lib;

import java.util.Map;

/**
 *
 * @author kitakeyos - Hoàng Hữu Dũng
 */
public class KeyValue<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public K setKey(K key) {
        return this.key = key;
    }

    public V setValue(V value) {
        return this.value = value;
    }
}
