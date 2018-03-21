package com.migu.data.android.logbase.thread;


import com.migu.data.android.logbase.LogBaseConstant;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by hasika on 2017/9/21.
 * 线程池
 */
public class LogBaseThreadPool {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread _thread = new Thread(r);
            _thread.setName(LogBaseConstant.Constant.THREAD_NAME);
            return _thread;
        }
    });

    public static void execute(Runnable r) {
        if (scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        scheduledExecutorService.schedule(r,0, TimeUnit.SECONDS);
    }

    public static void scheduleWithFixedDelay(Runnable r, long period) {
        if (scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        scheduledExecutorService.scheduleWithFixedDelay(r,1,period,TimeUnit.SECONDS);
    }
}
