package io.github.linxiaobaixcg.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lcq
 * @description: 响应对象
 * @date 2021/3/4 11:33
 */
@Data
public class RpcResponse<T> implements Serializable {

    /** 响应ID */
    private String requestId;

    /** 状态码 */
    private Integer code;

    /** 信息 */
    private String msg;

    /** 返回实体 */
    private T result;

    /**
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据泛型
     *
     * @return RpcResponse
     */
    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(ResponseCode.SUCCESS.getValue());
        if (null != data) {
            response.setResult(data);
        }
        return response;
    }

    /**
     * 失败响应
     * @param responseCode 响应码枚举
     * @param errorMessage 错误消息
     * @param <T> 泛型
     *
     * @return RpcResponse
     */
    public static <T> RpcResponse<T> fail(ResponseCode responseCode, String errorMessage) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(responseCode.getValue());
        response.setMsg(errorMessage);
        return response;
    }
}
