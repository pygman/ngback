package com.pygman.ngback.struct;

import com.pygman.ngback.util.TheTime;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 自定义消息结构
 * <p>
 * long id
 * <p>
 * byte type
 * <p>
 * (fromLength)
 * string from
 * <p>
 * (toLength)
 * string to
 * <p>
 * (bodyLength)
 * string body
 * <p>
 * byte status
 * <p>
 * Created by kevin  on 16/8/26.
 */
public class Message implements Serializable {

    public byte[] serialize() {
        int length = 22;
        byte[] fromBytes = null;
        if (from != null) {
            try {
                fromBytes = from.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            length += fromBytes.length;
        }
        byte[] toBytes = null;
        if (to != null) {
            try {
                toBytes = to.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            length += toBytes.length;
        }
        byte[] bodyBytes = null;
        if (body != null) {
            try {
                bodyBytes = body.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            length += bodyBytes.length;
        }
        byte[] bytes = new byte[length];
        int i = 9;
        System.arraycopy(longToByteArray(id), 0, bytes, 0, 8);
        bytes[8] = type;

        i = writeString(bytes, fromBytes, i);
        i = writeString(bytes, toBytes, i);
        i = writeString(bytes, bodyBytes, i);

        bytes[i] = status;

        return bytes;
    }

    private int writeString(byte[] result, byte[] strBytes, int i) {
        if (strBytes == null) {
            result[i++] = 0;
            result[i++] = 0;
            result[i++] = 0;
            result[i++] = 0;
        } else {
            int strLength = strBytes.length;
            byte[] strLengthBytes = intToByteArray(strLength);
            System.arraycopy(strLengthBytes, 0, result, i, 4);
            System.arraycopy(strBytes, 0, result, i += 4, strLength);
            i += strLength;
        }
        return i;
    }

    public static void deserialize(byte[] bytes, Message message) {
        long id = byteArrayToLong(bytes, 0);
        message.setId(id);
        message.setType(bytes[8]);
        int i = 13;
        int fromLength = byteArrayToInt(bytes, 9);
        try {
            message.setFrom(new String(bytes, i, fromLength, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int toLength = byteArrayToInt(bytes, i += fromLength);
        try {
            message.setTo(new String(bytes, i += 4, toLength, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int bodyLength = byteArrayToInt(bytes, i += toLength);
        try {
            message.setBody(new String(bytes, i += 4, bodyLength, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setStatus(bytes[i + bodyLength]);

    }

    public static byte[] longToByteArray(long l) {
        return new byte[]{
                (byte) ((l >> 56) & 0xFF),
                (byte) ((l >> 48) & 0xFF),
                (byte) ((l >> 40) & 0xFF),
                (byte) ((l >> 32) & 0xFF),
                (byte) ((l >> 24) & 0xFF),
                (byte) ((l >> 16) & 0xFF),
                (byte) ((l >> 8) & 0xFF),
                (byte) (l & 0xFF)
        };
    }

    public static byte[] intToByteArray(int i) {
        return new byte[]{
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    public static byte[] shortToByteArray(short s) {
        return new byte[]{
                (byte) ((s >> 8) & 0xFF),
                (byte) (s & 0xFF)
        };
    }

    public static long byteArrayToLong(byte[] bytes, int i) {
        return ((long) bytes[i + 7] & 0xFF) |
                ((long) bytes[i + 6] & 0xFF) << 8 |
                ((long) bytes[i + 5] & 0xFF) << 16 |
                ((long) bytes[i + 4] & 0xFF) << 24 |
                ((long) bytes[i + 3] & 0xFF) << 32 |
                ((long) bytes[i + 2] & 0xFF) << 40 |
                ((long) bytes[i + 1] & 0xFF) << 48 |
                ((long) bytes[i] & 0xFF) << 56;
    }

    public static int byteArrayToInt(byte[] bytes, int i) {
        return ((bytes[i + 3] & 0xFF) |
                (bytes[i + 2] & 0xFF) << 8 |
                (bytes[i + 1] & 0xFF) << 16 |
                (bytes[i] & 0xFF) << 24);
    }

    public static short byteArrayToShort(byte[] bytes, int i) {
        return (short) ((bytes[i + 1] & 0xFF) |
                (bytes[i] & 0xFF) << 8);
//        return (short) (bytes[i] << 8 |
//                        bytes[i+1]);
    }

    public void deserializeBy(byte[] bytes) {
        deserialize(bytes, this);
    }

    public static Message deserialize(byte[] bytes) {
        Message message = new Message(0);
        deserialize(bytes, message);
        return message;
    }

    /*
    public byte[] serializeByByteBuffer() throws Exception {
        int length = 16;
        byte[] fromBytes = null;
        if (from != null) {
            fromBytes = from.getBytes("utf-8");
            length += fromBytes.length;
        }
        byte[] toBytes = null;
        if (to != null) {
            toBytes = to.getBytes("utf-8");
            length += toBytes.length;
        }
        byte[] bodyBytes = null;
        if (body != null) {
            bodyBytes = body.getBytes("utf-8");
            length += bodyBytes.length;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        byteBuffer.put(longToByteArray(id));
        byteBuffer.put(type);
        if (fromBytes == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) fromBytes.length);
            byteBuffer.put(fromBytes);
        }
        if (toBytes == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) toBytes.length);
            byteBuffer.put(toBytes);
        }
        if (bodyBytes == null) {
            byteBuffer.put((byte) 0);
        } else {
            byteBuffer.put((byte) bodyBytes.length);
            byteBuffer.put(bodyBytes);
        }
        byteBuffer.put(status);
        return byteBuffer.array();
    }

    private byte[] string2Byte(ByteBuf out, String str) throws UnsupportedEncodingException {
        if (str == null) {
            return new byte[]{0};
        } else {
            byte[] strBytes = str.getBytes("utf-8");
            out.writeShort(strBytes.length);
            out.writeBytes(strBytes);
        }
    }
    */

    public Message() {
        this.id = TheTime.newID();
    }

    public Message(long id) {
        this.id = id;
    }

    private long id;// 会话ID

    private byte type;// 消息类型

    private String from;

    private String to;

    private String body;

    private byte status;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type=" + type +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", body='" + body + '\'' +
                ", status=" + status +
                '}';
    }
}
