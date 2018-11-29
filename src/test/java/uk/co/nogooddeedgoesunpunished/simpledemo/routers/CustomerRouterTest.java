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
package uk.co.nogooddeedgoesunpunished.simpledemo.routers;

import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import uk.co.nogooddeedgoesunpunished.simpledemo.SystemConstants;
import uk.co.nogooddeedgoesunpunished.simpledemo.model.Customer;
import uk.co.nogooddeedgoesunpunished.simpledemo.persistence.CustomerRepository;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest(CustomerRouter.class)
public class CustomerRouterTest {

    static {
        System.setProperty(SystemConstants.SYSTEM_NAME, "test");
        System.setProperty(SystemConstants.SUBSYSTEM_NAME, "Spring5");
    }

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private CustomerRepository customerRepository;

    @Before
    public void setUp() throws Exception {
        customerRepository.reset();
    }

    // GET - List all Customers
    @Test
    public void listAllCustomersTest() throws Exception {
        Customer customer = customerRepository.insertCustomer(new Customer(null, "first1", "last1")).block();
        webClient.get().uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(Customer[].class)
                .value(c -> c.length,Matchers.equalTo(1))
                .value(c -> c[0].getId(), Matchers.equalTo(customer.getId()));
        //
        customerRepository.insertCustomer(new Customer(null, "first2", "last2")).block();
        webClient.get().uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer[].class)
                .value(c -> c.length,Matchers.equalTo(2));
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 2);
    }

    // POST - Add a Customer
    @Test
    public void addACustomerTest() throws Exception {
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 0);
        //
        Customer customer = new Customer(null, "first", "last");
        webClient.post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(customer))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location","(/customer/)(\\S){36}")
                .expectBody(Customer.class)
                .value(Customer::getFirstname, Matchers.equalTo("first"))
                .value(Customer::getSurname, Matchers.equalTo("last"))
                .value(Customer::getId, Matchers.notNullValue());
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 1);
    }

    // POST - Add a Customer
    @Test
    public void addAnInvalidCustomerTest() throws Exception {
        Customer customer = new Customer(null, "fi", "last");
        webClient.post().uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(customer))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // DELETE - Remove a Customer, given their ID
    @Test
    public void removeACustomer() throws Exception {
        Customer customer = customerRepository.insertCustomer(new Customer(null, "first1", "last1")).block();
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 1);
        //
        webClient.delete().uri("/customer/{id}",customer.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 0);
        //
        webClient.delete().uri("/customer/{id}",customer.getId())
                .exchange()
                .expectStatus().isNotFound();
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 0);
    }

    // DELETE - Remove a Customer - Bad UUID
    @Test
    public void removeACustomerBadUUID() throws Exception {
        Customer customer = customerRepository.insertCustomer(new Customer(null, "first1", "last1")).block();
        assertEquals(customerRepository.findAllCustomers().collectList().block().size(), 1);
        //
        webClient.delete().uri("/customer/invaliduuid")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    // Normally use a mock here but use the real class to make things simple
    @TestConfiguration
    static class Config {
        @Bean
        public CustomerRepository getRepository() {
            return new CustomerRepository();
        }
    }
}
