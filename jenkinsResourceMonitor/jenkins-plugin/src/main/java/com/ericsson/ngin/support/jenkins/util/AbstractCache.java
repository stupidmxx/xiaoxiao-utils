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

import hudson.model.Computer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCache<T> implements Cache<T> {

	public abstract T getNew(Computer c);

	private static final int CACHE_TIME = 60 * 1000;
	private ConcurrentHashMap<String, Map.Entry<Long, T>> cacheMap = new ConcurrentHashMap<String, Map.Entry<Long, T>>();

	@Override
	public T getValue(Computer c) {
		long current = System.currentTimeMillis();
		if (!cacheMap.containsKey(c.getName())) {
			cacheMap.put(c.getName(), new Pair<Long, T>(current, getNew(c)));
		}
		if ((current - cacheMap.get(c.getName()).getKey()) > CACHE_TIME) {
			cacheMap.put(c.getName(), new Pair<Long, T>(current, getNew(c)));
		}
		return cacheMap.get(c.getName()).getValue();
	}

	@Override
	public void clear() {
		cacheMap.clear();
	}
}