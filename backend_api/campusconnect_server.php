<?php
// campusconnect_server.php

// 1. DATABASE CONFIGURATION
// ------------------------------------------------
$host = "localhost";
$user = "root";      // Default XAMPP user
$pass = "";          // Default XAMPP password is empty
$db   = "campus_connect_db";

// Create connection
$conn = new mysqli($host, $user, $pass, $db);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

// 2. RECEIVE DATA FROM KOTLIN
// ------------------------------------------------
// Kotlin sends data as JSON, so we read the raw input
$json_input = file_get_contents('php://input');
$data = json_decode($json_input, true);

// Check if 'action' is set (this tells PHP what to do)
if (!isset($data['action'])) {
    echo json_encode(["status" => "error", "message" => "No action specified"]);
    exit();
}

$action = $data['action'];

// 3. HANDLE ACTIONS
// ------------------------------------------------
switch ($action) {
    
    case 'register':
        registerUser($conn, $data);
        break;

    case 'login':
        loginUser($conn, $data); // <--- UPDATED: Now calls the actual function
        break;

    default:
        echo json_encode(["status" => "error", "message" => "Invalid action"]);
}

// 4. FUNCTIONS
// ------------------------------------------------

function registerUser($conn, $data) {
    // Extract data safely
    $student_number = $data['student_number'];
    $fname = $data['fname'];
    $mname = $data['mname'] ?? ''; // Use empty string if optional
    $lname = $data['lname'];
    $gender = $data['gender'];
    $dob = $data['dob'];
    $email = $data['email'];
    $phone = $data['phone'];
    $college = $data['college'];
    $program = $data['program'];
    
    // Encrypt password!
    $password = password_hash($data['password'], PASSWORD_DEFAULT); 

    // Check if email or student number already exists
    $check = $conn->prepare("SELECT id FROM users WHERE email = ? OR student_number = ?");
    $check->bind_param("ss", $email, $student_number);
    $check->execute();
    $check->store_result();

    if ($check->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "User already exists"]);
        return;
    }

    // Insert new user
    $sql = "INSERT INTO users (student_number, fname, mname, lname, gender, dob, email, phone, college, program, password) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssssssss", $student_number, $fname, $mname, $lname, $gender, $dob, $email, $phone, $college, $program, $password);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Registration successful"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Database error"]);
    }
}

// NEW FUNCTION: LOGIN
function loginUser($conn, $data) {
    $username = $data['username']; // Can be email OR student_number
    $password = $data['password'];

    // Prepare SQL to look for EITHER email OR student number
    $sql = "SELECT id, fname, lname, password FROM users WHERE email = ? OR student_number = ?";
    $stmt = $conn->prepare($sql);
    
    // Bind the same username variable twice (for the two ? placeholders)
    $stmt->bind_param("ss", $username, $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        // Verify the encrypted password
        if (password_verify($password, $row['password'])) {
            // Success! We don't send the password back, just the user info
            echo json_encode([
                "status" => "success", 
                "message" => "Login successful",
                "user_id" => $row['id'],
                "name" => $row['fname'] . " " . $row['lname']
            ]);
        } else {
            echo json_encode(["status" => "error", "message" => "Invalid password"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "User not found"]);
    }
}
?>