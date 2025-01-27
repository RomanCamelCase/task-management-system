
# Test Task: Task Management API
## [Postman API documentation](https://documenter.getpostman.com/view/28120336/2sAYQggnti)
Overview of available endpoints.
## Data model
- The databasechangeloglock and databasechangelog tables are service tables and do not directly affect the data model.
- The users table is independent. It stores user id, email and user password in a hashed form.
- A user can have multiple roles, making the app easy to scale and more flexible in customizing access.
- The table with refresh tokens is used to control user access to the application. If necessary, it is possible to delete all refresh tokens of the user.
- The task table stores the id of the user who created the task and data about the task.
![Снимок экрана 2025-01-27 052618](https://github.com/user-attachments/assets/ef4076f5-9f68-44e6-8431-e45e31720586)
## Requirements
#### Docker
You must install [docker](https://docs.docker.com/get-started/get-docker/) and [docker compose](https://docs.docker.com/compose/install/) to quickly launch the application.

#### Setting environment variables
Navigate to the **src/main/docker** directory. Copy the .env.origins file, remove the .origins ending, and enter the following parameters into the file:
- **DB_USERNAME** - Database user name
- **DB_PASSWORD** - Database user password
- **ACCESS_TOKEN_SECRET** - JWT secret key

#### To start the project, enter the following command in the terminal (the project will remain running in the background)
```cmd
docker-compose up -d 
```
