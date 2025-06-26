# Events Challenge Backend

Solution for a backend exercise.

## Requirements

- Event management (CRUD).
- Users and admins can sign up for published events.
- Events can be filtered by status, date, and title.
- Users can filter the events they signed up for by past or upcoming events.

## Endpoints

The API has the following endpoints:

| Method | Route                           | Action                          | Access        | Filters                                                                                                                    |
|--------|---------------------------------|---------------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------|
| POST   | /register                       | User signup                    | Public        |                                                                                                                            |
| POST   | /login                          | User login                      | Public        |                                                                                                                            |
| GET    | /api/v1/events                  | Get all the events              | Authenticated | pagination (`page` and `limit`), `date_start` (YYYY-MM-DD), `date_end` (YYYY-MM-DD), `status` (draft o published), `title` |
| GET    | /api/v1/events/:id              | Get a specific event            | Authenticated   |                                                                                                                            |
| POST   | /api/v1/events                  | Create an event                 | Admin         |                                                                                                                            |
| DELETE | /api/v1/events/:id              | Delete an event                 | Admin         |                                                                                                                            |
| PATCH  | /api/v1/events/:id              | Update an event                 | Admin         |                                                                                                                            |
| POST   | /api/v1/events/:id/signup       | Sign up for an event            | Authenticated   |                                                                                                                            |
| GET    | /api/v1/user/events             | Get user events                 | Authenticated   | pagination (`page` and `limit`), `filter` (past or upcoming)                                                               |
| PATCH  | /api/v1/users/:username/promote | Promote a user to administrator | Admin         |                                                                                                                            |

## Running the application

To run the application, you must provide a `.env` file with the following environment variables:

```
POSTGRES_DB=database_name
POSTGRES_USER=database_usename
POSTGRES_PASSWORD=database_password
JWT_SECRET=JWT_sercret_key
JWT_EXPIRATION=8640000 # 1 day
JWT_REFRESH_EXPIRATION=604800000 # 7 days
ADMIN_USERNAME=default_admin_username
ADMIN_PASSWORD=default_admin_password
```

And also an `application.properties` file with the following variables:

```
server.port=8080
spring.application.name=Events
spring.datasource.url=jdbc:postgresql://db:5432/${POSTGRES_DB}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

application.security.jwt.secret-key=${JWT_SECRET}
application.security.jwt.expiration=${JWT_EXPIRATION}
application.security.jwt.refresh-token.expiration=${JWT_REFRESH_EXPIRATION}

default.username=${ADMIN_USERNAME}
default.password=${ADMIN_USERNAME}

springdoc.api-docs.path=/api-docs
```

Then, execute the following command to run the app using Docker:

```
docker compose up --build
```

Finally, you can make requests to `localhost:8080` using an HTTP client or access `http://localhost:8080/swagger-ui/index.html` for the Swagger UI documentation

## Notes and considerations

- No order is assumed for event display.
- Admins can create an event with the status "published", it's not required that the event is created as a draft first.
- It's assumed that multiple admins can exist, so they have the ability to promote a regular user to an administrator using their username.
- The app creates a default admin user with the `ADMIN_USERNAME` and `ADMIN_PASSWORD` specified in the `.env` file.