package com.pygman.ngback;

import com.pygman.ngback.struct.Message;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteArrayTests {

    Message message;
    @Before
    public void before(){
        message = new Message();
//        message.setFrom("from");
        message.setType((byte) 1);
//        message.setTo("to");
        message.setBody("");
        message.setStatus((byte) 0);
    }

    @Test
    public void theTest() throws UnsupportedEncodingException {

        System.out.println(message);

        byte[] serialize = message.serialize();
        Message deserialize = Message.deserialize(serialize);
        System.out.println(deserialize);

    }

    @Test
    public void testShort(){
        byte[] bytes = Message.shortToByteArray((short) 4);
        System.out.println(Arrays.toString(bytes));
    }

    @Test
    public void test() throws Exception {

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            byte[] serialize = message.serialize();
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    @Test
    public void test2() throws Exception {

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
//            byte[] serialize = message.serializeByByteBuffer();
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    @Test
    public void test3(){

        Message message = new Message();

        System.out.println(Short.MAX_VALUE);

        byte[] bytes = message.shortToByteArray(Short.MAX_VALUE);
        System.out.println(Arrays.toString(bytes));

        short i = message.byteArrayToShort(bytes, 0);
        System.out.println(i);

    }

    @Test
    public void test4(){
        Message message = new Message();

        byte[] bytes = message.longToByteArray(Long.MAX_VALUE-100);
        System.out.println(Arrays.toString(bytes));

        long l = message.byteArrayToLong(bytes, 0);
        System.out.println(Long.MAX_VALUE-100);
        System.out.println(l);



    }

    @Test
    public void test5(){
        byte[] bytes = intToByteArray(Integer.MAX_VALUE);
        int i = byteArrayToInt(bytes);
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Arrays.toString(bytes));
        System.out.println(i);
    }

    @Test
    public void test6(){

        int i = 0;
        System.out.println(i+=4);
        System.out.println(i+=4);
        System.out.println(i+=4);
        System.out.println(i+=4);
        System.out.println(i);

    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

}
