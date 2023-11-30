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

async function refreshFindAll() {
	response = await fetch("db/crew/");

	if (response.ok) {
		const doc = await response.json();
		doc.forEach(addToCrewMembers);
	}
}

function addToCrewMembers(entry){
	var userHtml =	"<div>Name: " + entry.Name + "</div>" +
					"<div>ID: " + entry.CrewID + "</div>" +
					"<div>Rank: " + entry.Rank + "</div>";
					
	var userDiv = document.createElement("div");
	userDiv.setAttribute("class","user flexbox");
	userDiv.setAttribute("id",entry.CrewID);
	userDiv.setAttribute("onclick","remove('"+entry.CrewID+"')");
	userDiv.innerHTML=userHtml;
	document.getElementById("userBoxes").appendChild(userDiv);
}
