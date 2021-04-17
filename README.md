# VirtualLABS
web application project made with love with:
- spring boot 
- Angular
- MariaDb

## AVAILABLE USER
    - admin@polito.it pwd
    - d123456@polito.it pwd
    - s263138@studenti.polito.it pwd
    - s263094@studenti.polito.it pwd
    - s255300@studenti.polito.it pwd

## RUN VIRTUALLABS STANDALONE

You can run the app (frontend,backend and db) just type in root folder:

- `docker-compose up ` 

to shutdown:

- `docker-compose down`

to rebuild and then execute:

- `docker-compose up --build `


the process might be very slower (around 5-7 mins) because the containers build both frontend and backend.

the frontend will be exposed at http://localhost:80/ 
server api at http://localhost:8080/api/ 

## - TODO add users and pwds


this docker-compose yml contains 3 microservices:

- nginx (for serving frontend static file and proxy api request to spring)
- spring
- mariadb

usefull command: 
- `docker-compose up -d --force-recreate`
- `docker-compose down --rmi all`



## DEVELOPMENT

### Run DB
make a folder in db folder called db_data, this will be the shared volume with db container.
go in db folder and run:

- `docker-compose up`

(remember there are a volume attached to db container `db_data` folder )

### Run Backend

after db is running,load project in clion, sync project,build,then run (you can use Maven to clean project).

### Run frontend

if is the first time you load application go in client folder,then run:
- `npm install `

for run :
- `ng serve`