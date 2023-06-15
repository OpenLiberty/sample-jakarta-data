![](https://github.com/OpenLiberty/open-liberty/blob/master/logos/logo_horizontal_light_navy.png)

# Jakarta Data Sample
TODO Description

## Environment Set Up
To run this sample, first [download](https://github.com/OpenLiberty/sample-jakarta-data/archive/main.zip) or clone this repo - to clone:
```
git clone git@github.com:OpenLiberty/sample-jakarta-data.git
```

### Setup PostgreSQL
You will need a PostgreSQL instance to use this sample. If you have Docker installed, you can run the following from inside the sample-jakarta-data directory:

```
docker build -t liberty_postgres postgres
docker run --name liberty_postgres -d -p 5432:5432 liberty_postgres
```
If you are not using Docker, you will need to create a user with the name `sampleUser` and a password `openliberty` with access to a database named `testdb`

## Running the Sample
From inside the sample-jakarta-data directory, build and start the application in Open Liberty with the following command:
```
./mvnw liberty:dev
```

Once the server has started, the application is availible at http://localhost:9080


### Stop Postgres
If you started postgres using docker, you can stop the container with:
```
docker stop liberty_postgres
```