# WorkWave-backend

A full stack Spring boot app designed to help you manage your employees with ease

## The Brief

In this fictional brief, I have been task to build a full stake employee management tool with the following requirements:
We need a web application to create, list, modify and delete employees. The application should consist of a spring RESTful API and a React Typescript frontend. The schema for the employee is left to the criteria of the candidate.

React hints:

- React Redux is recommended.
- Typescript is recommended.
- React hooks are recommended.
- Add some basic validations on the form like required and max length validations.
- The site should be responsive.

RESTful API hints:

- The list can be a local database, CSV, TXT file or even in memory
- Implementing an API logging strategy.
- Implementing error handling strategy.

At least 3 endpoints are required:

- To create an employee
- To get a list of existing employees
- To delete an employee

<!-- ## Demo

This API works hand in hand with the Typescript React app (available [here](https://github.com/staceyjf/Postcheck-front)) which is being demo'ed below.

<div align="center">
  <img src="./planning /postcheckAPI.gif" alt="Homepage">
</div>

## Deployment

1. Flask Gunicorn server: Deployed via Azure Web Apps using a Docker container registered in Azure Container Registry (ACR).
2. MySQL Cloud DB: Aiven -->

## Build Steps

```bash
1. Clone the repo.
2. Cd into `WorkWave-backend` folder
3. Start the Spring Boot backend via `WorkWaveApplication.java`
4. Test suit can be run via `app/src/test` folder
```

## Planning considerations

### ERD

Understanding the relationship of the data was an important starting point, and it was determined that a normalized data structure would be the most suitable approach to minimize redundancy and dependency. I took the following steps:

1. Reviewed the fields required for authentication and how they could be overlaid with employee information to create a streamlined user based `Employee` entity
2. Separated out the work-specific details of the role into an `Contract` Entity to hold items such as contract type (contract vs. permanent) and employment type (part-time vs. full-time).

<div align="center">
  <img src="./planning/workwave_erd.png" style="max-width: 800px;" alt="ERD for workwave API">
</div>

### Business assumptions
1. Full-time permanent employees can only have one contract at a time (unless the end date is set before the start date of a new contract)
2. Full-time contracting employees can only start a new contract once the existing contract has expired.
3. The minimum length of a contract needs to be one calendar day

<!-- ### Design inspiration

I took inspiration from the existing Aus-Post service to help shape my design which can be seen below:

<div align="center">
  <img src="./planning /aus-post-inspiration.png" style="max-width: 800px;" alt="Image of Aus-post postcode checker">
</div> -->

### Design choices

1. **Adopting the Controller-Service-Repository Pattern:** This layered architecture approach ensured clear separation of concerns, leadings to better organized and more maintainable code. 

2. **Authentication:** Following a discussion with my \_nology coach, I wanted to extend my learning but implementing a cookie-based JWT instead via the Response Authorization header like I have previously done in my last two projects. 

<!-- which manifested into a flow of data via the following layers:

<div align="center">
  <img src="./planning /flowData.jpg" style="max-width: 800px;" alt="Flow of data">
</div> -->

## Key Features:

### Back-end

#### The '70':

1. **CRUD API Endpoints**: Developed comprehensive RESTful CRUD (Create, Read, Update, Delete) endpoints for managing Departments, Employees and Contracts. 

2. **API documentation:** Integrated Swagger for clear, interactive API documentation, making it easier to understand and consume the API.

3. **Logging:** Addition of Log4J logger to implement a error and general log file for clear debugging.

4. **CI/CD Pipeline:** Implement a development workflow with Github Actions to ensure code passes the test suite before being added to the main branch.


#### The '20':

1. **Authentication:** Explore utlitisng Jakata Java Cookie to attached and retrieve the JWT 

#### The '10':

1. **Basic Integration Testing:** Full testing with Junit5 and 

## Key Learning Highlights

1. **External libraries:** While Flask-Smorest did a lot of the heavy lifting with serialising / deserialisng and providing documentation via swagger, it required a class-based approach for my controllers, utilizing Flask's MethodView. Although my preference was for a functional approach to maintain consistency, the benefits offered by Flask-Smorest outweighed my preference for consistency.

## To-Dos

1. **Testing:** Implement unit testing with pyTest.
2. **Logging strategy:** Enhance the logging strategy to include file-based logging, improving the traceability and debugging of server-side errors.
3. **Response loading strategy:** Implement pagination for postcodes and suburbs, and explore alternative strategies (e.g., lazy loading) for optimizing data delivery in reporting features.
4. **Auth logic:** Refine the `token_required` decorator to efficiently return `signed_in_user` details, ensuring seamless authentication flows.
5. **Bi-directional implementation:** Enhance the create and update functionalities for suburbs to support bi-directional association with postcodes, facilitating richer data relationships.
6. **JWT Implementation:** Transition to a cookie-based JWT exchange mechanism for improved security and user authentication management.
7. **CI/CD Pipeline:** Implement a development workflow with Github Actions

## Changelog


## Documentation

Explore the spring API documentation at: `http://localhost:8080/swagger-ui/index.html`

<div align="center">
  <img src="./planning /Swagger_doc_new.png" style="max-width: 600px;" alt="Swagger documentation of PostCheck API">
</div> -->

## Technologies Used

<div align="center">

![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-05122A?style=flat&logo=springboot)
![React Testing Library](https://img.shields.io/badge/-React%20Testing%20Library-05122A?style=flat&logo=testinglibrary)
![Log4j2](https://img.shields.io/badge/-Log4j2-05122A?style=flat&logo=apache)
![OpenAPI](https://img.shields.io/badge/-OpenAPI-05122A?style=flat&logo=openapiinitiative)
![Docker](https://img.shields.io/badge/-Docker-05122A?style=flat&logo=docker)
![Git](https://img.shields.io/badge/-Git-05122A?style=flat&logo=git)
![GitHub](https://img.shields.io/badge/-GitHub-05122A?style=flat&logo=github)

</div>
