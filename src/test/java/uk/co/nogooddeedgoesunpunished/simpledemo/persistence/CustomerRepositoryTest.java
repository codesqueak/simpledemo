/*
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package uk.co.nogooddeedgoesunpunished.simpledemo.persistence;

import org.junit.*;
import uk.co.nogooddeedgoesunpunished.simpledemo.model.Customer;

import java.util.*;

import static junit.framework.TestCase.*;

public class CustomerRepositoryTest {

    private CustomerRepository repo;
    private UUID k1, k2, k3;

    @Before
    public void setUp() throws Exception {
        repo = new CustomerRepository();
        k1 = repo.insertCustomer(new Customer(null, "f1", "l1")).block().getId();
        k2 = repo.insertCustomer(new Customer(null, "f2", "l2")).block().getId();
        k3 = repo.insertCustomer(new Customer(null, "f3", "l3")).block().getId();
    }

    // basic example tests

    @Test
    public void findAllCustomers() {
        List<Customer> all = repo.findAllCustomers().collectList().block();
        assertNotNull(all);
        assertEquals(all.size(), 3);
    }

    @Test
    public void deleteCustomerById() {
        Customer customer = repo.deleteCustomerById(k1).block();
        assertNotNull(customer);
        assertEquals(customer.getFirstname(), "f1");
        //
        customer = repo.deleteCustomerById(k2).block();
        assertNotNull(customer);
        assertEquals(customer.getSurname(), "l2");
        //
        List<Customer> all = repo.findAllCustomers().collectList().block();
        assertNotNull(all);
        assertEquals(all.size(), 1);
        assertEquals(all.get(0).getId(), k3);
    }

    @Test
    public void insertCustomer() {
        UUID k4 = repo.insertCustomer(new Customer(null, "f4", "l4")).block().getId();
        List<Customer> all = repo.findAllCustomers().collectList().block();
        assertNotNull(all);
        assertEquals(all.size(), 4);
        //
        Customer customer = repo.deleteCustomerById(k4).block();
        assertNotNull(customer);
        assertEquals(customer.getFirstname(), "f4");
        assertEquals(customer.getSurname(), "l4");
    }
}