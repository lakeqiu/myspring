package org.myspringframework.aop.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Aspect（切面）的详细详细
 * @author lakeqiu
 */
@AllArgsConstructor
@Getter
public class AspectInfo {
    /**
     * 执行顺序
     */
    private int orderIndex;
    /**
     * 执行对象
     */
    private DefaultAspect aspectObject;
}
