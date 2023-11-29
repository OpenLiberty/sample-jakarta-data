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


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class CrewMember {

	@NotEmpty(message = "All crew members must have a name!")
	private String name;

	@NotNull(message = "Crew member must be one of the listed ranks!")
	private Rank rank;

	@Id
	@Positive(message = "ID Number must be a non-negative integer!")
	private int crewID; 

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public int getCrewID(){
		return crewID;
	}

	public void setCrewID(int crewID) {
		this.crewID = crewID;
	}

	public String toString() {
		return "Name: " + name + "<br>CrewID: " + crewID + "<br>Rank: " + rank;

	}

}