<?php

include "db_functions.php";

if ($_SERVER['REQUEST_METHOD']=='POST') {

    $user_id = $_POST["user_id"];

    $res = fetchExpenseData($user_id);

    $res1 = fetchExpense_Types($user_id);

    $response;

    if ($res != "FALSE") {

        $response = array(
            "response" => array(
                "responseCode" => "200",
            ),
            "expenses_list" => $res,
            "expense_types_list" => $res1,
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