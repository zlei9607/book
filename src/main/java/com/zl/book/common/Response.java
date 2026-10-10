package com.zl.book.common;

/**
 * @author zhanglei
 */
public class Response<T> {

    private String msg;
    private T data;
    private int code;

    public Response(String msg, T data, int code) {
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public Response(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static <T> Response<T> success() {
        return new Response<>("操作成功", 10000);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>("操作成功", data, 10000);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(msg, 90000);
    }

}

