<?php
/**
 * Test Endpoint to verify XAMPP backend is working
 * URL: http://localhost/mentorbridge/api/test.php
 */

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

include_once '../config/cors.php';

// Test 1: PHP is working
$phpWorking = true;

// Test 2: Database connection
$dbWorking = false;
$dbMessage = "";

try {
    include_once '../config/database.php';
    $database = new Database();
    $db = $database->getConnection();
    
    if ($db) {
        $dbWorking = true;
        $dbMessage = "Database connection successful";
        
        // Count records
        $stmt = $db->query("SELECT COUNT(*) as count FROM mentors");
        $mentorCount = $stmt->fetch(PDO::FETCH_ASSOC)['count'];
        
        $stmt = $db->query("SELECT COUNT(*) as count FROM users");
        $userCount = $stmt->fetch(PDO::FETCH_ASSOC)['count'];
        
        $dbMessage .= " | Mentors: $mentorCount | Users: $userCount";
    }
} catch (Exception $e) {
    $dbMessage = "Database error: " . $e->getMessage();
}

// Return test results
$response = [
    "status" => "success",
    "message" => "XAMPP Backend Test",
    "timestamp" => date('Y-m-d H:i:s'),
    "tests" => [
        "php" => [
            "working" => $phpWorking,
            "version" => phpversion(),
            "message" => "PHP is running"
        ],
        "database" => [
            "working" => $dbWorking,
            "message" => $dbMessage
        ],
        "cors" => [
            "working" => true,
            "message" => "CORS headers enabled"
        ]
    ],
    "endpoints" => [
        "login" => "POST http://localhost/mentorbridge/api/login.php",
        "register" => "POST http://localhost/mentorbridge/api/register.php",
        "mentors" => "GET http://localhost/mentorbridge/api/get_mentors.php",
        "mentor" => "GET http://localhost/mentorbridge/api/get_mentor.php?id=1",
        "sessions" => "GET http://localhost/mentorbridge/api/get_sessions.php?userId=1"
    ],
    "androidConfig" => [
        "emulator" => "http://10.0.2.2/mentorbridge/api/",
        "device" => "http://YOUR_PC_IP/mentorbridge/api/ (Find IP with ipconfig)"
    ],
    "testUsers" => [
        ["email" => "john@example.com", "password" => "password"],
        ["email" => "jane@example.com", "password" => "password"]
    ]
];

// Set success/error status
if (!$dbWorking) {
    $response["status"] = "error";
    $response["message"] = "Database not working! Run schema.sql in phpMyAdmin";
    http_response_code(500);
} else {
    http_response_code(200);
}

header('Content-Type: application/json');
echo json_encode($response, JSON_PRETTY_PRINT);
?>
