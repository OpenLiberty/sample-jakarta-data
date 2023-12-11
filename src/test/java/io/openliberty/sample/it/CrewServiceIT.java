/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.sample.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

public class CrewServiceIT {
    
    private static String baseURL;
    
    private Client client;
    private Response response;

    @BeforeAll
    public static void init() {             
        String port = System.getProperty("http.port");
        baseURL = "http://localhost:" + port + "/";
    }


    @BeforeEach
    public void setup() {
      client = ClientBuilder.newClient();
      assumeTrue(isPostgresAvailable(), "Postgres is not Available");
      response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"75\"}"));
      assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
      response = client.target(baseURL + "db/crew/").request().delete(); //Delete All before each test
      assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class)); 
    }

    @AfterEach
    public void teardown() {
            client.close();
    }

    @Test
    public void testAddGetDeleteCrewMember() {
      
        //Remove Existing
        response = client.target(baseURL + "db/crew/75").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class));     

        //Check Add
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"75\",\"ship\":\"Liberty Saucer\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));

        //Check Get
        response = client.target(baseURL + "db/crew").request().get();
        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonObject expectedObject = Json.createObjectBuilder().add("Name", "Mark").add("CrewID", 75).add("Rank", "Captain").add("Ship", "Liberty Saucer").build();
        
        boolean found = false;
        for (JsonValue value : reader.readArray()) {
            if (value.asJsonObject().equals(expectedObject)) found = true;
        }
        assertTrue(found,"Json object not found in returned array");

        //Check Delete
        response = client.target(baseURL + "db/crew/75").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class)); 

        //Confirm Delete
        response = client.target(baseURL + "db/crew").request().get();
        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        expectedObject = Json.createObjectBuilder().add("Name", "Mark").add("CrewID", 75).add("Rank", "Captain").add("Ship", "Liberty Saucer").build();
        
        found = false;
        for (JsonValue value : reader.readArray()) {
            if (value.asJsonObject().equals(expectedObject)) found = true;
        }
        assertFalse(found,"Json object should not have been found in Array");
    }
    
    @Test
    public void testValidationCrewMember() {
        //Name Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"\",\"rank\":\"Captain\",\"crewID\":\"75\",\"ship\":\"Liberty Saucer\"}"));

        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonArray array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"All crew members must have a name!\"", array.get(0).toString());   
        
        //Rank Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Scientist\",\"crewID\":\"75\",\"ship\":\"Liberty Saucer\"}"));

        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"Crew member must be one of the listed ranks!\"", array.get(0).toString());    

        //CrewID Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"-1\",\"ship\":\"Liberty Saucer\"}"));

        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"ID Number must be a non-negative integer!\"", array.get(0).toString());    

        //Ship Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"75\",\"ship\":\"Quarkus Speedboat\"}"));

        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"Crew member must be assigned to one of the listed ships!\"", array.get(0).toString());    


    }

    /**
     * Test findByRank, currently expects ordering to remain the same, which the spec doesn't require.
     */
    @Test
    public void testFindByRank() {
        response = client.target(baseURL + "db/crew/it1").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Engineer\",\"crewID\":75,\"ship\":\"Liberty Saucer\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/it2").request().post(Entity.json("{\"name\":\"Jim\",\"rank\":\"Captain\",\"crewID\":64,\"ship\":\"Liberty Saucer\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/it3").request().post(Entity.json("{\"name\":\"Alex\",\"rank\":\"Engineer\",\"crewID\":15,\"ship\":\"Jakarta Sailboat\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));

        //Check findByRank("Captain")
        response = client.target(baseURL + "db/crew/rank/Captain").request().get();
        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonArray array = reader.readArray();
        JsonArray expectedArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("Name", "Jim").add("CrewID", 64.add("Rank", "Captain").add("Ship", "Liberty Saucer")).build()).build();
        assertEquals(expectedArray, array);

        //Check findByRank("Engineer")
        response = client.target(baseURL + "db/crew/rank/Engineer").request().get();
        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        expectedArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("Name", "Mark").add("CrewID", 75).add("Rank", "Engineer").add("Ship", "Liberty Saucer").build())
                                                 .add(Json.createObjectBuilder().add("Name", "Alex").add("CrewID", 15).add("Rank", "Engineer").add("Ship", "Jakarta Sailboat").build()).build();
        assertEquals(expectedArray, array);

        //Check findByRank("Officer")
        response = client.target(baseURL + "db/crew/rank/Officer").request().get();
        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        expectedArray = Json.createArrayBuilder().build();
        assertEquals(expectedArray, array);       

        //Check find

        response = client.target(baseURL + "db/crew/75").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/64").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class)); 
        response = client.target(baseURL + "db/crew/15").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class)); 

    }

    /**
     * Test findByShipSizeAndRank, currently expects ordering to remain the same, which the spec doesn't require.
     */
    @Test
    public void testFindByShipSizeAndRank() {
        response = client.target(baseURL + "db/crew/it1").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Engineer\",\"crewID\":75,\"ship\":\"Liberty Saucer\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/it2").request().post(Entity.json("{\"name\":\"Jim\",\"rank\":\"Captain\",\"crewID\":64,\"ship\":\"Liberty Saucer\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/it3").request().post(Entity.json("{\"name\":\"Alex\",\"rank\":\"Engineer\",\"crewID\":15,\"ship\":\"WebSphere Battleship\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));
        response = client.target(baseURL + "db/crew/it4").request().post(Entity.json("{\"name\":\"Nathan\",\"rank\":\"Captain\",\"crewID\":30,\"ship\":\"Jakarta Sailboat\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));

        //Check findByShipSizeAndRank
        response = client.target(baseURL + "db/crew/shipSize/small/rank/Engineer").request().get();
        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonArray array = reader.readArray();
        JsonArray expectedArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("Name", "Mark").add("CrewID", 75).add("Ship", "Liberty Saucer").build()).build();
        assertEquals(expectedArray, array); 
        
        response = client.target(baseURL + "db/crew/shipSize/large/rank/Engineer").request().get();
        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        expectedArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("Name", "Alex").add("CrewID", 15).add("Ship", "WebSphere Battleship").build()).build();
        assertEquals(expectedArray, array);    
        
        response = client.target(baseURL + "db/crew/shipSize/small/rank/Captain").request().get();
        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        expectedArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("Name", "Jim").add("CrewID", 64).add("Ship", "Liberty Saucer").build())
                                                 .add(Json.createObjectBuilder().add("Name", "Nathan").add("CrewID", 30).add("Ship", "Jakarta Sailboat").build()).build();
        assertEquals(expectedArray, array);    
    }


    private static boolean isPostgresAvailable() {
        return checkHostAndPort("localhost", 5432);
    }
    
    private static boolean checkHostAndPort(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}