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
package io.openliberty.sample.application;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/crew")
@ApplicationScoped
public class CrewService {

	@Inject
	CrewMembers crewMembers;

	@Inject
	Validator validator;

	@POST
	@Path("/{id}") 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON) 
	public String add(CrewMember crewMember) {
		
		Set<ConstraintViolation<CrewMember>> violations = validator.validate(crewMember);
		if(violations.size() > 0) {
			JsonArrayBuilder messages = Json.createArrayBuilder();
			for (ConstraintViolation<CrewMember> v : violations) { 			
				messages.add(v.getMessage());
			}
			return messages.build().toString();
		}

		crewMembers.save(crewMember);

		return "";
	}

	@DELETE
	@Path("/{id}")
	public void remove(@PathParam("id") String id) {
		crewMembers.deleteByCrewID(id);
	}

	@GET
	public String retrieve() {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		crewMembers.findAll().forEach( c -> {
			JsonObject json = Json.createObjectBuilder()
								.add("Name", c.getName())
								.add("CrewID", c.getCrewID())
								.add("Rank",c.getRank()).build();
			jab.add(json);

	  	});
		return jab.build().toString();
	}

	@GET
	@Path("/rank/{rank}")
	public String retrieveByRank(@PathParam("rank") String rank) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (CrewMember c : crewMembers.findByRank(rank)) {	
			JsonObject json = Json.createObjectBuilder()
								.add("Name", c.getName())
								.add("CrewID", c.getCrewID()).build();
			jab.add(json);
		}
		return jab.build().toString();
	}

	@DELETE
	@Path("/")
	public void remove() {
		crewMembers.deleteAll();
	}
}