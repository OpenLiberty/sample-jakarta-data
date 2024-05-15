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

import java.util.List;

import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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

	@POST
	@Path("/{id}") 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON) 
	public String add(CrewMember crewMember) {

		try {
			crewMembers.save(crewMember);
		} catch (ConstraintViolationException x) {
			JsonArrayBuilder messages = Json.createArrayBuilder();
			for (ConstraintViolation<?> v : x.getConstraintViolations()) {
				messages.add(v.getMessage());
			}
			return messages.build().toString();
		}

		return "";
	}

	@DELETE
	@Path("/{id}")
	public void remove(@PathParam("id") int id) {
		crewMembers.deleteByCrewID(id);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String retrieve() {

		Iterable<CrewMember> crewMembersIterable = crewMembers.findAll()::iterator;
		
		return crewMembersToJsonArray(crewMembersIterable);
	}

	@GET
	@Path("/rank/{rank}")
	@Produces(MediaType.APPLICATION_JSON)
	public String retrieveByRank(@PathParam("rank") String rank) {
		
		List<CrewMember> crewMembersList = crewMembers.findByRank(Rank.fromString(rank));

		return crewMembersToJsonArray(crewMembersList);
	}

	@GET
	@Path("/rank/{rank}/page/{pageNum}")
	@Produces(MediaType.APPLICATION_JSON)
	public String retrieveByRank(@PathParam("rank") String rank,
								 @PathParam("pageNum") long pageNum) {

		PageRequest pageRequest = PageRequest.ofPage(pageNum).size(5);

		Page<CrewMember> page = crewMembers.findByRank(Rank.fromString(rank), 
                                                       pageRequest, 
                                                       Sort.asc("name"), 
                                                       Sort.asc("id"));

		return crewMembersToJsonArray(page);
	}

	@GET
	@Path("/shipSize/{shipSize}/rank/{rank}")
	public String retrieveByShipSizeAndRank(@PathParam("shipSize") Ship.Size size, @PathParam("rank") String rank) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (CrewMember c : crewMembers.findByShipSizeAndRank(size, Rank.fromString(rank))) {	
			JsonObject json = Json.createObjectBuilder()
								.add("Name", c.name)
								.add("CrewID", c.crewID)
								.add("Ship", c.ship.shipName).build();
			jab.add(json);
		}
		return jab.build().toString();
	}

	@DELETE
	public void remove() {
		crewMembers.deleteAll();
	}

	private String crewMembersToJsonArray(Iterable<CrewMember> crewMembers) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (CrewMember c : crewMembers) {
			JsonObject json = Json.createObjectBuilder()
								.add("Name", c.name)
								.add("CrewID", c.crewID)
								.add("Rank",c.rank.toString())
								.add("Ship", c.ship.shipName).build();
			jab.add(json);
		}
		return jab.build().toString();

	}
}