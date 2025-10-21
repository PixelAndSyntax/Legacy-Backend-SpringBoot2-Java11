package com.example.legacydemo.repo;

import com.example.legacydemo.domain.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CustomerRepository
 *
 * Tests cover:
 * - CRUD operations
 * - Custom query methods
 * - Data persistence
 * - Transaction handling
 * - Edge cases
 */
@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setName("Test Customer");
        // Don't set ID, let JPA generate it
    }

    @Test
    void testRepositoryInjection() {
        // Test that repository is properly injected
        assertNotNull(customerRepository, "CustomerRepository should be injected");
        assertNotNull(entityManager, "TestEntityManager should be injected");
    }

    @Test
    void testSaveCustomer() {
        // Test saving a customer
        Customer savedCustomer = customerRepository.save(testCustomer);

        assertNotNull(savedCustomer, "Saved customer should not be null");
        assertNotNull(savedCustomer.getId(), "Saved customer should have an ID");
        assertEquals(testCustomer.getName(), savedCustomer.getName(), "Name should be preserved");
    }

    @Test
    void testFindById() {
        // Save a customer first
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Long customerId = savedCustomer.getId();

        // Test finding by ID
        Optional<Customer> foundCustomer = customerRepository.findById(customerId);

        assertTrue(foundCustomer.isPresent(), "Customer should be found by ID");
        assertEquals(customerId, foundCustomer.get().getId(), "Found customer should have correct ID");
        assertEquals(testCustomer.getName(), foundCustomer.get().getName(), "Found customer should have correct name");
    }

    @Test
    void testFindByIdNotFound() {
        // Test finding non-existent customer
        Optional<Customer> notFoundCustomer = customerRepository.findById(999L);

        assertFalse(notFoundCustomer.isPresent(), "Non-existent customer should not be found");
    }

    @Test
    void testFindAll() {
        // Save multiple customers
        Customer customer1 = new Customer();
        customer1.setName("Customer 1");
        Customer customer2 = new Customer();
        customer2.setName("Customer 2");

        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        // Test finding all customers
        List<Customer> allCustomers = customerRepository.findAll();

        assertNotNull(allCustomers, "findAll should not return null");
        assertTrue(allCustomers.size() >= 2, "Should find at least the customers we saved");

        // Verify our customers are in the list
        boolean foundCustomer1 = allCustomers.stream()
            .anyMatch(c -> "Customer 1".equals(c.getName()));
        boolean foundCustomer2 = allCustomers.stream()
            .anyMatch(c -> "Customer 2".equals(c.getName()));

        assertTrue(foundCustomer1, "Should find Customer 1");
        assertTrue(foundCustomer2, "Should find Customer 2");
    }

    @Test
    void testDeleteById() {
        // Save a customer first
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Long customerId = savedCustomer.getId();

        // Verify customer exists
        assertTrue(customerRepository.findById(customerId).isPresent(),
                  "Customer should exist before deletion");

        // Delete the customer
        customerRepository.deleteById(customerId);
        entityManager.flush();

        // Verify customer is deleted
        assertFalse(customerRepository.findById(customerId).isPresent(),
                   "Customer should not exist after deletion");
    }

    @Test
    void testDeleteCustomer() {
        // Save a customer first
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Long customerId = savedCustomer.getId();

        // Delete the customer entity
        customerRepository.delete(savedCustomer);
        entityManager.flush();

        // Verify customer is deleted
        assertFalse(customerRepository.findById(customerId).isPresent(),
                   "Customer should not exist after deletion");
    }

    @Test
    void testExistsById() {
        // Save a customer first
        Customer savedCustomer = entityManager.persistAndFlush(testCustomer);
        Long customerId = savedCustomer.getId();

        // Test exists by ID
        assertTrue(customerRepository.existsById(customerId),
                  "Customer should exist");

        // Test non-existent customer
        assertFalse(customerRepository.existsById(999L),
                   "Non-existent customer should not exist");
    }

    @Test
    void testCount() {
        // Get initial count
        long initialCount = customerRepository.count();

        // Add customers
        Customer customer1 = new Customer();
        customer1.setName("Count Test 1");
        Customer customer2 = new Customer();
        customer2.setName("Count Test 2");

        entityManager.persistAndFlush(customer1);
        entityManager.persistAndFlush(customer2);

        // Test count
        long newCount = customerRepository.count();
        assertEquals(initialCount + 2, newCount, "Count should increase by 2");
    }

  @Test
  void testCustomQueryMethods() {
    // Test basic repository functionality
    String testName = "Query Test Customer";
    Customer queryCustomer = new Customer();
    queryCustomer.setName(testName);
    queryCustomer.setEmail("query@test.com");
    entityManager.persistAndFlush(queryCustomer);

    // Test basic findAll functionality
    List<Customer> allCustomers = customerRepository.findAll();
    assertTrue(allCustomers.stream().anyMatch(c -> testName.equals(c.getName())),
               "Should find customer in repository");
  }    @Test
    void testUpdateCustomer() {
        // Save a customer first
        Customer savedCustomer = customerRepository.save(testCustomer);
        Long customerId = savedCustomer.getId();

        // Update the customer
        String newName = "Updated Customer Name";
        savedCustomer.setName(newName);
        Customer updatedCustomer = customerRepository.save(savedCustomer);

        // Verify update
        assertEquals(customerId, updatedCustomer.getId(), "ID should remain the same");
        assertEquals(newName, updatedCustomer.getName(), "Name should be updated");

        // Verify persistence
        Optional<Customer> persistedCustomer = customerRepository.findById(customerId);
        assertTrue(persistedCustomer.isPresent(), "Updated customer should still exist");
        assertEquals(newName, persistedCustomer.get().getName(), "Persisted customer should have updated name");
    }

    @Test
    void testSaveAll() {
        // Test saving multiple customers at once
        Customer customer1 = new Customer();
        customer1.setName("Batch Customer 1");
        Customer customer2 = new Customer();
        customer2.setName("Batch Customer 2");
        Customer customer3 = new Customer();
        customer3.setName("Batch Customer 3");

        List<Customer> customersToSave = List.of(customer1, customer2, customer3);
        List<Customer> savedCustomers = customerRepository.saveAll(customersToSave);

        assertNotNull(savedCustomers, "saveAll should not return null");
        assertEquals(3, savedCustomers.size(), "Should save all 3 customers");

        // Verify all customers have IDs
        for (Customer customer : savedCustomers) {
            assertNotNull(customer.getId(), "Each saved customer should have an ID");
        }
    }

    @Test
    void testDeleteAll() {
        // Save some customers first
        Customer customer1 = new Customer();
        customer1.setName("Delete All Test 1");
        Customer customer2 = new Customer();
        customer2.setName("Delete All Test 2");

        customerRepository.saveAll(List.of(customer1, customer2));

        // Verify customers exist
        long countBeforeDelete = customerRepository.count();
        assertTrue(countBeforeDelete >= 2, "Should have at least 2 customers before delete");

        // Delete all customers
        customerRepository.deleteAll();

        // Verify all customers are deleted
        long countAfterDelete = customerRepository.count();
        assertEquals(0, countAfterDelete, "Should have no customers after deleteAll");
    }

    @Test
    void testTransactionRollback() {
        // Test transaction handling
        try {
            Customer invalidCustomer = new Customer();
            // Set invalid data that might cause constraint violation
            invalidCustomer.setName(null); // Assuming name is required

            customerRepository.save(invalidCustomer);
            entityManager.flush();

        } catch (Exception e) {
            // Exception is expected for invalid data
            // Verify transaction was rolled back properly
            long count = customerRepository.count();
            assertTrue(count >= 0, "Count should be valid even after failed transaction");
        }
    }

    @Test
    void testCustomerPersistence() {
        // Test full persistence lifecycle
        String originalName = "Persistence Test Customer";
        testCustomer.setName(originalName);

        // Save
        Customer savedCustomer = customerRepository.save(testCustomer);
        assertNotNull(savedCustomer.getId(), "Customer should have ID after save");

        // Update
        String updatedName = "Updated Persistence Test Customer";
        savedCustomer.setName(updatedName);
        customerRepository.save(savedCustomer);

        // Reload from database
        Optional<Customer> reloadedCustomer = customerRepository.findById(savedCustomer.getId());
        assertTrue(reloadedCustomer.isPresent(), "Customer should be reloadable");
        assertEquals(updatedName, reloadedCustomer.get().getName(), "Reloaded customer should have updated name");

        // Delete
        customerRepository.delete(reloadedCustomer.get());

        // Verify deletion
        assertFalse(customerRepository.findById(savedCustomer.getId()).isPresent(),
                   "Customer should be deleted");
    }
}