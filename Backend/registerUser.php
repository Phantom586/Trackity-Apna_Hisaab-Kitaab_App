<?php

include "db_functions.php";

if ($_SERVER['REQUEST_METHOD']=='POST') {

    $full_name = $_POST["full_name"];
    $email = $_POST["email"];
    $pass = $_POST["password"];

    $res = storeUser($full_name, $email, $pass);

    $response;

    if ($res != "FALSE") {

        $response = array(
            "response" => array(
                "responseCode" => "200",
            ),
            "result" => array(
                "User_ID" => $res["ID"],
            ),
        );

        echo json_encode($response);

    } else {

        $response = array(
            "response" => array(
                "responseCode" => "404",
            ),
            "result" => array(),
        );

        echo json_encode($response);

    }

}

?>