<?php

include "db_functions.php";

if ($_SERVER['REQUEST_METHOD']=='POST') {

    $json_data = $_POST["data"];

    $data = json_decode($json_data);

    $response = array();
    $store_response = array();
    $update_response = array();

    $user_id = $data->User_ID;
    $store_data = $data->store;
    $delete_data = $data->delete;

    // For Delete Data.
    for ($i = 0; $i < count($delete_data); $i++) {

        $res = deleteExpenseType($user_id, $delete_data[$i]->ID);

    }

    // For Store Data.
    for ($i = 0; $i < count($store_data); $i++) {

        $res = storeExpenseType($user_id, $store_data[$i]);

        $a = array();

        if ($res) {

            $a["id"] = $store_data[$i]->ID;
            $a["status"] = "Yes";
            array_push($store_response, $a);

        } else {
            
            $a["id"] = $store_data[$i]->ID;
            $a["status"] = "No";
            array_push($store_response, $a);

        }

    }

    $response = array(
        "response" => array(
            "responseCode" => "200",
        ),
        "store_response" => $store_response,
        "update_response" => $update_response
    );

echo json_encode($response);

}

?>