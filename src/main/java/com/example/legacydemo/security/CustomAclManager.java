package com.example.legacydemo.security;

import org.springframework.stereotype.Component;

import java.security.Principal;
import java.security.acl.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Complex custom ACL implementation that requires significant redesign for Jakarta.
 * This class deeply integrates with java.security.acl which is removed in Java 17.
 * 
 * MIGRATION CHALLENGE: This requires complete architectural redesign as java.security.acl
 * has no direct replacement. Options include:
 * - Implement custom domain-level RBAC
 * - Migrate to Spring Security ACL module
 * - Use external authorization framework (e.g., Apache Shiro, Keycloak)
 */
@Component
public class CustomAclManager {
    
    private final Map<String, CustomAcl> aclRegistry = new ConcurrentHashMap<>();
    
    /**
     * Custom ACL implementation with complex permission logic
     */
    private static class CustomAcl implements Acl {
        private final Principal owner;
        private final String name;
        private final List<AclEntry> entries = new ArrayList<>();
        
        public CustomAcl(Principal owner, String name) {
            this.owner = owner;
            this.name = name;
        }
        
        @Override
        public void setName(Principal caller, String name) throws NotOwnerException {
            if (!checkOwner(caller)) {
                throw new NotOwnerException();
            }
            // Complex business logic here
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public boolean addOwner(Principal caller, Principal owner) throws NotOwnerException {
            if (!checkOwner(caller)) {
                throw new NotOwnerException();
            }
            // Multi-owner support - complex logic
            return true;
        }
        
        @Override
        public boolean deleteOwner(Principal caller, Principal owner) 
                throws NotOwnerException, LastOwnerException {
            if (!checkOwner(caller)) {
                throw new NotOwnerException();
            }
            // Cannot delete last owner
            throw new LastOwnerException();
        }
        
        @Override
        public boolean isOwner(Principal owner) {
            return this.owner.equals(owner);
        }
        
        @Override
        public boolean addEntry(Principal caller, AclEntry entry) throws NotOwnerException {
            if (!checkOwner(caller)) {
                throw new NotOwnerException();
            }
            return entries.add(entry);
        }
        
        @Override
        public boolean removeEntry(Principal caller, AclEntry entry) throws NotOwnerException {
            if (!checkOwner(caller)) {
                throw new NotOwnerException();
            }
            return entries.remove(entry);
        }
        
        @Override
        public Enumeration<Permission> getPermissions(Principal user) {
            // Complex permission resolution logic
            List<Permission> permissions = new ArrayList<>();
            for (AclEntry entry : entries) {
                if (entry.getPrincipal().equals(user)) {
                    Enumeration<Permission> entryPerms = entry.permissions();
                    while (entryPerms.hasMoreElements()) {
                        permissions.add(entryPerms.nextElement());
                    }
                }
            }
            return Collections.enumeration(permissions);
        }
        
        @Override
        public Enumeration<AclEntry> entries() {
            return Collections.enumeration(entries);
        }
        
        @Override
        public boolean checkPermission(Principal principal, Permission permission) {
            // Complex cascading permission check
            for (AclEntry entry : entries) {
                if (entry.getPrincipal().equals(principal)) {
                    if (entry.checkPermission(permission)) {
                        return !entry.isNegative(); // Handle negative permissions
                    }
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "CustomAcl[" + name + ", owner=" + owner + ", entries=" + entries.size() + "]";
        }
        
        private boolean checkOwner(Principal caller) {
            return owner.equals(caller);
        }
    }
    
    /**
     * Custom permission type with business logic
     */
    public static class ResourcePermission implements Permission {
        private final String resource;
        private final String action;
        
        public ResourcePermission(String resource, String action) {
            this.resource = resource;
            this.action = action;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ResourcePermission) {
                ResourcePermission other = (ResourcePermission) obj;
                return resource.equals(other.resource) && action.equals(other.action);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(resource, action);
        }
        
        @Override
        public String toString() {
            return "ResourcePermission[" + resource + ":" + action + "]";
        }
    }
    
    /**
     * Creates ACL with complex initialization - tightly coupled to java.security.acl
     */
    public Acl createAcl(String resourceName, Principal owner) {
        CustomAcl acl = new CustomAcl(owner, resourceName);
        aclRegistry.put(resourceName, acl);
        return acl;
    }
    
    /**
     * Complex permission check with cascading logic
     */
    public boolean checkAccess(String resourceName, Principal principal, String action) {
        CustomAcl acl = aclRegistry.get(resourceName);
        if (acl == null) {
            return false; // Deny by default
        }
        
        ResourcePermission permission = new ResourcePermission(resourceName, action);
        return acl.checkPermission(principal, permission);
    }
    
    /**
     * Grant permission with complex ACL entry creation
     */
    public void grantPermission(String resourceName, Principal owner, 
                                Principal user, String action) {
        try {
            CustomAcl acl = aclRegistry.get(resourceName);
            if (acl == null) {
                acl = (CustomAcl) createAcl(resourceName, owner);
            }
            
            // Create AclEntry - this API is removed in Java 17
            AclEntry entry = new AclEntry() {
                private Principal principal = user;
                private boolean negative = false;
                private Set<Permission> permissions = new HashSet<>();
                
                @Override
                public boolean setPrincipal(Principal user) {
                    if (this.principal != null) {
                        return false;
                    }
                    this.principal = user;
                    return true;
                }
                
                @Override
                public Principal getPrincipal() {
                    return principal;
                }
                
                @Override
                public void setNegativePermissions() {
                    this.negative = true;
                }
                
                @Override
                public boolean isNegative() {
                    return negative;
                }
                
                @Override
                public boolean addPermission(Permission permission) {
                    return permissions.add(permission);
                }
                
                @Override
                public boolean removePermission(Permission permission) {
                    return permissions.remove(permission);
                }
                
                @Override
                public boolean checkPermission(Permission permission) {
                    return permissions.contains(permission);
                }
                
                @Override
                public Enumeration<Permission> permissions() {
                    return Collections.enumeration(permissions);
                }
                
                @Override
                public String toString() {
                    return "AclEntry[" + principal + ", negative=" + negative + 
                           ", permissions=" + permissions + "]";
                }
                
                @Override
                public Object clone() {
                    return this; // Simplified for demo
                }
            };
            
            entry.addPermission(new ResourcePermission(resourceName, action));
            acl.addEntry(owner, entry);
            
        } catch (NotOwnerException e) {
            throw new SecurityException("Not authorized to grant permission", e);
        }
    }
    
    /**
     * Returns ACL summary - used throughout the application
     */
    public Map<String, String> getAclSummary() {
        Map<String, String> summary = new HashMap<>();
        aclRegistry.forEach((resource, acl) -> {
            Enumeration<AclEntry> entries = acl.entries();
            int entryCount = 0;
            while (entries.hasMoreElements()) {
                entries.nextElement();
                entryCount++;
            }
            summary.put(resource, acl.getName() + " (" + entryCount + " entries)");
        });
        return summary;
    }
}
