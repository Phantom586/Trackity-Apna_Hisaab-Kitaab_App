<?php

include 'db_functions.php';

$response;

if($_SERVER['REQUEST_METHOD']=="POST"){

    $u_key = $_POST["u_key"];

    $result = fetch_latest_app_version($u_key);

    if($result != "FALSE"){

        $response = array(
            array(
                "error" => FALSE
            ),
            array(
                "App_Version" => $result["Value"],
            )
        );

        echo json_encode($response);

    } else {
        
        $response = array(
            array(
                "error" => TRUE
            )
        );
        echo json_encode($response);

    }


}

?>