package com.github.argon.sos.interactions.util;

import java.lang.reflect.Method;

public class MethodUtil {
    public static boolean isGetterMethod(Method method) {
        // don't call methods with params
        if (method.getParameterCount() > 0) {
            return false;
        }
        String methodName = method.getName();

        if (methodName.startsWith("get") && methodName.length() == 3) {
            return true;
        }

        if (methodName.startsWith("is") && methodName.length() == 2) {
            return true;
        }

        if (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(2))) {
            return true;
        }

        if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
            return true;
        }

        return false;
    }
}
