package com.pygman.ngback.constants;

/**
 * 预置返回状态
 * Created by pygman on 16-10-14.
 */
public enum ResponseStatus {

    CLIENT_OFFLINE(404, "客户机离线", "", "", ""),
    MESSAGE_SEND_ERROR(500, "信息发送异常", "", "", ""),
    SERVICE_NOT_FOUND(404, "未发现服务或服务异常", "", "", "");


    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String path;

    ResponseStatus(Integer status, String error, String exception, String message, String path) {
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
    }

    @Override
    public String toString() {
        Long timestamp = System.currentTimeMillis();
        return "{" +
                "\"timestamp\":" + timestamp +
                ",\"status\":" + status +
                ",\"error\":\"" + error + '\"' +
                ",\"exception\":\"" + exception + '\"' +
                ",\"message\":\"" + message + '\"' +
                ",\"path\":\"" + path + '\"' +
                '}';
    }
}
