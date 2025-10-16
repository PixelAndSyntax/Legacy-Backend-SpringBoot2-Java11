package com.example.legacydemo.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Complex servlet filter with deep javax.servlet integration.
 * 
 * MIGRATION CHALLENGE: This requires careful migration to jakarta.servlet.*
 * Additionally uses deprecated session tracking modes and complex filter chains
 * that may behave differently in Jakarta Servlet 6.0
 * 
 * NOTE: Not annotated with @Component - registered via ServletFilterConfig instead
 */
public class LegacySecurityFilter implements Filter {
    
    private FilterConfig filterConfig;
    private final Map<String, SessionData> sessionRegistry = new ConcurrentHashMap<>();
    
    /**
     * Complex session tracking data structure
     */
    private static class SessionData {
        String sessionId;
        Principal principal;
        long createdTime;
        long lastAccessTime;
        Map<String, Object> attributes;
        
        SessionData(String sessionId) {
            this.sessionId = sessionId;
            this.createdTime = System.currentTimeMillis();
            this.lastAccessTime = createdTime;
            this.attributes = new HashMap<>();
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        
        // Complex initialization with javax.servlet specific features
        ServletContext context = filterConfig.getServletContext();
        
        // Uses deprecated method - getMajorVersion/getMinorVersion behavior changes
        int majorVersion = context.getMajorVersion();
        int minorVersion = context.getMinorVersion();
        
        System.out.println("Initializing LegacySecurityFilter for Servlet " + 
                         majorVersion + "." + minorVersion);
        
        // Set context attributes using javax.servlet APIs
        context.setAttribute("security.filter.initialized", true);
        context.setAttribute("security.filter.version", "1.0-javax");
        
        // Complex session configuration
        Set<SessionTrackingMode> trackingModes = new HashSet<>();
        trackingModes.add(SessionTrackingMode.COOKIE);
        trackingModes.add(SessionTrackingMode.URL); // Deprecated but still used
        
        try {
            context.setSessionTrackingModes(trackingModes);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Handle complex session tracking issues
            System.err.println("Session tracking mode configuration failed: " + e.getMessage());
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Type casting with javax.servlet specific classes
        if (!(request instanceof HttpServletRequest) || 
            !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Complex session management using javax.servlet.http.HttpSession
        HttpSession session = httpRequest.getSession(true);
        String sessionId = session.getId();
        
        // Track session with custom logic
        SessionData sessionData = sessionRegistry.computeIfAbsent(
            sessionId, SessionData::new
        );
        sessionData.lastAccessTime = System.currentTimeMillis();
        
        // Complex authentication check using deprecated APIs
        Principal userPrincipal = httpRequest.getUserPrincipal();
        if (userPrincipal != null) {
            sessionData.principal = userPrincipal;
        }
        
        // Use javax.servlet specific request attributes
        httpRequest.setAttribute("session.data", sessionData);
        httpRequest.setAttribute("filter.processed", true);
        
        // Complex header manipulation
        String requestedWith = httpRequest.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            httpResponse.setHeader("X-Custom-Ajax", "processed");
        }
        
        // Use deprecated method - getRequestURL behavior
        StringBuffer requestURL = httpRequest.getRequestURL();
        String queryString = httpRequest.getQueryString();
        String fullURL = queryString == null ? requestURL.toString() : 
                        requestURL.append('?').append(queryString).toString();
        
        sessionData.attributes.put("last.request.url", fullURL);
        
        // Complex locale handling using javax.servlet APIs
        Locale locale = httpRequest.getLocale();
        Enumeration<Locale> locales = httpRequest.getLocales();
        List<Locale> localeList = new ArrayList<>();
        while (locales.hasMoreElements()) {
            localeList.add(locales.nextElement());
        }
        sessionData.attributes.put("supported.locales", localeList);
        
        // Wrap request with custom wrapper - tightly coupled to javax.servlet
        RequestWrapper wrappedRequest = new RequestWrapper(httpRequest, sessionData);
        
        try {
            chain.doFilter(wrappedRequest, response);
        } finally {
            // Cleanup logic that depends on javax.servlet session lifecycle
            cleanupExpiredSessions();
        }
    }
    
    @Override
    public void destroy() {
        // Complex cleanup with javax.servlet ServletContext
        if (filterConfig != null) {
            ServletContext context = filterConfig.getServletContext();
            context.removeAttribute("security.filter.initialized");
            context.log("LegacySecurityFilter destroyed");
        }
        sessionRegistry.clear();
    }
    
    /**
     * Custom request wrapper tightly coupled to javax.servlet
     */
    private static class RequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
        private final SessionData sessionData;
        
        public RequestWrapper(HttpServletRequest request, SessionData sessionData) {
            super(request);
            this.sessionData = sessionData;
        }
        
        @Override
        public String getHeader(String name) {
            // Custom header logic
            if ("X-Session-Id".equals(name)) {
                return sessionData.sessionId;
            }
            return super.getHeader(name);
        }
        
        @Override
        public Enumeration<String> getHeaders(String name) {
            // Complex header enumeration
            if ("X-Session-Id".equals(name)) {
                return Collections.enumeration(
                    Collections.singletonList(sessionData.sessionId)
                );
            }
            return super.getHeaders(name);
        }
        
        @Override
        public Object getAttribute(String name) {
            // Complex attribute resolution with fallback
            Object value = sessionData.attributes.get(name);
            return value != null ? value : super.getAttribute(name);
        }
    }
    
    /**
     * Session cleanup with complex expiration logic
     */
    private void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        long timeout = 30 * 60 * 1000; // 30 minutes
        
        sessionRegistry.entrySet().removeIf(entry -> {
            SessionData data = entry.getValue();
            return (now - data.lastAccessTime) > timeout;
        });
    }
    
    /**
     * Get active session count - used by monitoring endpoints
     */
    public int getActiveSessionCount() {
        return sessionRegistry.size();
    }
    
    /**
     * Complex session inspection method
     */
    public Map<String, Object> getSessionInfo(String sessionId) {
        SessionData data = sessionRegistry.get(sessionId);
        if (data == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("sessionId", data.sessionId);
        info.put("principal", data.principal != null ? data.principal.getName() : "anonymous");
        info.put("createdTime", new Date(data.createdTime));
        info.put("lastAccessTime", new Date(data.lastAccessTime));
        info.put("attributes", new HashMap<>(data.attributes));
        
        return info;
    }
}
