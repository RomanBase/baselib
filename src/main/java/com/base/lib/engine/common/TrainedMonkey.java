package com.base.lib.engine.common;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseRenderable;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class TrainedMonkey {

    public static <T> T[] arrayUp(T[] src, T add) {

        if (src == null) {
            throw new RuntimeException("Monkeys can't copy data from null source array");
        }

        @SuppressWarnings("unchecked")
        T[] temp = (T[]) src.getClass().cast(Array.newInstance(src.getClass().getComponentType(), src.length + 1));

        System.arraycopy(src, 0, temp, 0, src.length);
        temp[src.length] = add;

        return temp;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayClear(T[] array, Object remove) {

        List<T> list = Arrays.asList(array);
        list.removeAll(Collections.singleton(remove));

        return (T[]) list.toArray();
    }

    public static String[] toStringArray(List<String> list) {

        int count = list.size();
        String[] array = new String[count];
        for (int i = 0; i < count; i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    public static void print(Object[] array, String stringOffset) {

        if (stringOffset == null) {
            stringOffset = "";
        }

        StringBuilder sb = new StringBuilder();
        for (Object o : array) {
            sb.append(o.toString());
            sb.append(stringOffset);
        }

        Base.logD(sb.toString());
    }

    public static String readableSizeFormat(long sizeInBytes) {

        if (sizeInBytes <= 0) return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));

        return new DecimalFormat("#,##0.###").format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void run(Runnable action) {

        new Thread(action, "Monkey's Action").start();
    }

    public static void run(final Runnable action, long millisecDelay) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(action, "Monkey's Action").start();
            }
        };

        Base.activity.runOnUiThread(runnable, millisecDelay);
    }

    public static void wait(Object o, long millis) {

        synchronized (o) {
            try {
                o.wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void wait(Object o) {

        synchronized (o) {
            try {
                o.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sleep(Thread t, long millis) {

        try {
            t.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void notify(Object o) {

        synchronized (o) {
            o.notifyAll();
        }
    }

    public static BaseRenderable emptyRenderable() {

        return new BaseRenderable() {
            @Override
            public void draw() {

            }

            @Override
            public void update() {

            }

            @Override
            public void destroy() {

            }
        };
    }

    public static BaseRenderable actionRenderable(final Runnable action) {

        return action == null ? emptyRenderable() : new BaseRenderable() {
            @Override
            public void draw() {

                action.run();
            }

            @Override
            public void update() {

            }

            @Override
            public void destroy() {

            }
        };
    }

}
