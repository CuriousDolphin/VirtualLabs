# VirtualLABS
web application project made with love with:
- spring boot 
- Angular
- MariaDb

## Run DB
go in server folder and run:

- `docker-compose up`

(remember there are a volume attached to db container `db_data` folder )

## Run Backend

after db is running,load project in clion, sync project,build,then run (you can use Maven to clean project).

NB: for develpment reason we build and run spring boot from Clion, in the next iteration backend and db (and maybe FE for comfort reason) will be in the same `docker-compose.yml`.

## Run frontend

if is the first time you load application go in client folder,then run:
- `npm install `

for run :
- `ng serve`