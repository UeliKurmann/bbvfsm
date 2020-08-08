package ch.bbv.fsm.impl.internal.statemachine.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple multi map implementation.
 *
 * @author Ueli Kurmann
 *
 * @param <K> the key
 * @param <V> the value
 */
public class Multimap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = 1L;

	@Override
	public List<V> get(final Object key) {
		final List<V> result = super.get(key);
		if (result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	@Override
	public List<V> put(final K key, final List<V> value) {
		throw new UnsupportedOperationException();
	}

	public synchronized List<V> putOne(final K key, final V value) {

		if (containsKey(key)) {
			get(key).add(value);
		} else {
			final List<V> l = new ArrayList<>();
			l.add(value);
			super.put(key, l);
		}
		return get(key);
	}

}
