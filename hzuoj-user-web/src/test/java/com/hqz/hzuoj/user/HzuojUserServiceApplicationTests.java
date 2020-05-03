package com.hqz.hzuoj.user;

import com.hqz.hzuoj.bean.user.User;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

class HzuojUserServiceApplicationTests {
    private static ConcurrentHashMap<Integer,String> map = new ConcurrentHashMap();
    private static Map<Integer,String> map2 =new Hashtable<>();
    private static CountDownLatch cdl1 = new CountDownLatch(2);
    private static CountDownLatch cdl2 = new CountDownLatch(2);
    private static CountDownLatch cdl3 = new CountDownLatch(2);
    private static CountDownLatch cdl4 = new CountDownLatch(2);

    static class ConcurrentPutThread extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for(int i=0;i<10000000;i++) {
                map.put(i, String.valueOf(i));
            }
            cdl1.countDown();

        }

    }
    static class ConcurrentGetThread extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int size = map.size();
            for(int i=0;i<size;i++) {
                map.get(i);
            }
            cdl2.countDown();
        }


    }


    static class SynchronizedPutThread extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for(int i=0;i<10000000;i++) {
                map2.put(i, String.valueOf(i * i));
            }
            cdl3.countDown();
        }

    }

    static class SynchronizedGetThread extends Thread{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int size = map2.size();
            for(int i=0;i<size;i++) {
                map2.get(i);
            }
            cdl4.countDown();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Set<Integer> set = new HashSet<>();
        Set<Integer> set1 = new TreeSet<>();
        Set<Integer> set2 = new LinkedHashSet<>();
    }

}

class Thread extends java.lang.Thread {

}

