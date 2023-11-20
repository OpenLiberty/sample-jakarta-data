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
import jakarta.json.JsonObjectBuilder;
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
    }

    @AfterEach
    public void teardown() {
            client.close();
    }

    @Test
    public void testAddGetDeleteCrewMember() {
        assumeTrue(isPostgresAvailable(), "Postgres is not Available");
        
        //Remove Existing
        response = client.target(baseURL + "db/crew/75").request().delete();
        assertEquals(204, response.getStatus(), "output: " + response.readEntity(String.class));     

        //Check Add
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"75\"}"));
        assertEquals(200, response.getStatus(), "output: " + response.readEntity(String.class));

        //Check Get
        response = client.target(baseURL + "db/crew").request().get();
        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonObject expectedObject = Json.createObjectBuilder().add("Name", "Mark").add("CrewID", "75").add("Rank", "Captain").build();
        
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
        expectedObject = Json.createObjectBuilder().add("Name", "Mark").add("CrewID", "75").add("Rank", "Captain").build();
        
        found = false;
        for (JsonValue value : reader.readArray()) {
            if (value.asJsonObject().equals(expectedObject)) found = true;
        }
        assertFalse(found,"Json object should not have been found in Array");
    }
    
    @Test
    public void testValidationCrewMember() {
        assumeTrue(isPostgresAvailable(), "Postgres is not Available");

        //Name Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"\",\"rank\":\"Captain\",\"crewID\":\"75\"}"));

        JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        JsonArray array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"All crew members must have a name!\"", array.get(0).toString());   
        
        //Rank Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Scientist\",\"crewID\":\"75\"}"));

        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"Crew member must be one of the listed ranks!\"", array.get(0).toString());    

        //CrewID Validation
        response = client.target(baseURL + "db/crew/it").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"-1\"}"));

        reader = Json.createReader(new StringReader(response.readEntity(String.class)));
        array = reader.readArray();
        assertEquals(1, array.size(), "Validation array should have only contained 1 message");
        assertEquals("\"ID Number must be a non-negative integer!\"", array.get(0).toString());    

    }

    private boolean isPostgresAvailable() {
        return checkHostAndPort("localhost", 5432);
    }
    
    private boolean checkHostAndPort(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}