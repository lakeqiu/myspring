package org.myspringframework.aop.aspect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.myspringframework.aop.PointcutLocator;

/**
 * Aspect（切面）的详细
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

    private PointcutLocator pointcutLocator;
}
