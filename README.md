# VirtualLABS
web application project made with love with:
- spring boot 
- Angular
- MariaDb

## RUN VIRTUALLABS STANDALONE

You can run the app (frontend,backend and db) just type in root folder:
- `docker-compose build`

- `docker-compose up`

the frontend will be exposed at http://localhost:80



this docker-compose yml contains 3 microservices:

- nginx 
- spring
- mariadb

usefull command: 
- `docker-compose up -d --force-recreate`
- `docker-compose down --rmi all`



## DEVELOPMENT

### Run DB
make a folder in db folder called db_data, this will be the shared volume with db container.
go in server folder and run:

- `docker-compose up`

(remember there are a volume attached to db container `db_data` folder )

### Run Backend

after db is running,load project in clion, sync project,build,then run (you can use Maven to clean project).

### Run frontend

if is the first time you load application go in client folder,then run:
- `npm install `

for run :
- `ng serve`