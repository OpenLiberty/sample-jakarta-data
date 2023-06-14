/*******************************************************************************
* Copyright (c) 2019 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

function addCrewMember() {
	var crewMember = {};
	crewMember.name = document.getElementById("crewMemberName").value;
	var rank = document.getElementById("crewMemberRank");
	crewMember.rank = rank.options[rank.selectedIndex].text;
	crewMember.crewID = document.getElementById("crewMemberID").value;

	
	var request = new XMLHttpRequest();

	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var i = 0;
			if (this.response != '') {
				for (m of JSON.parse(this.response)) {
					toast(m, i++);
				}
			}
		}
		refreshDocDisplay();
	}

	request.open("POST", "db/crew/"+crewMember.crewID, true);
	request.setRequestHeader("Content-type", "application/json");
	request.send(JSON.stringify(crewMember));
}
	

function refreshDocDisplay() {
	var request = new XMLHttpRequest();
	
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			clearDisplay()		
			doc = JSON.parse(this.responseText);
			
			doc.forEach(addToDisplay);
			if (doc.length > 0) {
				document.getElementById("userDisplay").style.display = 'flex';
				document.getElementById("docDisplay").style.display = 'flex';
			} else {
				document.getElementById("userDisplay").style.display = 'none';
				document.getElementById("docDisplay").style.display = 'none';
			}
			document.getElementById("docText").innerHTML = JSON.stringify(doc,null,2);
		}
	}

	request.open("GET", "db/crew/", true);
	request.send();
}

function addToDisplay(entry){
	var userHtml =	"<div>Name: " + entry.Name + "</div>" +
					"<div>ID: " + entry.CrewID + "</div>" +
					"<div>Rank: " + entry.Rank + "</div>";
					
	var userDiv = document.createElement("div");
	userDiv.setAttribute("class","user flexbox");
	userDiv.setAttribute("id",entry._id.$oid);
	userDiv.setAttribute("onclick","remove('"+entry._id.$oid+"')");
	userDiv.innerHTML=userHtml;
	document.getElementById("userBoxes").appendChild(userDiv);
}

function clearDisplay(){
	var usersDiv = document.getElementById("userBoxes");
	while (usersDiv.firstChild) {
		usersDiv.removeChild(usersDiv.firstChild);
	}
}

function remove(id) {
	var request = new XMLHttpRequest();
	
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById(id).remove();
			refreshDocDisplay()
		}
	}

	request.open("DELETE", "db/crew/"+id, true);
	request.send();
}

function toast(message, index) {
	var length = 3000;
	var toast = document.getElementById("toast");
	setTimeout(function(){ toast.innerText = message; toast.className = "show"; }, length*index);
	setTimeout(function(){ toast.className = toast.className.replace("show",""); }, length + length*index);
}