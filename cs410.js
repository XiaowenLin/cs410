"use strict";
/********************************************************
	This function gets an XmlHttpRequest object

********************************************************/
function GetXHR() { 
	var xmlHttp;
	try { 
		// Chrome, Firefox, Opera 8.0+, Safari, IE7 and later
		xmlHttp = new XMLHttpRequest(); 
	} catch (e) { 
		// Older Internet Explorer 
		try { 
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP"); 
		} catch (e) { 
			try { 
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP"); 
			} catch (e) { 
				return false; 
			} 
		} 
	}
	return xmlHttp; 
}

/********************************************
* searchDocs
* Author: Aaron Snyder
* Created: 4/25/16
* 
* Description:
*		Calls searchDocs.php 
*
* Inputs:
*		find - what to search the documents for
*		near - location to search
*		
********************************************/
function searchDocs(){
  var XHR = GetXHR();
  if (XHR === null) {
     alert("Your browser does not support Ajax!");
     return;
  }
  //displays a message while the module runs
  document.getElementById('results').innerHTML = "<h2>LOADING RESULTS</h2><br /><h3>PLEASE WAIT</h3>";
  //call the searchDocs.php module
  var  urlstr = "searchDocs.php";
  urlstr += "?findVal="       + encodeURIComponent(document.getElementById('find').value);
  urlstr += "&nearVal="       + encodeURIComponent(document.getElementById('near').value);
   urlstr += "&sid=" + Math.random();
  XHR.onreadystatechange = function() {
     if (XHR.readyState === 4) {
         //display results
        document.getElementById('results').innerHTML = XHR.responseText;
        return;
     } 
  };
  XHR.open("GET",urlstr,true);
  XHR.send(null);
   return;
}
