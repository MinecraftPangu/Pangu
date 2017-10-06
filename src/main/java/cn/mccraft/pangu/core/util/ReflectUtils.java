package cn.mccraft.pangu.core.util;

import cn.mccraft.pangu.core.PanguCore;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 反射工具类。
 * 如方法或变量不存在，该类下的方法都不会抛出异常。
 */
public interface ReflectUtils {
    /**
     * 设置变量
     *
     * @param ownerClass 要修改的类
     * @param owner 要修改的对象，如为静态变量时可空
     * @param name 变量名
     * @param object 要修改成的对象
     * @param breakPrivate 是否反射修改为变量可见
     */
    static void setField(Class ownerClass, Object owner, String name, Object object, boolean breakPrivate){
        try {
            // resolve break private
            Field field;
            try {
                field = breakPrivate ? ownerClass.getDeclaredField(name) : ownerClass.getField(name);
            } catch (NoSuchFieldException e){
                // check no field
                return;
            }

            // break access
            if (!field.isAccessible()) field.setAccessible(true);

            field.set(owner, object);
        } catch (Exception e) {
            PanguCore.getLogger().error(
                    String.format(
                            "Unable to set field in %s#%s typed %s, breakPrivate?%b",
                            ownerClass.getName(),
                            name,
                            object,
                            breakPrivate
                    ), e);
        }
    }

    /**
     * 获取变量的值
     *
     * @param ownerClass 要修改的类
     * @param owner 要修改的对象，如为静态变量时可空
     * @param name 变量名
     * @param typeCheck 返回类型转换
     * @param breakPrivate 是否反射修改为变量可见
     * @param <T>
     * @return
     */
    @Nullable
    static <T> T getField(Class ownerClass, Object owner, String name, Class<T> typeCheck, boolean breakPrivate) {
        try {
            // resolve break private
            Field field;
            try {
                field = breakPrivate ? ownerClass.getDeclaredField(name) : ownerClass.getField(name);
            } catch (NoSuchFieldException e){
                // check no field
                return null;
            }
            // check type
            if (typeCheck != null && !typeCheck.isAssignableFrom(field.getType())) return null;

            // break access
            if (!field.isAccessible()) field.setAccessible(true);

            Object object = field.get(owner);
            return (T) object;

        } catch (Exception e) {
            PanguCore.getLogger().error(
                    String.format(
                            "Unable to get field in %s#%s typed %s, breakPrivate?%b",
                            ownerClass.getName(),
                            name,
                            typeCheck.getName(),
                            breakPrivate
                    ), e);
        }
        return null;
    }

    @Nullable
    static <T> T getField(Object owner, String name, Class<T> typeCheck) {
        return getField(owner.getClass(), owner, name, typeCheck, false);
    }

    @Nullable
    static <T> T getField(Class ownerClass, String name, Class<T> typeCheck) {
        return getField(ownerClass, null, name, typeCheck, false);
    }

    @Nullable
    static Object getField(Object owner, String name) {
        return getField(owner.getClass(), owner, name, null, false);
    }

    @Nullable
    static Object getField(Class ownerClass, String name) {
        return getField(ownerClass, null, name, null, false);
    }

    /**
     * invoke method
     *
     * @param ownerClass method's owner
     * @param owner the invoker
     * @param name the name of method
     * @param returnTypeCheck the type of method's return, null for void
     * @param breakPrivate if set accessible to true
     * @param parameters the parameters to invoke
     * @param <T>
     * @return
     */
    @Nullable
    static <T> T invokeMethod(Class ownerClass, Object owner, String name, Class<T> returnTypeCheck, boolean breakPrivate, Object... parameters) {
        try {
            Class[] parameterTypes = toTypes(parameters);

            // resolve break private
            Method method;
            try {
                method = breakPrivate ? ownerClass.getDeclaredMethod(name, parameterTypes) : ownerClass.getMethod(name, parameterTypes);
            } catch (NoSuchMethodException e){
                return null;
            }

            // check type
            if (returnTypeCheck != null && !returnTypeCheck.isAssignableFrom(method.getReturnType())) return null;

            // break access
            if (!method.isAccessible()) method.setAccessible(true);

            Object object = method.invoke(owner, parameters);
            return (T) object;
        } catch (Exception e) {
            PanguCore.getLogger().error(
                    String.format(
                            "Unable to get field in %s#method %s, typed %s, breakPrivate?%b",
                            ownerClass,
                            name,
                            returnTypeCheck,
                            breakPrivate
                    ), e);
        }
        return null;
    }

    /*
            invoke method with one less arg
     */
    static <T> T invokeMethod(Annotation anno, String name, Class<T> returnTypeCheck, boolean breakPrivate, Object... parameters) {
        return invokeMethod(anno.annotationType(), anno, name,returnTypeCheck, breakPrivate, parameters);
    }
    static <T> T invokeMethod(Class ownerClass, String name, Class<T> returnTypeCheck, boolean breakPrivate, Object... parameters) {
        return invokeMethod(ownerClass, null, name,returnTypeCheck, breakPrivate, parameters);
    }
    static <T> T invokeMethod(Object owner, String name, Class<T> returnTypeCheck, boolean breakPrivate, Object... parameters) {
        return invokeMethod(owner.getClass(), owner, name,returnTypeCheck, breakPrivate, parameters);
    }

    /*
            invoke method with two less args
     */
    static <T> T invokeMethod(Class ownerClass, String name, Class<T> returnTypeCheck) {
        return invokeMethod(ownerClass, null, name,returnTypeCheck, false);
    }
    static <T> T invokeMethod(Object owner, String name, Class<T> returnTypeCheck) {
        return invokeMethod(owner.getClass(), owner, name, returnTypeCheck, false);
    }
    static <T> T invokeMethod(Annotation anno, String name, Class<T> returnTypeCheck) {
        return invokeMethod(anno.annotationType(), anno, name, returnTypeCheck, false);
    }

    /*
            invoke method with two args
     */
    static <T> T invokeMethod(Object owner, String name) {
        return invokeMethod(owner, name, null);
    }
    static <T> T invokeMethod(Class ownerClass, String name) {
        return invokeMethod(ownerClass, name, null);
    }
    static <T> T invokeMethod(Annotation anno, String name) {
        return invokeMethod(anno.annotationType(), anno, name, null, false);
    }


    /**
     * transforms parameters array to class array
     */
    static Class[] toTypes(Object... parameters){
        return Arrays.stream(parameters).map(parameter -> parameter.getClass()).collect(Collectors.toList()).toArray(new Class[0]);
    }

    /**
     * 无异常的 {@link Class#forName(String)}
     *
     * @return 类不存在时返回 null
     */
    static Class<?> forName(String name){
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 无异常的 {@link Class#newInstance()}
     *
     * @param name 类名
     * @return 异常时返回 null
     */
    static Object forInstance(String name){
        try {
            return forName(name).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 无异常的 {@link Class#newInstance()}
     *
     * @param clazz 创建的对象的类
     * @return 异常时返回 null
     */
    static Object forInstance(Class clazz){
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
