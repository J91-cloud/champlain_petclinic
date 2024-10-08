# champlain_petclinic

Champlain Final Project 1 420-N52-LA Pet Clinic repo

## Source
This project is based on the spring petclinic microservices (https://github.com/spring-petclinic/spring-petclinic-microservices) implementation.
However, only the customers, visits, vets, and api-gateway services have been retained. In addition, the 
Docker setup has been changed.


## Running the project
Once you have cloned the repo (see the setup instructions below), you need to do the following:

### mailer.env
In the project's top-level folder, you will need to create a file called mailer.env. It will contain SMTP_PASS property set to the hashed password of the mailing service you will be using. 
My students: I'll provide this to you. 


### Docker Profile (for running with docker-compose with ALL SERVICES)
Must be used prior to issuing a PR and in Sprint Reviews.
```
docker-compose build
docker-compose up -d
docker-compose logs -f

or

docker-compose up --build
```

### Docker Profile (for running with docker-compose with all services but NO FRONTEND)
This can be used during development to avoid having to rebuild everything whenever you make a change to the frontend. 
```
docker-compose -f docker-compose_no_FE.yml build
docker-compose -f docker-compose_no_FE.yml up -d
docker-compose logs -f

or

docker-compose -f docker-compose_no_FE.yml up --build
```
### Bring up Frontend
React frontend:
```
localhost:3000/
```
Until it is fixed, to login on React frontend, use need to enter the following URI:
```
localhost:3000/users/login
```
Angular frontend:
```
localhost:8080/
```
In terminal:

Check database contents (did the script run)
```
winpty docker-compose exec mysql3 mysql -uuser -p customers-db -e "select * from owners"
winpty docker-compose exec mysql3 mysql -uuser -p customers-db -e "select * from pets"
winpty docker-compose exec mysql3 mysql -uuser -p customers-db -e "select * from types"
```
When all docker containers are up, test with curl:
```
curl localhost:8080/api/gateway/customer/owners | jq
curl localhost:8080/api/gateway/vet/vets | jq
```

```
ssh-add
```
and enter your passphrase when prompted (Note: if you are not using the default filename, you'll need to specify your key filename `ssh-add ~/.ssh/myprivatekeyname)`. The system will print Identity Added if successful.

---
