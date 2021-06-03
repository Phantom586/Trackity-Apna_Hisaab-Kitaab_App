<?php

include "db_functions.php";

if ($_SERVER['REQUEST_METHOD']=='GET') {

    $user_id = $_POST["user_id"];

    $res = fetchExpense_Types($user_id);

    $response;

    if ($res != "FALSE") {

        $response = array(
            "response" => array(
                "responseCode" => "200",
            ),
            "expense_types_list" => $res,
        );

        echo json_encode($response);

    } else {

        $response = array(
            "response" => array(
                "responseCode" => "404",
            ),
        );

        echo json_encode($response);

    }

}

?>