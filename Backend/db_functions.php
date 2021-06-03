<?php

    function storeUser($full_name, $email, $pass) {

        require 'Connect.php';

        $q = "INSERT INTO User_Table (ID, Full_Name, Email, Password, Signup_Timestamp) VALUES(null, '$full_name', '$email', '$pass', null)";

        if ($conn->query($q) === TRUE) {

            $q = "SELECT ID FROM User_Table WHERE Email='$email'";

            $result = $conn->query($q);

            if( $result->num_rows > 0){
                if ( $row = $result->fetch_array() ) {
                    return $row;
            }
        }
            
        } else {
            return "FALSE";
        }

    } 

    function fetchExpense_Types($user_id) {

        require 'Connect.php';

        $q = "SELECT ID, Head_ID, Name FROM Expense_Type WHERE User_ID='$user_id'";

        $result = $conn->query($q);

        if( $result->num_rows > 0){
            if ( $row = $result->fetch_all() ) {
                return $row;
            }
        } else {
            return "FALSE";
        }

    }

    function storeExpense($user_id, $data) {

        require 'Connect.php';

        $cols = "(User_ID, ID, Head_ID, SubHead_ID, Description, Amount, Date, Time)";

        $q = "INSERT INTO Expense " .$cols. " VALUES('$user_id', '$data->ID', '$data->Head_ID', '$data->SubHead_ID', '$data->Description', '$data->Amount', '$data->Date', '$data->Time')";
        // echo $q . "\n";

        if ($conn->query($q) === TRUE) {
            return TRUE;
        } else {
            echo mysqli_error($conn);
            return FALSE;
        }

    }

    function updateExpense($user_id, $data) {

        require 'Connect.php';

        $updatedValues = "Head_ID='$data->Head_ID', SubHead_ID='$data->SubHead_ID', Description='$data->Description', Amount='$data->Amount', Date='$data->Date', Time='$data->Time'";

        $q = "UPDATE Expense SET " .$updatedValues. " WHERE User_ID='$user_id' AND ID='$data->ID'";
        
        if ($conn->query($q) ===  TRUE){
            return TRUE;
        } else {
            return FALSE;
        }

    }

    function storeExpenseType($user_id, $data) {

        require 'Connect.php';

        $q = "INSERT INTO Expense_Type (User_ID, ID, Head_ID, Name) VALUES('$user_id', '$data->ID', '$data->Parent_ID', '$data->Name')";

        if ($conn->query($q) === TRUE) {
            return TRUE;
        } else {
            return FALSE;
        }

    }

    function fetchExpenseData($user_id) {

        require 'Connect.php';

        $cols = "ID, Head_ID, SubHead_ID, Description, Amount, Date, Time";

        $q = "SELECT " .$cols. " FROM Expense WHERE User_ID='$user_id'";

        $result = $conn->query($q);

        if( $result->num_rows > 0){
            if ( $row = $result->fetch_all() ) {
                return $row;
            }
        } else {
            return "FALSE";
        }

    }

    function verifyUser($email) {

        require 'Connect.php';

        $q = "SELECT ID FROM User_Table WHERE Email='$email'";

        $result = $conn->query($q);

        if( $result->num_rows > 0){
            if ( $row = $result->fetch_array() ) {
                return $row;
            }
        } else {
            return "FALSE";
        }

    }

    function deleteExpense($user_id, $id) {

        require 'Connect.php';

        $q = "DELETE FROM Expense WHERE User_ID='$user_id' AND ID='$id'";
        
        if ($conn->query($q) ===  TRUE){
            return TRUE;
        } else {
            return FALSE;
        }

    }

    function deleteExpenseType($user_id, $id) {

        require 'Connect.php';

        $q = "DELETE FROM Expense_Type WHERE User_ID='$user_id' AND ID='$id'";
        
        if ($conn->query($q) ===  TRUE){
            return TRUE;
        } else {
            return FALSE;
        }

    }

?>