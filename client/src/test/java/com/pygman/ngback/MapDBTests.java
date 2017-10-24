package com.pygman.ngback;

import com.pygman.ngback.struct.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by pygman on 12/7/2016.
 */
public class MapDBTests {

    DB db;

    BTreeMap<Long, byte[]> bTreeMap;

    @Before
    public void before() {
        db = DBMaker.fileDB("D:\\data\\test.db").checksumHeaderBypass().fileMmapEnable().make();

        bTreeMap = db.treeMap("main", Serializer.LONG, Serializer.BYTE_ARRAY).createOrOpen();
    }

    @After
    public void after() {
        //        db.commit();

//        Runtime.getRuntime().addShutdownHook(new Thread(db::close));
    }

    @Test
    public void head() throws UnsupportedEncodingException {
        while (!bTreeMap.isEmpty()) {
            Map.Entry<Long, byte[]> longEntry = bTreeMap.firstEntry();
            System.out.println(longEntry.getKey() + " ==> " + Message.deserialize(longEntry.getValue()));
            bTreeMap.remove(longEntry.getKey());
        }

    }

    @Test
    public void testPoll(){
        bTreeMap.put(1L,new byte[1]);
        Map.Entry<Long, byte[]> longEntry = bTreeMap.pollFirstEntry();
        System.out.println(longEntry);
        Map.Entry<Long, byte[]> longEntry2 = bTreeMap.pollFirstEntry();
        System.out.println(longEntry2);

    }

    @Test
    public void testName(){
        DB db = DBMaker.fileDB("D:\\data\\name.db").checksumHeaderBypass().fileMmapEnable().make();

        BTreeMap<Long, byte[]> bTreeMap = db.treeMap("main", Serializer.LONG, Serializer.BYTE_ARRAY).createOrOpen();
        Iterator<Map.Entry<Long, byte[]>> entryIterator1 = bTreeMap.entryIterator();
        while (entryIterator1.hasNext()){
            Map.Entry<Long, byte[]> next = entryIterator1.next();
            System.out.println(next.getKey()+"=>"+next.getValue());
        }
//        bTreeMap.put(1L,new byte[]{1});
        BTreeMap<Long, byte[]> bTreeMap2 = db.treeMap("name", Serializer.LONG, Serializer.BYTE_ARRAY).createOrOpen();
        Iterator<Map.Entry<Long, byte[]>> entryIterator = bTreeMap2.entryIterator();
        while (entryIterator.hasNext()){
            Map.Entry<Long, byte[]> next = entryIterator.next();
            System.out.println(next.getKey()+"=>"+next.getValue());
        }

    }

    public static void main(String[] args) {
        DB db = DBMaker.fileDB("D:\\data\\test.db").checksumHeaderBypass().fileMmapEnable().make();

        BTreeMap<Long, byte[]> bTreeMap = db.treeMap("main", Serializer.LONG, Serializer.BYTE_ARRAY).createOrOpen();
//        bTreeMap.put(1L,new byte[]{1});
        new Thread(() -> {
            while (true) {

                while (bTreeMap.isEmpty()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(3000);
//                        System.out.println("sleep");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Map.Entry<Long, byte[]> firstEntry = bTreeMap.firstEntry();
                System.out.println("\n" + firstEntry.getKey()+"=>"+new String(firstEntry.getValue()));
                bTreeMap.remove(firstEntry.getKey());
            }
        }).start();

        long i = 0;
        Scanner scanner = new Scanner(System.in);
        while (true){
            String s = scanner.next();
            bTreeMap.put(i, s.getBytes());
            i++;
        }
    }

    @Test
    public void test() throws UnsupportedEncodingException {

        Iterator<Map.Entry<Long, byte[]>> entryIterator = bTreeMap.entryIterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long, byte[]> next = entryIterator.next();
            System.out.println(next.getKey());
            System.out.println(Message.deserialize(next.getValue()));
        }


        for (long i = 0; i < 1000; i++) {
            Message message = new Message();
            message.setFrom("from" + i);
            message.setTo("to" + i);
            message.setBody("body" + i);
            message.setType((byte) 0);
            message.setStatus((byte) 0);
            bTreeMap.put(message.getId(), message.serialize());
        }
    }

    @Test
    public void aTest() {
        Queue<String> queue = new LinkedList<>();
//        queue.element()
    }

}
