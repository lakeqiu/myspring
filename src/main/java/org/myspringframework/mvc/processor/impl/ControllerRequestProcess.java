package org.myspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.annotation.RequestMapping;
import org.myspringframework.mvc.annotation.RequestParam;
import org.myspringframework.mvc.annotation.ResponseBody;
import org.myspringframework.mvc.processor.RequestProcessor;
import org.myspringframework.mvc.render.ResultRender;
import org.myspringframework.mvc.render.impl.JsonResultRender;
import org.myspringframework.mvc.render.impl.ResourceNotFoundResultRender;
import org.myspringframework.mvc.render.impl.ViewResultRender;
import org.myspringframework.mvc.type.ControllerMethod;
import org.myspringframework.mvc.type.RequestPathInfo;
import org.myspringframework.util.ConverterUtil;
import org.myspringframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller请求处理器
 * @author lakeqiu
 */
@Slf4j
public class ControllerRequestProcess implements RequestProcessor {
    /**
     * IOC容器
     */
    private BeanContainer beanContainer;

    /**
     * 请求和对应Controller方法的映射
     */
    private Map<RequestPathInfo, ControllerMethod> pathInfoControllerMethodMap = new ConcurrentHashMap<>();

    /**
     * 依靠容器的能力，建立起请求路径、请求方法与Controller方法实例的映射
     */
    public ControllerRequestProcess() {
        this.beanContainer = BeanContainer.getInstance();
        // 获取被@RequestMapping标记的类
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        // 建立起请求路径、请求方法与Controller方法实例的映射
        initPathInfoControllerMethodMap(requestMappingSet);
    }

    /**
     * 建立起请求路径、请求方法与Controller方法实例的映射
     * @param requestMappingSet 被 @RequestMapping 标记的类
     */
    private void initPathInfoControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) {
            return;
        }

        // 1、遍历所有被@RequestMapping标记的类，获取类上面该注解的属性值作为一级路径
        for (Class<?> requestMappingClass : requestMappingSet) {
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            String basePath = requestMapping.value();

            // 如果路径开始字符不是“/”就添加上，方便后面匹配
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }

            // 2、遍历类里所有被@RequestMapping标记的方法，获取方法上面该注解的属性值作为二级路径
            // 获取类所有方法
            Method[] methods = requestMappingClass.getDeclaredMethods();

            // 判空处理
            if (ValidationUtil.isEmpty(methods)) {
                continue;
            }

            // 遍历所有方法
            for (Method method : methods) {
                // 该方法被@RequestMapping标记的话就进行处理
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodRequest.value();

                    // 如果路径开始字符不是“/”就添加上，方便后面匹配
                    if (!methodPath.startsWith("/")) {
                        methodPath = "/" + methodPath;
                    }

                    // 拼接完整请求路径
                    String url = basePath + methodPath;

                    // 3、解析方法里被@RequestParam标记的参数
                    // 获取该注解的属性值，作为参数名
                    // 获取被标记的参数的数据类型，建立参数名和参数类型的映射

                    // 存放参数名和该参数类型的映射关系
                    Map<String, Class<?>> methodParam = new HashMap<>();

                    Parameter[] parameters = method.getParameters();
                    if (!ValidationUtil.isEmpty(parameters)) {
                        // 遍历参数
                        for (Parameter parameter : parameters) {
                            RequestParam param = parameter.getAnnotation(RequestParam.class);
                            // 目前暂定为Controller方法里面所有参数都需要标注@RequestParam注解
                            if (param == null) {
                                throw new RuntimeException("参数必须标注 @RequestParam");
                            }

                            methodParam.put(param.value(), parameter.getType());
                        }
                    }

                    // 4、将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放置到映射表中
                    // GET 或 POST 方法
                    String httpMethod = String.valueOf(methodRequest.method());
                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);
                    if (this.pathInfoControllerMethodMap.containsKey(requestPathInfo)) {
                        log.warn("请求路径{}， 请求类{}， 请求方法{} 被覆盖了",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }
                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParam);

                    this.pathInfoControllerMethodMap.put(requestPathInfo, controllerMethod);
                }
            }
        }
    }

    /**
     * 以责任链模式处理请求，返回 true 则继续调用下一个处理器处理
     *
     * @param requestProcessorChain 责任链
     * @return boolean
     * @throws Exception 处理过程中抛出的异常
     */
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1、解析HTTPServletRequest的请求方法，请求路径，获取对应的ControllerMethod实例
        String requestMethod = requestProcessorChain.getRequestMethod();
        String requestPath = requestProcessorChain.getRequestPath();
        RequestPathInfo requestPathInfo = new RequestPathInfo(requestMethod, requestPath);
        ControllerMethod controllerMethod = this.pathInfoControllerMethodMap.get(requestPathInfo);
        // 没有可以处理此路径的资源
        if (controllerMethod == null) {
            // 设置资源找不到时的渲染器
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(requestMethod, requestPath));
            return false;
        }

        // 2、解析请求参数，并传递到controllerMethod中执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());

        // 3、根据处理的结果，选择对应的渲染器
        setResultRender(result, controllerMethod, requestProcessorChain);

        return true;
    }

    /**
     * 根据处理的结果，选择对应的渲染器
     * @param result 结果
     * @param controllerMethod controllerMethod
     * @param requestProcessorChain 请求处理链
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        // 结果为空，这里不需要设置渲染器
        if (result == null) {
            return;
        }

        ResultRender resultRender;

        // 判断执行的方法是否被@ResponseBody标注
        boolean isJson = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        // 是的话使用JsonResultRender
        if (isJson) {
            resultRender = new JsonResultRender(result);
        } else {
            // 不是的话使用ViewResultRender
            resultRender = new ViewResultRender(result);
        }

        // 设置渲染器
        requestProcessorChain.setResultRender(resultRender);
    }

    /**
     * 解析请求参数，并传递到controllerMethod中执行对应方法
     * @param controllerMethod controllerMethod
     * @param request request
     * @return 方法执行结果
     */
    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        // 1、从请求里获取GET 或 POST 的参数名及其对应的值
        Map<String, String> requestParamMap = new HashMap<>();

        // GET、POST方法的请求参数获取方式
        // Map<参数名, 参数值>
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
            if (!ValidationUtil.isEmpty(parameter.getValue())) {
                // 只支持一个参数对应一个值的方式
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }

        // 2、根据获取到的请求参数名及其对应的值，以及controllerMethod里面的参数和类型的映射关系，去实例化出方法对应的参数
        // 存放方法参数的实例
        List<Object> methodParams = new ArrayList<>();
        // Map<参数名, 参数类型>
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();
        for (String paramName : methodParamMap.keySet()) {
            Class<?> paramType = methodParamMap.get(paramName);
            String paramValue = requestParamMap.get(paramName);
            Object value;

            // 根据参数值创建对应实例，目前只支持String 和继承类型及其包装类型
            // 参数值为空
            if (paramValue == null) {
                // 创建该类型参数默认值
                value = ConverterUtil.primitiveNull(paramType);
            } else {
                value = ConverterUtil.convert(paramType, paramValue);
            }

            // 加入参数列表中
            methodParams.add(value);
        }

        // 3、执行Controller里对应的方法并返回结果
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        Method invokeMethod = controllerMethod.getInvokeMethod();
        invokeMethod.setAccessible(true);

        Object result;
        // 执行方法
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (InvocationTargetException e) {
            // 如果是调用异常的话,需要通过 e.getTargetException0
            // 去获取执行方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
