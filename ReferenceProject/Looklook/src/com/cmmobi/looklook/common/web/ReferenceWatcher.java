package com.cmmobi.looklook.common.web;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceWatcher<T> {

	private final ReferenceQueue<? super T> mQueue;
	private HashSet<Reference<T>> mRefs;
	private final int mThreshold;

	private final AtomicInteger mCount;

	public ReferenceWatcher() {
		this(20);
	}

	public ReferenceWatcher(int threshold) {
		mQueue = new ReferenceQueue<T>();
		mRefs = new HashSet<Reference<T>>();
		mThreshold = threshold;
		mCount = new AtomicInteger();
	}

	public void watch(T ref) {
		mRefs.add(new SoftReference<T>(ref, mQueue));
		if (mCount.incrementAndGet() >= mThreshold) {
			clean();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void clean() {
		Reference ref = null;
		while ((ref = mQueue.poll()) != null) {
			T value = (T) ref.get();
			if (value != null) {
				mRefs.remove(value);
			}
		}
	}

	public Set<T> getSnapShotAndClean() {
		clean();
		HashSet<T> values = new HashSet<T>();
		for (Reference<T> tempRef : mRefs) {
			T value = tempRef.get();
			if (value != null) {
				values.add(value);
			}
		}
		mCount.set(mRefs.size());

		return values;
	}
}
