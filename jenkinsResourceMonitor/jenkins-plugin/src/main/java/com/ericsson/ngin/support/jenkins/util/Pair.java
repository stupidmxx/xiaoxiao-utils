/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.ericsson.ngin.support.jenkins.util;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {

	K k;
	V v;

	public Pair(K key, V value) {
		k = key;
		v = value;
	}
	@Override
	public K getKey() {
		return k;
	}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public V setValue(V value) {
		return v = value;
	}
}
