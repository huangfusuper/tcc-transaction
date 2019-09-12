package org.mengyun.tcctransaction;

import java.io.Serializable;

/**
 * 在Participant中使用InvocationContext把事务参与者的confirm和cancel方法的元数据保存下来
 * 事务参与者的元数据
 * Created by changmingxie on 11/9/15.
 */
public class InvocationContext implements Serializable {

    private static final long serialVersionUID = -7969140711432461165L;
    /**
     * 目标Class
     */
    private Class targetClass;
    /**
     * 目标方法名
     */
    private String methodName;
    /**
     * 目标参数列表
     */
    private Class[] parameterTypes;
    /**
     * 实际参数列表
     */
    private Object[] args;

    public InvocationContext() {

    }

    public InvocationContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.targetClass = targetClass;
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }
}
