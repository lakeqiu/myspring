package org.myspringframework.util;

/**
 * @author lakeqiu
 */
public class ConverterUtil {
    /**
     * 返回基本数据类型的默认值（空值）
     * @param paramType 参数类型
     * @return 参数类型实例空值
     */
    public static Object primitiveNull(Class<?> paramType) {
        if (paramType == int.class || paramType == double.class ||
                paramType == float.class || paramType == long.class ||
                paramType == byte.class || paramType == short.class) {
            return 0;
        } else if (paramType == boolean.class) {
            return false;
        }

        return null;
    }

    /**
     * String 类型值转化为对应参数类型
     * @param paramType 参数类型
     * @param paramValue 值
     * @return 参数类型实例
     */
    public static Object convert(Class<?> paramType, String paramValue) {
        if(isPrimitive(paramType)){
            if(ValidationUtil.isEmpty(paramValue)){
                return primitiveNull(paramType);
            }
            if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
                return Integer.parseInt(paramValue);
            } else if (paramType.equals(String.class)) {
                return paramValue;
            } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
                return Double.parseDouble(paramValue);
            } else if (paramType.equals(Float.class) || paramType.equals(float.class)) {
                return Float.parseFloat(paramValue);
            } else if (paramType.equals(Long.class) || paramType.equals(long.class)) {
                return Long.parseLong(paramValue);
            } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
                return Boolean.parseBoolean(paramValue);
            } else if (paramType.equals(Short.class) || paramType.equals(short.class)) {
                return Short.parseShort(paramValue);
            } else if (paramType.equals(Byte.class) || paramType.equals(byte.class)) {
                return Byte.parseByte(paramValue);
            }
            return paramValue;

        } else {
            throw new RuntimeException("暂时只支持基础数据类型转化");
        }
    }

    /**
     * 判定是否基本数据类型(包括包装类以及String)
     *
     * @param type 参数类型
     * @return 是否为基本数据类型
     */
    private static boolean isPrimitive(Class<?> type) {
        return type == boolean.class
                || type == Boolean.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == String.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class;
    }
}
