![](https://github.com/OpenLiberty/open-liberty/blob/master/logos/logo_horizontal_light_navy.png)

# Jakarta Data Sample
This sample shows how to store and retrieve data using Jakarta Data. Jakarta Data is planned to release in Jakarta EE 11, and is currently in beta in Open Liberty.

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
If you are not using Docker, you will need to create a user with the name `sampleUser` and password `openliberty` with access to a database named `testdb`

## Running the Sample
From inside the sample-jakarta-data directory, build and start the application in Open Liberty with the following command:
```
./mvnw liberty:dev
```

Once the server has started, the application is availible at http://localhost:9080

### Try it out
Give the sample a try by registering a crew member. Enter a name (a String), an ID Number (an Integer), and select a Rank from the menu, then click 'Register Crew Member'.

Two more boxes will appear, one with all of your crew members (which you can click to remove) and one showing your crew members sorted by rank.

### How it works
This application provides a few REST endpoints to demonstrate some of the capabilities of Jakarta Data. By defining an Entiy class ([CrewMember](src/main/java/io/openliberty/sample/application/CrewMember.java)) you can create a Repository interface ([CrewMembers](src/main/java/io/openliberty/sample/application/CrewMembers.java)) with query methods. When the Repository is injected with CDI, Jakarta Data will provide an implementation of the interface, including implementations of the query methods.

We can start by injecting the Repository in our application using CDI
```java
@Inject
CrewMembers crewMembers;
```


The first endpoint persists a crewMember in the database by calling `crewMembers.save()`

```java
public String add(CrewMember crewMember) {
    
    //...
    //Jakarta Bean Validation
    //...

    crewMembers.save(crewMember);
```

To remove an individual crewMember from the database based on the ID, you can use `crewMembers.deleteByCrewID`
```java
public String remove(@PathParam("id") String id) {
    crewMembers.deleteByCrewID(id);
```

In order to display all of our crewMembers, you can get all of them easily by calling `crewMembers.findAll()`
```java
public String retrieve() {
    JsonArrayBuilder jab = Json.createArrayBuilder();
    for (CrewMember c : crewMembers.findAll()) {	
```
In the `CrewMembers.java` file we can see that these will be returned sorted alphabetically, using `@OrderBy("name")`
```java
@OrderBy("name")
List<CrewMember> findAll();
```

Finally, for a slightly more complex operation, we can ask for a subset of the crewMembers with a given Rank, using `crewMembers.findByRank()`
```java
public String retrieveByRank(@PathParam("rank") String rank) {
    JsonArrayBuilder jab = Json.createArrayBuilder();
    for (CrewMember c : crewMembers.findByRank(rank)) {	
```

## Stop Postgres
When you are done trying out the sample application, you can stop the Postgres container with:
```
docker stop liberty_postgres
```

## Where to go next

Check out the Jakarta Data Specification on GitHub: https://github.com/jakartaee/data  
You can make suggestions or report bugs by opening an issue, or star the repository to show you're interested.