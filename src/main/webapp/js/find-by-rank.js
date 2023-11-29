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

async function refreshFindByRank() {
    response = await fetch("db/crew/rank/Captain");
	parseFindByRank("Captain", response);

	response = await fetch("db/crew/rank/Officer");
	parseFindByRank("Officer", response);

	response = await fetch("db/crew/rank/Engineer");
	parseFindByRank("Engineer", response);
}

async function parseFindByRank(rank, response) {
	if (response.ok) {
		membersList = await response.json()
		if (membersList.length > 0) {
			document.getElementById(rank).innerText = rank
		}
		for (let i = 0; i < membersList.length; i++) {
			addToCrewMembersByRank(membersList[i], rank)
		}
	}
}


function addToCrewMembersByRank(entry, rank) {
	var userHtml =	"<div>Name: " + entry.Name + "</div>" +
					"<div>ID: " + entry.CrewID + "</div>";
					
	var userDiv = document.createElement("div");
	userDiv.setAttribute("class","user flexbox");
	userDiv.setAttribute("id",entry.CrewID);
	userDiv.innerHTML=userHtml;
	document.getElementById(rank).appendChild(userDiv);
}