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
?>
