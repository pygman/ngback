package com.pygman.ngback.constants;

/**
 * 消息类型定义
 * Created by kevin  on 16/8/26.
 */
public enum MessageType {

    SERVICE_REQ((byte) 1, "业务请求消息"),
    SERVICE_RESP((byte) 2, "业务响应消息"),
    LOGIN_REQ((byte) 3, "登录请求消息"),
    LOGIN_RESP((byte) 4, "登录响应消息"),
    LOGIN_RESP_REPEAT((byte) 5, "重复账号登录"),
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
    CLIENT_REGISTER_RESP_REPEAT((byte) 99, "客户端注册重复账号"),
    SERVER_TABLE_UPDATE((byte) 100, "通知客户端更新服务器列表");


    private byte value;

    private String comment;

    MessageType(byte value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public byte value() {
        return this.value;
    }

    private String comment() {
        return this.comment;
    }
}
