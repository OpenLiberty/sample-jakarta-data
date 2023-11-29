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

async function addCrewMember() {
	
	var crewMember = {};
	crewMember.name = document.getElementById("crewMemberName").value;
	var rank = document.getElementById("crewMemberRank");
	crewMember.rank = rank.options[rank.selectedIndex].text;
	crewMember.crewID = document.getElementById("crewMemberID").value;

	
	const response = await fetch("db/crew/"+crewMember.crewID, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(crewMember),
	})

	if (response.ok) {
		var i = 0;
		const body = await response.text();
		if (body.length > 0) {
			for (m of JSON.parse(body)) {
				toast(m, i++);
			}
		}
		refreshDocDisplay();
	}
}
	

async function refreshDocDisplay() {
	clearDisplay()

	if (document.getElementById("userDisplay").style.display == 'flex') refreshFindAll();
	if (document.getElementById("docDisplay").style.display == 'flex') refreshFindByRank();

}

function clearDisplay(){
	var elements = ["userBoxes", "Captain", "Engineer", "Officer"];
	for (let i = 0; i < elements.length; i++) {
		var usersDiv = document.getElementById(elements[i]);
		while (usersDiv.firstChild) {
			usersDiv.removeChild(usersDiv.firstChild);
		}
	}
}

async function remove(id) {
	const response = await fetch("db/crew/"+id, {
		method: "DELETE",
	})

	if (response.ok) {
		refreshDocDisplay()
	}

}

function toast(message, index) {
	var length = 3000;
	var toast = document.getElementById("toast");
	setTimeout(function(){ toast.innerText = message; toast.className = "show"; }, length*index);
	setTimeout(function(){ toast.className = toast.className.replace("show",""); }, length + length*index);
}