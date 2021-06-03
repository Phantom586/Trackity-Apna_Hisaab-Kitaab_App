<?php

define('DB_HOST', 'localhost');
define('DB_UNAME', 'root');
define('DB_PASS', "DBP@ssPS#123");
define('DB_NAME', 'EXP_Tracker');

$conn = new mysqli(DB_HOST, DB_UNAME, DB_PASS, DB_NAME);

if ($conn->connect_error) 
	die("Connection Failed". $conn->connect_error);
else
	// echo "Connection Established";

?>
