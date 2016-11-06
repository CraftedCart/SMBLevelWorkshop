package craftedcart.smblevelworkshop.util;

import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 03/11/2016 (DD/MM/YYYY)
 */
public class QuickMapEntry<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public QuickMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        return null;
    }

}
