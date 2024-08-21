# Getting Started

### Reference Documentation

    CRUD 
    Reactive Client API with Spring


GET customers

GET customers/{id}          returns 404 if no customer is not found

GET customers/paginated?page=1&size=5   (optional query parameters)

POST /customers

PUT /customers      returns 404 if the given id customer is not found

DELETE  /customers      // returns 404 if no customer is not found, or 204 if it is deleted

