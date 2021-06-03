<?php

include "db_functions.php";

if ($_SERVER['REQUEST_METHOD']=='POST') {

    $email = $_POST["email"];

    $res = verifyUser($email);

    $response;

    if ($res != "FALSE") {

        $response = array(
            "response" => array(
                "responseCode" => "200",
            ),
            "exists" => true,
            "user_id" => $res["ID"],
        );

        echo json_encode($response);

    } else {

        $response = array(
            "response" => array(
                "responseCode" => "404",
            ),
            "exists" => false,
        );

        echo json_encode($response);

    }

}

?>