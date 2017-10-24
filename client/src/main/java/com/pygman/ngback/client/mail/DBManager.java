package com.pygman.ngback.client.mail;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.serializer.GroupSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.TimeUnit;

/**
 * 持久化管理
 * Created by pygman on 12/8/2016.
 */
@Component
public class DBManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.db}")
    private String dbPath;

    @Value("${ng.client.name}")
    private String clientName;

    private DB db;

    public BTreeMap<Long, byte[]> makeMap(String dbName) {
        return makeMap(dbName, Serializer.LONG_DELTA, Serializer.BYTE_ARRAY);
    }

    public <K, V> BTreeMap<K, V> makeMap(String dbName, GroupSerializer<K> keySerializer, GroupSerializer<V> valueSerializer) {
        return db.treeMap(dbName, keySerializer, valueSerializer)
                .createOrOpen();
    }

    public static class DBMap<K, V> {
        private BTreeMap<K, V> mailMap;

        public void setMap(BTreeMap<K, V> map) {
            mailMap = map;
        }

        public V insert(K k, V v) {
            return mailMap.put(k, v);
        }

        public boolean containsKey(K k) {
            return mailMap.containsKey(k);
        }

        public Map.Entry<K, V> firstEntry() {
            return mailMap.firstEntry();
        }

        public boolean offer(K k, V v) {
            return mailMap.put(k, v) != null;
        }

        public Map.Entry<K, V> poll() {
            while (mailMap.isEmpty()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
//                        System.out.println("sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return mailMap.pollFirstEntry();
        }

        public ConcurrentNavigableMap get(K toKey) {
            return mailMap.headMap(toKey);
        }

        public ConcurrentNavigableMap get(K fromKey, K toKey) {
            return mailMap.subMap(fromKey, toKey);
        }

        public V put(K k, V v) {
            return mailMap.put(k, v);
        }

        public void remove(K k) {
            mailMap.remove(k);
        }

        public ConcurrentNavigableMap<K, V> headMap(K toKey){
            return mailMap.headMap(toKey);
        }

        public void test() {

        }
    }

    @PostConstruct
    public void init() {
        if (dbPath == null || "default".equals(dbPath)) {
            String user_home = System.getProperty("user.home");
            dbPath = user_home + "/.ng/"+clientName+".db";
        }

        File dbFile = new File(dbPath);
        File parentFile = dbFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        db = DBMaker.fileDB(dbFile).checksumHeaderBypass().fileMmapEnable().make();
        /*TODO
        faultMap = db.treeMap("fault", Serializer.LONG, Serializer.STRING)
                .createOrOpen();
                */
    }

    @PreDestroy
    public void close() {
        //TODO
        logger.info("系统关闭,持久化缓存数据");
        /*TODO
        faultMap.close();
        */
        db.close();
    }

}
