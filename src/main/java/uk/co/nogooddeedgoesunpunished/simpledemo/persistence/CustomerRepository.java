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

import org.springframework.stereotype.Repository;
import reactor.core.publisher.*;
import uk.co.nogooddeedgoesunpunished.simpledemo.model.Customer;

import java.util.*;

@Repository
public class CustomerRepository {

    private Map<UUID, Customer> customerData = new HashMap<>();

    public Flux<Customer> findAllCustomers() {
        return Flux.fromIterable(customerData.values());
    }

    public Mono<Customer> deleteCustomerById(UUID id) {
        return Mono.justOrEmpty(customerData.remove(id));
    }

    public Mono<Customer> insertCustomer(Customer customer) {
        Customer newCustomer = new Customer(UUID.randomUUID(), customer.getFirstname(), customer.getSurname());
        customerData.put(newCustomer.getId(), newCustomer);
        return Mono.just(newCustomer);
    }

    // for testing only - you wouldn't do do this in a real reporsitory !
    public void reset()
    {
        customerData.clear();;
    }

}
