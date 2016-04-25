<?php
/************************************
Author: Aaron Snyder
Date: 4/25/16
************************************/
//retrieve user inputs
$findVal  = rawurldecode($_GET['findVal']);
$nearVal  = rawurldecode($_GET['nearVal']);
//echo the inputs for a test
echo "Find: {$findVal} Near: {$nearVal}";
return;
//awaiting java function details
/*
try {
   //Initialize the java connection
   $javaConn = new Java(''); //Initialize the java connection
   if($javaConn === NULL) {
      echo "ERROR creating java connection";
      return;
   }

   // Run the query
   $result = $javaConn->searchDocs($findVal, $nearVal);

} catch (Exception $e) {
   echo "ERROR " . $e;
   return;
} 
*/
?>
