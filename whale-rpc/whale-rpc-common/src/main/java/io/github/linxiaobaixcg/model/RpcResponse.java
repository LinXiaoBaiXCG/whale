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
}
