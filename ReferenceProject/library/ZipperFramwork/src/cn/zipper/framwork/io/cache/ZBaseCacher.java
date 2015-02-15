package cn.zipper.framwork.io.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.utils.ZThread;

/**
 * 文件缓存基类;
 * 需要完善: 
 * 		过期时间, 
 * 		task列表最大容量限制, 
 * 		自动控制缓存文件体积
 * 		存储卡卸载,
 * 		存储空间足够;
 */
public abstract class ZBaseCacher<T> implements Runnable {
	
	private ExecutorService executor;
	private ExecutorService retryExecutor;
	private HashMap<String, T> cacher;
	private List<Task> tasksVector;
	private Handler handler;
	private Task tempTask;
	private int threadPoolSize;
	private int currentPoolSize;
	private int startIndex;
	protected File file;
	
	
	public ZBaseCacher(File path, int poolSize) {
		init(path, poolSize);
	}
	
	public ZBaseCacher(String path, int poolSize) {
		init(new File(path), poolSize);
	}
	
	private void init(File file, int poolSize) {
		this.executor = Executors.newFixedThreadPool(poolSize);
		this.retryExecutor = Executors.newCachedThreadPool();
//		this.cacher = new HashMap<String, SoftReference<T>>();
		this.cacher = new HashMap<String, T>();
		this.tasksVector = new ArrayList<Task>();
		this.handler = new Handler(Looper.getMainLooper());
		this.threadPoolSize = poolSize;
		this.file = file;
		if (!this.file.isDirectory()) {
			ZLog.printStackTrace();
		} else {
			this.file.mkdirs();
		}
	}
	
	protected abstract String formatName(String name);
	protected abstract T readFile(String name);
	protected abstract boolean saveFile(String name, T type);
	protected abstract T load(String name);
	
	public final void cleanAllCache() {
		cleanFileCache();
		cleanMemoryCache();
	}
	
	public final void cleanMemoryCache() {
		cacher.clear();
	}
	
	public final void cleanFileCache() {
		File[] files = file.listFiles();
		if (files != null) {
			for (File temp : files) {
				temp.delete();
			}
		}
	}
	
	private final void clean() {
		File[] files = file.listFiles();
		if (files != null) {
			for (File temp : files) {
//				ZFileReader reader = new ZFileReader(temp, null);
//				reader.
			}
		}
	}
	
	public final void cleanTasks() {
		tasksVector.clear();
	}
	
	public final void shutdown() {
		currentPoolSize = 0;
		executor.shutdownNow();
		retryExecutor.shutdownNow();
		cleanTasks();
	}
	
	public final synchronized void startAt(String name, int loadCount) {
		int tempIndex = indexOf(name);
		if (tempIndex > -1) {
			this.startIndex = tempIndex;
			if(currentPoolSize < threadPoolSize) {
				pushInExecutor(popOutNextTask());
			}
		}
	}
	
	public final T get(String name, boolean isReadFileInThread, T defaultType, T errorType, Object tag, OnStateChangedListener<T> listener) {
		T type = null;
		
		String formatName = formatName(name);
		
		if (cacher.containsKey(formatName)) {
//			SoftReference<T> reference = cacher.get(formatName);
			T reference = cacher.get(formatName);
			if (reference != null) {
//				type = reference.get();
				type = reference;
			}
		}
		
		if (type == null && !isReadFileInThread) {
			try {
				type = readFile(formatName);
				putInMemoryCache(formatName, type);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		if (type == null) {
			type = defaultType;
			if (indexOf(name) < 0) {
				Task task = new Task(name, formatName, isReadFileInThread, errorType, tag, listener);
				tasksVector.add(task);
				if(currentPoolSize < threadPoolSize) {
					pushInExecutor(popOutNextTask());
				}
			}
		}
		
		return type;
	}
	
	private int indexOf(String name) {
		int index = -1;
		for (int i=0; i<tasksVector.size(); i++) {
			Task task = tasksVector.get(i);
			if (task.name.equals(name)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	private void putInMemoryCache(String formatName, T type) {
		if (type != null) {
//			cacher.put(formatName, new SoftReference<T>(type));
			cacher.put(formatName, type);
		}
	}
	
	private void pushInExecutor(Task task) {
		if (!executor.isShutdown() && task != null) {
			currentPoolSize ++;
			executor.execute(task);
		}
	}
	
	private void pushInRetryExecutor(Task task) {
		if (!retryExecutor.isShutdown() && task != null) {
			retryExecutor.execute(task);
		}
	}
	
	private Task popOutNextTask() {
		Task task = null;
		
		if (tasksVector.size() > 0) {
			
			int tempIndex = startIndex;
			if (tempIndex <= tasksVector.size() - 1) {
				task = tasksVector.get(tempIndex);
				tasksVector.remove(task);
			} else {
				startIndex = 0;
				if(currentPoolSize < threadPoolSize) {
					pushInExecutor(popOutNextTask());
				}
			}
		}
		
		return task;
	}
	
	@Override
	public void run() {
		tempTask.listener.OnStateChanged(tempTask.name, tempTask.formatName, tempTask.type, tempTask.tag);
	}
	
	private class Task implements Runnable {
		
		private String name;
		private String formatName;
		private OnStateChangedListener<T> listener;
		private Object tag;
		private T type;
		private T errorType;
		private int retryCount;
		private boolean isReadFileInThread;
		
		public Task(String name, String formatName, boolean isReadFileInThread, T errorType, Object tag, OnStateChangedListener<T> listener) {
			this.name = name;
			this.formatName = formatName;
			this.isReadFileInThread = isReadFileInThread;
			this.errorType = errorType;
			this.tag = tag;
			this.listener = listener;
		}

		@Override
		public void run() {
			ZThread.setToMaxPriority();
			if (isReadFileInThread) {
				try {
					type = readFile(formatName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (type != null) {
					currentPoolSize --;
					pushInExecutor(popOutNextTask());
					putInMemoryCache(formatName, type);
					notifyListener();
				}
			}
			
			if (type == null) {
				try {
					type = load(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (type != null) {
					currentPoolSize --;
					try {
						saveFile(formatName, type);
					} catch (Exception e) {
						e.printStackTrace();
					}
					pushInExecutor(popOutNextTask());
					putInMemoryCache(formatName, type);
					notifyListener();
				} else if (retryCount < 3){
					if (retryCount == 0) {
						currentPoolSize --;
						pushInExecutor(popOutNextTask());
					}
					ZThread.sleep(retryCount * retryCount * 100);
					pushInRetryExecutor(this);
					retryCount ++;
				} else {
					putInMemoryCache(formatName, errorType);
					notifyListener();
				}
			}
		}
		
		private void notifyListener() {
			if (listener != null) {
				tempTask = this;
				if (handler == null) {
					ZBaseCacher.this.run();
				} else {
					handler.post(ZBaseCacher.this);
				}
			}
		}
	}
	
	public static interface OnStateChangedListener<T> {
		
		public abstract void OnStateChanged(String name, String formatName, T type, Object tag);
	}

}
