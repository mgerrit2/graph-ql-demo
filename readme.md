# Overview

GraphQL's biggest advantage is **client-driven data fetching**—instead of the server dictating fixed data shapes across many endpoints, clients ask for the exact fields and nested relationships they need in a single request.

---

### Key Advantages of GraphQL

* **Eliminates Over-fetching and Under-fetching:** Clients request only the fields they actually render, preventing bloated payloads (over-fetching) and avoiding a cascade of round-trips to assemble related data (under-fetching).
* **Single Endpoint & Aggregated Data:** A single `/graphql` endpoint can resolve and combine data from multiple microservices, REST APIs, or databases behind the scenes, simplifying client networking.
* **Strongly Typed Schema & Built-in Documentation:** Every type, field, and argument is defined in a schema. Tools like GraphiQL or Apollo Sandbox use introspection to auto-generate interactive docs and autocomplete.
* **Frontend & Backend Decoupling (Rapid Iteration):** UI teams can add or change requested fields in queries without waiting for backend engineers to create or modify REST endpoints.
* **Easier API Evolution Without Versioning:** Fields can be added without breaking existing queries, and outdated fields can be marked `@deprecated` with migration guidance instead of forcing `/v1`, `/v2`, and `/v3` URLs.
* **Real-Time Updates via Subscriptions:** Built-in support for subscriptions lets clients push event-driven updates (such as chat messages, live scores, or status changes) over persistent connections like WebSockets.

---

### GraphQL vs. REST at a Glance

| Aspect | GraphQL | REST |
| --- | --- | --- |
| **Data Fetching** | Client specifies exact fields and nested graphs | Server defines fixed response shapes per resource |
| **Endpoints** | Typically a single endpoint (e.g., `/graphql`) | Multiple resource-oriented endpoints (e.g., `/users`, `/posts`) |
| **Round-Trips** | Often 1 query for nested/related data | Frequently multiple requests or custom BFF/query params |
| **Type System & Docs** | Mandatory schema + introspection | OpenAPI/Swagger is optional and separate |
| **Caching** | Harder with HTTP caches (often `POST` requests) | Straightforward via standard HTTP caching (`GET`, `ETag`, `Cache-Control`) |

---

### Practical Nuance (Trade-offs to Keep in Mind)

GraphQL shines in **complex UIs, mobile apps with tight bandwidth, and multi-source aggregation**, 
but it adds complexity around **query cost/depth limiting** (to prevent expensive nested queries), 
**N+1 query resolution** (typically solved with DataLoader batching), and **HTTP caching** compared to straightforward CRUD REST APIs.

## Query graphql
### Sample query person
```graphql
query MyQuery {
    persons {
        firstname
        lastname
    }
    personById(id: "1") {
        firstname
    }
}
```
#### Hoppscotch
##### Query
```graphql
query PersonQuery {
    persons {
        firstname
        lastname
    }
}
```

##### Create
###### Query
```graphql
mutation CreatePerson($input: PersonInput!) {
    createPerson(input: $input) {
        firstname
        lastname
        active
        email
    }
}
```
###### Variables
```json
{
    "input": {
    "firstname": "Marcus",
    "lastname": "Tullius",
    "active": true,
    "email": "marcus.tullius@example.com"
    }
}
```
##### update
###### Query
```graphql
mutation UpdatePerson($id: ID!, $input: PersonInput!) {
    updatePerson(id: $id, input: $input) {
        firstname
        lastname
        active
        email
    }
}
```
###### Variables
```json
{
  "id": "1",
  "input": {
    "firstname": "John",
    "lastname": "Doe Updated",
    "active": true,
    "email": "john.doe@example.com"
  }
}
```

##### delete
###### Query
```graphql
mutation DeletePerson($id: ID!) {
    deletePerson(id: $id)
}
```
###### Variables
```json
{
  "id": "4"
}
```

## Graphql
[Graphql ui](http://localhost:8080/graphiql)

## Swagger
[swagger](http://localhost:8080/swagger-ui/index.html)