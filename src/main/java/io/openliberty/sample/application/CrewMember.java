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


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class CrewMember {

	@NotEmpty(message = "All crew members must have a name!")
	public String name;

	@NotNull(message = "Crew member must be one of the listed ranks!")
	public Rank rank;

	@Id
	@Positive(message = "ID Number must be a non-negative integer!")
	public int crewID; 

	@Embedded
	@NotNull(message = "Crew member must be assigned to one of the listed ships!")
	public Ship ship;
 

	public String toString() {
        String shipName = ship != null ? "<br>Ship: " + ship.shipName : "";
		return "Name: " + name + "<br>CrewID: " + crewID + "<br>Rank: " + rank + shipName;
	}

}