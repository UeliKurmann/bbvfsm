package ch.bbv.fsm.impl.internal.statemachine.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multimap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = 1L;

	@Override
	public List<V> get(Object key) {
		List<V> result = super.get(key);
		if(result == null) {
			return new ArrayList<>();
		}
		return result;
	}

	
	@Override
	public List<V> put(K key, List<V> value) {
		throw new UnsupportedOperationException();
	}
	
	public synchronized List<V> put(K key, V value) {
		if(containsKey(key)) {
			get(key).add(value);
		}else {
			List<V> l = new ArrayList<>();
			l.add(value);
			super.put(key, l);
		}
		return get(key);
	}
	
}
