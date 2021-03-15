package io.github.linxiaobaixcg.model;

import io.github.linxiaobaixcg.enums.MessageType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lcq
 * @description: 响应对象
 * @date 2021/3/4 11:33
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应ID */
    private String requestId;

    /** 信息 */
    private String msg;

    /** 返回实体 */
    private Object result;

    /** 消息类型 */
    private MessageType messageType;
}
