package io.github.linxiaobaixcg.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lcq
 * @description: 请求对象
 * @date 2021/3/4 11:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求对象ID
     * 用于验证请求和响应是否匹配
     */
    private String requestId;

    /** 类名 */
    private String className;

    /** 方法名 */
    private String methodName;

    /** 参数类型 */
    private Class<?>[] parameterTypes;

    /** 入参 */
    private Object[] parameters;

    /**
     * 版本号
     */
    private String version;
}
