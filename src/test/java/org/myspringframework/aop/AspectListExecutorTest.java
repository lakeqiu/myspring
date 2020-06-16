package org.myspringframework.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myspringframework.aop.aspect.AspectInfo;
import org.myspringframework.aop.mock.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lakeqiu
 */
public class AspectListExecutorTest {

    @DisplayName("AspectList排序：sortAspectList")
    @Test
    public void sortTest() {
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        aspectInfoList.add(new AspectInfo(3, new Mock1(), null));
        aspectInfoList.add(new AspectInfo(5, new Mock2(), null));
        aspectInfoList.add(new AspectInfo(2, new Mock3(), null));
        aspectInfoList.add(new AspectInfo(4, new Mock4(), null));
        aspectInfoList.add(new AspectInfo(1, new Mock5(), null));

        AspectListExecutor aspectListExecutor = new AspectListExecutor(AspectListExecutorTest.class, aspectInfoList);
        List<AspectInfo> sortAspectInfoList = aspectListExecutor.getSortAspectInfoList();
        for (AspectInfo aspectInfo : sortAspectInfoList) {
            System.out.println(aspectInfo.getAspectObject().getClass().getName());
        }
    }
}
