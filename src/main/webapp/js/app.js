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
	crewMember.ship = document.getElementById("crewMemberShip").value

	
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
		} else {
			refreshDisplay();
		}
	} else {
		const body = await response.text();
		if (body.includes("Unable to deserialize property 'crewID'"))
			toast("ID Number must be a non-negative integer!",0)
	}
}

function setActiveQuery(query) {
	var nodes = document.querySelectorAll('.queryContainer');
	for (var i = 0; i < nodes.length; i++) {
		if (nodes.item(i).id == query) nodes.item(i).style.display = 'flex';
		else nodes.item(i).style.display = 'none';
	}

	refreshDisplay();
}
	

async function refreshDisplay() {
	clearDisplay()

	if (document.getElementById("findAll").style.display == 'flex') refreshFindAll();
	if (document.getElementById("findByRank").style.display == 'flex') refreshFindByRank();

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
		refreshDisplay()
	}

}

function toast(message, index) {
	var length = 3000;
	var toast = document.getElementById("toast");
	setTimeout(function(){ toast.innerText = message; toast.className = "show"; }, length*index);
	setTimeout(function(){ toast.className = toast.className.replace("show",""); }, length + length*index);
}