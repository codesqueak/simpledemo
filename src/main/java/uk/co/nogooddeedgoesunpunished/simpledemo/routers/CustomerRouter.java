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

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import uk.co.nogooddeedgoesunpunished.simpledemo.model.Customer;
import uk.co.nogooddeedgoesunpunished.simpledemo.persistence.CustomerRepository;

import javax.validation.Validator;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Controller
public class CustomerRouter {

    private CustomerRepository customerRepository;
    private Validator validator;

    public CustomerRouter(CustomerRepository customerRepository, Validator validator) {
        this.validator = validator;
        this.customerRepository = customerRepository;
    }

    @Bean
    public RouterFunction<ServerResponse> appRoutes() {
        // List all Customers
        return route(GET("/customers"), req -> ok().contentType(APPLICATION_JSON).body(customerRepository.findAllCustomers(), Customer.class))
                // Remove a Customer, given their ID
                .and(route(DELETE("/customer/{id}"), req -> {
                    try {
                        UUID id = UUID.fromString(req.pathVariable("id"));
                        return customerRepository.deleteCustomerById(id)
                                .flatMap(uuid -> noContent().build())
                                .switchIfEmpty(notFound().build());
                    } catch (Exception e) {
                        return ServerResponse.badRequest().build();
                    }
                }))
                // Add a Customer
                .and(route(POST("/customer").and(accept(APPLICATION_JSON)),
                           req -> req.body(toMono(Customer.class))
                                   .flatMap(customerRepository::insertCustomer)
                                   .flatMap(c -> validator.validate(c).isEmpty()
                                           ? created(UriComponentsBuilder.fromPath("/customer/" + c.getId()).build().toUri()).contentType(APPLICATION_JSON).body(Mono.just(c), Customer.class)
                                           : ServerResponse.unprocessableEntity().build()
                                   )
                ));
    }
}

