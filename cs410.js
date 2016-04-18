"use strict";
//test function for the eyeglass button
function displayValues(){
	var findVal = document.getElementById('find').value;
	var nearVal = document.getElementById('near').value;
	document.getElementById('results').innerHTML += "Find: " + findVal + " Near: " + nearVal + "<br>";
}
