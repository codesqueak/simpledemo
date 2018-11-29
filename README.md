# Simple Demonstration of Spring 5 REST using WebFlux Reactive Streams

## API

| Operation  | Endpoint      |  Notes |
|----------|-------------|------|
| Add a Customer | /customer | Id returned as a UUID |
| Remove a Customer, given their ID | /customer/{id}  | id must be a UUID |
| List all Customers |   |  |

## Customer Data

A Customer has the following attributes:

* Id
* Firstname
* Surname

represented in JSON as:

```json
{
    "Id": "17b60f5f-a0ba-4435-98c9-52f1d179a40d",
    "Firstname": "fred",
    "Surname": "bloggs"
}
```

## Run the Demo

Compile and run the demo using: `mvnw clean install`

Test coverage report is available at: `\target\site\jacoco\index.html`

## Validation

Basic `JSR-303/JSR-349 Bean Validation` has been implemnted on the Add a Customer endpoint.  Thsi ensure that the name fields are not null
and are of a reasonable length.  Validation failure generates a 422 (Unprocessable Entity) response





