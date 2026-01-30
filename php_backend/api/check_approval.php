<?php
/**
 * Check Mentor Approval Status API
 * Endpoint: POST /mentorbridge/api/check_approval.php
 * 
 * Checks the current approval status of a mentor
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

$userId = $data->user_id ?? null;
$action = $data->action ?? null;

if (!$userId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "User ID is required"
    ]);
    exit;
}

try {
    $database = new Database();
    $db = $database->getConnection();
    
    // Get mentor approval status
    $query = "SELECT approval_status FROM mentors WHERE user_id = :user_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_id", $userId);
    $stmt->execute();
    
    if ($stmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "Mentor profile not found"
        ]);
        exit;
    }
    
    $mentor = $stmt->fetch(PDO::FETCH_ASSOC);
    $approvalStatus = $mentor['approval_status'];
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "approval_status" => $approvalStatus,
        "message" => "Status: " . ucfirst($approvalStatus)
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>
