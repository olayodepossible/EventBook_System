# Event Booking

---

## Introduction

It is important to know that booking systems has become an essential aspect of daily life. From booking tickets for a concert or reserving a spot at a conference, these systems are used widely by individuals and businesses.

---

## System Functionality

The system will allow users to create, find and reserve tickets for events, view and manage their reservations and to be notified before the event kickoff.

A **user** has:
- name (limited to 100 characters);
- email (valid email format);
- password (minimum 8 characters).

An **event** has:
- name (limited to 100 characters);
- date (in a valid date format);
- available attendees count (positive integer limited to 1000);
- event description (limited to 500 characters).
- category (Concert, Conference, Game)

The System Develop a set of REST service APIs based on the swagger file provided - [swagger file](event-booking-swagger.yml), that allows users to:

- Create an account;
- User authentication to log into the system;
- Create events;
- Search and reserve tickets for events;
- Send notification to users before event starts.

---

## Prerequisites
- Java 11
- Maven

## Build
```bash
mvn clean install
```

## RUN
```bash
mvn spring-boot:run
```
## TEST
````
mvn test

jaccoco for code coverage:  mvn clean verify
````
## Note:
````
- Periodic task to send notifications every 5min --
- Password for all user is "password"
    
````


:scroll: **END**
