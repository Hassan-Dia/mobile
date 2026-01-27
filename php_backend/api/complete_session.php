<?php
/**
 * Complete Session API
 * Endpoint: POST /mentorbridge/api/complete_session.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed. Use POST."
    ]);
    exit;
}

$data = json_decode(file_get_contents("php://input"));

// Accept both camelCase and snake_case
$sessionId = $data->session_id ?? $data->sessionId ?? null;

if (!$sessionId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Session ID is required"
    ]);
    exit;
}

try {
    $database = new Database();
    $db = $database->getConnection();
    
    // Check if session exists
    $checkQuery = "SELECT id, status FROM sessions WHERE id = :session_id";
    $checkStmt = $db->prepare($checkQuery);
    $checkStmt->bindParam(":session_id", $sessionId);
    $checkStmt->execute();
    
    if ($checkStmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "Session not found"
        ]);
        exit;
    }
    
    // Mark session as completed
    $updateQuery = "UPDATE sessions 
                    SET status = 'completed'
                    WHERE id = :session_id";
    $updateStmt = $db->prepare($updateQuery);
    $updateStmt->bindParam(":session_id", $sessionId);
    
    if ($updateStmt->execute()) {
        http_response_code(200);
        echo json_encode([
            "success" => true,
            "message" => "Session marked as complete",
            "data" => [
                "session_id" => (int)$sessionId,
                "status" => "completed"
            ]
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Failed to complete session"
        ]);
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>
