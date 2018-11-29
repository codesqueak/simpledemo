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
package uk.co.nogooddeedgoesunpunished.simpledemo;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.*;
import org.springframework.web.reactive.function.BodyInserters;
import uk.co.nogooddeedgoesunpunished.simpledemo.model.Customer;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class ApplicationIntegrationTest {

    static {
        System.setProperty(SystemConstants.SYSTEM_NAME, "int-test");
        System.setProperty(SystemConstants.SUBSYSTEM_NAME, "Spring5");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void simpleIntegrationTest() throws Exception {
        // empty
        webTestClient.get().uri("/customers").exchange().expectStatus().isOk().expectBody(Customer[].class).isEqualTo(new Customer[0]);
        // add two customers
        EntityExchangeResult<Customer> result =
                webTestClient.post().uri("/customer").body(BodyInserters.fromObject(new Customer(null, "first1", "second1"))).exchange().expectStatus().isCreated().expectBody(Customer.class).returnResult();
        webTestClient.post().uri("/customer").body(BodyInserters.fromObject(new Customer(null, "first2", "second2"))).exchange().expectStatus().isCreated();
        // check customers
        webTestClient.get().uri("/customers").exchange().expectStatus().isOk().expectBody(Customer[].class).value(c -> c.length, Matchers.equalTo(2));
        // delete a customer
        assertNotNull(result.getResponseBody());
        UUID id = result.getResponseBody().getId();
        webTestClient.delete().uri("/customer/{id}", id).exchange().expectStatus().isNoContent();
        // final check
        webTestClient.get().uri("/customers").exchange().expectStatus().isOk().expectBody(Customer[].class).value(c -> c.length, Matchers.equalTo(1));
    }

}
