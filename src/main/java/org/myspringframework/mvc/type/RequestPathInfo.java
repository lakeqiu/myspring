package org.myspringframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储http请求路径和请求方法
 * @author lakeqiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPathInfo {
    /**
     * http请求方法类型，Get或Post
     */
    private String httpMethod;

    /**
     * http请求路径
     */
    private String httpPath;
}
