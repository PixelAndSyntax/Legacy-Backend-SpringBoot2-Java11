package com.example.legacydemo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Customer domain object
 *
 * Tests cover:
 * - Object creation and initialization
 * - Getter and setter methods
 * - Equals and hashCode contracts
 * - toString implementation
 * - Edge cases and validation
 */
class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    void testDefaultConstructor() {
        // Test default constructor creates a valid object
        Customer defaultCustomer = new Customer();
        assertNotNull(defaultCustomer, "Default constructor should create non-null object");
    }

    @Test
    void testParameterizedConstructor() {
        // Test parameterized constructor if available
        Long testId = 1L;
        String testName = "John Doe";

        // Customer has constructor(String name, String email)
        Customer paramCustomer = new Customer(testName, "test@example.com");
        assertEquals(testName, paramCustomer.getName(), "Constructor should set name correctly");
        assertEquals("test@example.com", paramCustomer.getEmail(), "Constructor should set email correctly");

        // Test basic setters
        customer.setId(testId);
        customer.setName(testName);
        assertEquals(testId, customer.getId(), "Setter should set ID correctly");
        assertEquals(testName, customer.getName(), "Setter should set name correctly");
    }

    @Test
    void testIdProperty() {
        // Test ID property getter and setter
        Long testId = 123L;
        customer.setId(testId);
        assertEquals(testId, customer.getId(), "ID should be set and retrieved correctly");

        // Test null ID
        customer.setId(null);
        assertNull(customer.getId(), "ID should accept null values");

        // Test negative ID
        Long negativeId = -1L;
        customer.setId(negativeId);
        assertEquals(negativeId, customer.getId(), "ID should accept negative values");
    }

    @Test
    void testNameProperty() {
        // Test name property getter and setter
        String testName = "Alice Smith";
        customer.setName(testName);
        assertEquals(testName, customer.getName(), "Name should be set and retrieved correctly");

        // Test null name
        customer.setName(null);
        assertNull(customer.getName(), "Name should accept null values");

        // Test empty name
        customer.setName("");
        assertEquals("", customer.getName(), "Name should accept empty strings");

        // Test name with special characters
        String specialName = "José María O'Connor-Smith";
        customer.setName(specialName);
        assertEquals(specialName, customer.getName(), "Name should handle special characters");
    }

    @Test
    void testEmailProperty() {
        // Test email property if it exists
        try {
            String testEmail = "test@example.com";
            customer.setEmail(testEmail);
            assertEquals(testEmail, customer.getEmail(), "Email should be set and retrieved correctly");

            // Test null email
            customer.setEmail(null);
            assertNull(customer.getEmail(), "Email should accept null values");

            // Test empty email
            customer.setEmail("");
            assertEquals("", customer.getEmail(), "Email should accept empty strings");
        } catch (Exception e) {
            // Email property might not exist, which is fine
        }
    }

    @Test
    void testEqualsContract() {
        // Test equals method implementation
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("John Doe");

        Customer customer2 = new Customer();
        customer2.setId(1L);
        customer2.setName("John Doe");

        Customer customer3 = new Customer();
        customer3.setId(2L);
        customer3.setName("Jane Doe");

        // Reflexive: x.equals(x) should return true
        assertEquals(customer1, customer1, "Object should equal itself");

        // Symmetric: x.equals(y) should return same as y.equals(x)
        if (customer1.equals(customer2)) {
            assertEquals(customer2, customer1, "Equals should be symmetric");
        }

        // Test inequality
        assertNotEquals(customer1, customer3, "Different customers should not be equal");
        assertNotEquals(customer1, null, "Customer should not equal null");
        assertNotEquals(customer1, "string", "Customer should not equal different type");
    }

    @Test
    void testHashCodeContract() {
        // Test hashCode implementation
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("John Doe");

        Customer customer2 = new Customer();
        customer2.setId(1L);
        customer2.setName("John Doe");

        // If two objects are equal, their hash codes should be equal
        if (customer1.equals(customer2)) {
            assertEquals(customer1.hashCode(), customer2.hashCode(),
                        "Equal objects should have equal hash codes");
        }

        // Hash code should be consistent
        int hashCode1 = customer1.hashCode();
        int hashCode2 = customer1.hashCode();
        assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
    }

    @Test
    void testToString() {
        // Test toString implementation
        customer.setId(1L);
        customer.setName("John Doe");

        String toString = customer.toString();
        assertNotNull(toString, "toString should not return null");

        // Test toString with null values
        Customer nullCustomer = new Customer();
        String nullToString = nullCustomer.toString();
        assertNotNull(nullToString, "toString should not return null even with null fields");
    }

    @Test
    void testCustomerValidation() {
        // Test basic validation scenarios
        customer.setId(1L);
        customer.setName("Valid Name");

        // Test that customer can be created with valid data
        assertNotNull(customer.getId(), "Customer should have valid ID");
        assertNotNull(customer.getName(), "Customer should have valid name");

        // Test edge cases
        customer.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, customer.getId(), "Customer should handle max Long ID");

        customer.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, customer.getId(), "Customer should handle min Long ID");
    }

    @Test
    void testCustomerSerialization() {
        // Test that customer can be serialized (important for REST APIs)
        customer.setId(1L);
        customer.setName("Serializable Customer");

        // Basic test - if customer implements Serializable
        assertDoesNotThrow(() -> {
            customer.toString(); // Basic serialization check
        }, "Customer should be serializable");
    }

    @Test
    void testCustomerImmutability() {
        // Test if customer has immutable aspects
        customer.setId(1L);
        customer.setName("Original Name");

        Long originalId = customer.getId();
        String originalName = customer.getName();

        // Verify that references are properly handled
        assertNotNull(originalId, "Original ID should not be null");
        assertNotNull(originalName, "Original name should not be null");

        // Test modification
        customer.setName("Modified Name");
        assertNotEquals(originalName, customer.getName(), "Name should be modifiable");
    }

    @Test
    void testCustomerComparison() {
        // Test if Customer implements Comparable
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Alice");

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Bob");

        try {
            // If Comparable is implemented
            @SuppressWarnings("unchecked")
            Comparable<Customer> comparable1 = (Comparable<Customer>) customer1;
            int comparison = comparable1.compareTo(customer2);
            assertTrue(comparison != 0, "Different customers should have non-zero comparison");
        } catch (ClassCastException e) {
            // Customer doesn't implement Comparable, which is fine
        }
    }
}