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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.openliberty.sample.application.CrewMember;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

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
    public void testAddUpdateDeleteCrewMember() {
        assumeTrue(isPostgresAvailable(), "Postgres is not Available");  

        response = client.target(baseURL + "db/crew/test").request().post(Entity.json("{\"name\":\"Mark\",\"rank\":\"Captain\",\"crewID\":\"75\"}"));
        assertEquals(response.getStatus(), 200);

    } 


    private boolean isPostgresAvailable() {
        return checkHostAndPort("localhost", 5432);
        // || checkHostAndPort("otherHost", 5432);
    }
    
    private boolean checkHostAndPort(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}