<?php
/**
 * Approve or Reject Mentor
 * Endpoint: POST /mentorbridge/api/approve_mentor.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Create database connection
$database = new Database();
$db = $database->getConnection();

// Get posted data
$data = json_decode(file_get_contents("php://input"));

if (!isset($data->mentor_user_id) || !isset($data->action)) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Missing required fields: mentor_user_id and action"
    ]);
    exit;
}

$mentor_user_id = (int)$data->mentor_user_id;
$action = $data->action; // "approve" or "reject"

try {
    // First, check if this mentor exists in users table
    $query = "SELECT id, name, email FROM users WHERE id = :user_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':user_id', $mentor_user_id);
    $stmt->execute();
    
    if ($stmt->rowCount() == 0) {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "User not found"
        ]);
        exit;
    }
    
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($action === "approve") {
        // Check if mentor already exists in mentors table
        $query = "SELECT id FROM mentors WHERE user_id = :user_id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(':user_id', $mentor_user_id);
        $stmt->execute();
        
        if ($stmt->rowCount() > 0) {
            // Mentor already exists, just update status
            $query = "UPDATE mentors SET approval_status = 'approved' WHERE user_id = :user_id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(':user_id', $mentor_user_id);
            $stmt->execute();
        } else {
            // Create new mentor entry (default values, can be updated later)
            $query = "INSERT INTO mentors (user_id, name, expertise, bio, experience, hourly_rate, approval_status) 
                      VALUES (:user_id, :name, 'General', 'New Mentor', 0, 50.00, 'approved')";
            $stmt = $db->prepare($query);
            $stmt->bindParam(':user_id', $mentor_user_id);
            $stmt->bindParam(':name', $user['name']);
            $stmt->execute();
        }
        
        http_response_code(200);
        echo json_encode([
            "success" => true,
            "message" => "Mentor approved successfully"
        ]);
        
    } elseif ($action === "reject") {
        // Update mentor status to rejected instead of deleting
        $query = "UPDATE mentors SET approval_status = 'rejected' WHERE user_id = :user_id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(':user_id', $mentor_user_id);
        
        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Mentor profile rejected. User can login and resubmit."
            ]);
        } else {
            throw new Exception("Failed to update mentor status");
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Invalid action. Use 'approve' or 'reject'"
        ]);
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Error processing request: " . $e->getMessage()
    ]);
}
?>
