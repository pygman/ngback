package com.pygman.ngback.struct;

import com.pygman.ngback.constants.MessageType;

/**
 * Message 工厂
 * Created by pygman on 16-10-27.
 */
public class MESSAGES {

    /*
    SERVICE_REQ((byte) 0, "业务请求消息"),
    SERVICE_RESP((byte) 1, "业务响应消息"),
    LOGIN_REQ((byte) 2, "登录请求消息"),
    LOGIN_RESP((byte) 3, "登录响应消息"),
    LOGIN_RESP_REPEAT((byte) 33, "重复账号登录"),
    HEARTBEAT_REQ((byte) 40, "心跳请求消息"),
    HEARTBEAT_SERVER_REQ((byte) 41, "Server注册心跳请求消息"),
    HEARTBEAT_CLIENT_REQ((byte) 42, "Client注册心跳请求消息"),
    HEARTBEAT_RESP((byte) 50, "心跳响应消息"),
    HEARTBEAT_SERVER_RESP((byte) 51, "Server注册心跳响应消息"),
    HEARTBEAT_CLIENT_RESP((byte) 52, "Client注册心跳响应消息"),

    SERVER_REGISTER_REQ((byte) 6, "服务端注册请求消息"),
    SERVER_REGISTER_RESP((byte) 7, "服务端注册响应消息"),
    SERVER_REGISTER_RESP_REPEAT((byte) 77, "服务端注册重复账号"),
    CLIENT_REGISTER_REQ((byte) 8, "客户端注册请求消息"),
    CLIENT_REGISTER_RESP((byte) 9, "客户端注册响应消息"),
    CLIENT_REGISTER_RESP_REPEAT((byte) 99, "客户端注册重复账号");
     */

    private static Message M(MessageType messageType, String body, long messageId) {
        Message message = new Message();
        message.setType(messageType.value());
        message.setId(messageId);
        message.setBody(body);
        return message;
    }

    /**
     * from to body
     *
     * @param messageType
     * @param body
     * @return
     */
    private static Message M(MessageType messageType, String from, String to, String body) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setType(messageType.value());
        message.setBody(body);
        return message;
    }

    private static Message M(MessageType messageType, String body) {
        Message message = new Message();
        message.setType(messageType.value());
        message.setBody(body);
        return message;
    }

    private static Message M(MessageType messageType, Message message) {
        Message newMessage = new Message(message.getId());
        newMessage.setFrom(message.getFrom());
        newMessage.setTo(message.getTo());
        newMessage.setType(messageType.value());
        newMessage.setBody(message.getBody());
        return newMessage;
    }

    private static Message M(MessageType messageType) {
        Message message = new Message();
        message.setType(messageType.value());
        return message;
    }

    /**
     * 代发请求
     *
     * @param body
     * @return
     */
    public static Message SERVICE_REQ(String from, String to, String body) {

        return M(MessageType.SERVICE_REQ, from, to, body);
    }

    public static Message SERVICE_RESP(Message message) {
        return M(MessageType.SERVICE_RESP, message);
    }

    public static Message SERVICE_RESP(String resp, long messageId) {
        return M(MessageType.SERVICE_RESP, resp, messageId);
    }

    public static Message LOGIN_REQ(String name) {
        return M(MessageType.LOGIN_REQ, name);
    }

    public static Message LOGIN_RESP() {
        return M(MessageType.LOGIN_RESP);
    }

    public static Message LOGIN_RESP(String serverReg) {
        return M(MessageType.LOGIN_RESP, serverReg);
    }

    public static Message LOGIN_RESP_REPEAT() {
        return M(MessageType.LOGIN_RESP_REPEAT);
    }

    public static Message HEARTBEAT_REQ() {
        return M(MessageType.HEARTBEAT_REQ);
    }

    public static Message HEARTBEAT_RESP() {
        return M(MessageType.HEARTBEAT_RESP);
    }

    public static Message HEARTBEAT_SERVER_REQ() {
        return M(MessageType.HEARTBEAT_SERVER_REQ);
    }

    public static Message HEARTBEAT_SERVER_RESP() {
        return M(MessageType.HEARTBEAT_SERVER_RESP);
    }

    public static Message HEARTBEAT_SERVER_RESP(String regMessage) {
        return M(MessageType.HEARTBEAT_SERVER_RESP, regMessage);
    }

    public static Message HEARTBEAT_CLIENT_REQ() {
        return M(MessageType.HEARTBEAT_CLIENT_REQ);
    }

    public static Message HEARTBEAT_CLIENT_RESP() {
        return M(MessageType.HEARTBEAT_CLIENT_RESP);
    }

    public static Message HEARTBEAT_CLIENT_RESP(String serverTable) {
        return M(MessageType.HEARTBEAT_CLIENT_RESP, serverTable);
    }

    public static Message SERVER_REGISTER_REQ(String serverReg) {
        return M(MessageType.SERVER_REGISTER_REQ, serverReg);
    }

    public static Message SERVER_REGISTER_RESP(String regMessage) {
        return M(MessageType.SERVER_REGISTER_RESP, regMessage);
    }

    public static Message SERVER_REGISTER_RESP_REPEAT(String regMessage) {
        return M(MessageType.SERVER_REGISTER_RESP_REPEAT, regMessage);
    }


    public static Message CLIENT_REGISTER_FORCE_REQ(String clientName) {
        Message message = new Message();
        message.setType(MessageType.CLIENT_REGISTER_REQ.value());
        message.setStatus((byte) 1);
        message.setBody(clientName);
        return message;
    }

    public static Message CLIENT_REGISTER_REQ(String clientName) {
        return M(MessageType.CLIENT_REGISTER_REQ, clientName);
    }

    public static Message CLIENT_REGISTER_RESP(String regMessage) {
        return M(MessageType.CLIENT_REGISTER_RESP, regMessage);
    }

    public static Message CLIENT_REGISTER_RESP_REPEAT(String regMessage) {
        return M(MessageType.CLIENT_REGISTER_RESP_REPEAT, regMessage);
    }

    public static Message SERVER_TABLE_UPDATE(String regMessage) {
        return M(MessageType.SERVER_TABLE_UPDATE, regMessage);
    }

}
