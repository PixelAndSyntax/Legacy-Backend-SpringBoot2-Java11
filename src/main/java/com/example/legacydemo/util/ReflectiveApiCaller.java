package com.example.legacydemo.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

/**
 * Reflection-based utility that dynamically invokes deprecated and removed APIs.
 * 
 * MIGRATION CHALLENGE: Automated tools cannot detect these reflective calls.
 * This requires manual code review and testing to identify all dynamic API usage.
 * The actual methods being called are hidden from static analysis.
 */
@Component
public class ReflectiveApiCaller {
    
    /**
     * Dynamically calls Thread.stop() using reflection to hide from static analysis
     * 
     * MIGRATION NOTE: Thread.stop(Throwable) is removed in Java 17
     * This will throw NoSuchMethodException at runtime in Java 17+
     */
    public String callDeprecatedThreadStop() {
        try {
            Thread thread = new Thread(() -> {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            });
            thread.start();
            
            // Use reflection to call removed method
            Method stopMethod = Thread.class.getMethod("stop", Throwable.class);
            RuntimeException exception = new RuntimeException("Stop via reflection");
            
            // This will fail in Java 17+ with NoSuchMethodException
            stopMethod.invoke(thread, exception);
            
            return "Thread.stop() called via reflection (should not reach here in Java 17+)";
        } catch (NoSuchMethodException e) {
            return "ERROR: Thread.stop(Throwable) not found - Java 17+ detected";
        } catch (IllegalAccessException | InvocationTargetException e) {
            return "ERROR: Failed to invoke Thread.stop() - " + e.getMessage();
        }
    }
    
    /**
     * Dynamically calls Thread.destroy() using reflection
     * 
     * MIGRATION NOTE: Thread.destroy() is removed in Java 17
     */
    public String callDeprecatedThreadDestroy() {
        try {
            Thread thread = new Thread(() -> {});
            
            // Use reflection to call removed method
            Method destroyMethod = Thread.class.getMethod("destroy");
            destroyMethod.invoke(thread);
            
            return "Thread.destroy() called via reflection";
        } catch (NoSuchMethodException e) {
            return "ERROR: Thread.destroy() not found - Java 17+ detected";
        } catch (Exception e) {
            return "ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
    
    /**
     * Dynamically creates instances using reflection with complex class loading
     * 
     * MIGRATION CHALLENGE: The actual types being used are determined at runtime
     */
    public Object createInstanceDynamically(String className) {
        try {
            // Dynamic class loading - could be loading removed classes
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            return "ERROR: Class not found: " + className + " (removed in Java 17?)";
        } catch (Exception e) {
            return "ERROR: Failed to instantiate " + className + ": " + e.getMessage();
        }
    }
    
    /**
     * Attempts to create java.security.acl instances dynamically
     */
    public String createAclDynamically() {
        try {
            // Try to load removed java.security.acl.Acl interface
            Class<?> aclInterface = Class.forName("java.security.acl.Acl");
            return "ACL interface loaded: " + aclInterface.getName();
        } catch (ClassNotFoundException e) {
            return "ERROR: java.security.acl.Acl not found - removed in Java 17";
        }
    }
    
    /**
     * Uses reflection to call String.getBytes(String) deprecated method
     * 
     * MIGRATION CHALLENGE: This deprecated overload might behave differently
     * or be removed entirely in future versions
     */
    public byte[] getBytesViaReflection(String text, String encoding) {
        try {
            // Call deprecated String.getBytes(String) via reflection
            Method getBytesMethod = String.class.getMethod("getBytes", String.class);
            return (byte[]) getBytesMethod.invoke(text, encoding);
        } catch (NoSuchMethodException e) {
            // Fallback to proper method
            return text.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return ("ERROR: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Dynamically checks for sun.misc.Unsafe and invokes methods
     * 
     * MIGRATION CHALLENGE: sun.misc.Unsafe access patterns change significantly
     * in newer Java versions and may require VarHandle migration
     */
    public String accessUnsafeDynamically() {
        try {
            // Load Unsafe class dynamically
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            
            // Get the Unsafe instance via reflection
            java.lang.reflect.Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            Object unsafe = theUnsafeField.get(null);
            
            // Try to call some Unsafe methods dynamically
            Method addressSizeMethod = unsafeClass.getMethod("addressSize");
            int addressSize = (Integer) addressSizeMethod.invoke(unsafe);
            
            Method pageMethod = unsafeClass.getMethod("pageSize");
            int pageSize = (Integer) pageMethod.invoke(unsafe);
            
            return "Unsafe accessed via reflection: addressSize=" + addressSize + 
                   ", pageSize=" + pageSize;
        } catch (Exception e) {
            return "ERROR: Cannot access sun.misc.Unsafe - " + e.getMessage();
        }
    }
    
    /**
     * Complex method that chains multiple reflective calls
     * 
     * MIGRATION CHALLENGE: Requires tracing through multiple layers of reflection
     */
    public String complexReflectiveChain() {
        StringBuilder result = new StringBuilder();
        
        // Chain 1: Call deprecated thread methods
        result.append("1. Thread API: ").append(callDeprecatedThreadStop()).append("\n");
        
        // Chain 2: Try to load removed security classes
        result.append("2. ACL API: ").append(createAclDynamically()).append("\n");
        
        // Chain 3: Access Unsafe
        result.append("3. Unsafe API: ").append(accessUnsafeDynamically()).append("\n");
        
        // Chain 4: Try to load applet classes
        try {
            Class<?> appletClass = Class.forName("java.applet.Applet");
            result.append("4. Applet API: Found ").append(appletClass.getName()).append("\n");
        } catch (ClassNotFoundException e) {
            result.append("4. Applet API: Not found (removed in Java 17)\n");
        }
        
        return result.toString();
    }
    
    /**
     * Uses method handles with removed APIs - even more hidden from analysis
     */
    public String useMethodHandles() {
        try {
            java.lang.invoke.MethodHandles.Lookup lookup = 
                java.lang.invoke.MethodHandles.lookup();
            
            // Try to get method handle for removed Thread.stop method
            java.lang.invoke.MethodType mt = 
                java.lang.invoke.MethodType.methodType(void.class, Throwable.class);
            
            java.lang.invoke.MethodHandle mh = 
                lookup.findVirtual(Thread.class, "stop", mt);
            
            return "MethodHandle created for Thread.stop - will fail in Java 17+";
        } catch (NoSuchMethodException e) {
            return "ERROR: Thread.stop method not found via MethodHandle";
        } catch (IllegalAccessException e) {
            return "ERROR: Cannot access Thread.stop via MethodHandle";
        }
    }
    
    /**
     * Dynamic principal creation that may use removed security APIs
     */
    public Principal createPrincipalDynamically(String name) {
        // Try multiple strategies, some using removed APIs
        try {
            // Strategy 1: Try javax.security.cert.X509Certificate's principal
            Class<?> x509Class = Class.forName("javax.security.cert.X509Certificate");
            // This class is removed in Java 17
            return () -> "Principal from removed javax.security.cert API: " + name;
        } catch (ClassNotFoundException e) {
            // Fallback to simple principal
            return () -> name;
        }
    }
}
