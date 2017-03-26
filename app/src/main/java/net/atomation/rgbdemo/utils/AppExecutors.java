package net.atomation.rgbdemo.utils;
import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * various Executor Services to be used in order to give work to worker threads
 */
public final class AppExecutors {

    private static final ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder().setDaemon(false).setPriority(Thread.MIN_PRIORITY);

    public static final ExecutorService QUEUE_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactoryBuilder.setNameFormat("Queue" + "-Executor-Th-%1$d").build());

    public static final ScheduledExecutorService SCHEDULED_QUEUE_EXECUTOR = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), threadFactoryBuilder.setNameFormat("ScheduledQueue" + "-Executor-Th-%1$d").build());

    public static final ExecutorService SERIAL_EXECUTOR = Executors.newFixedThreadPool(1, threadFactoryBuilder.setNameFormat("Serial" + "-Executor-Th-%1$d").build());

    public static final ScheduledExecutorService SCHEDULED_SERIAL_EXECUTOR = Executors.newSingleThreadScheduledExecutor(threadFactoryBuilder.setNameFormat("ScheduledSerial" + "-Executor-Th-%1$d").build());

    public static final Handler UI_THREAD = new Handler(Looper.getMainLooper());

}
