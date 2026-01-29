<?php
/**
 * Session Management API
 * POST - Book new session
 * PUT - Update session
 * DELETE - Cancel session
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

// Handle POST - Book new session
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case field names
    $availabilityId = $data->availability_id ?? $data->availabilityId ?? null;
    $userId = $data->user_id ?? $data->userId ?? null;
    
    if (!empty($availabilityId) && !empty($userId)) {
        
        // Verify availability exists and is active
        $checkQuery = "SELECT id, mentor_id, session_date, session_time, duration, topic, is_active 
                       FROM availability 
                       WHERE id = :availability_id AND is_active = 1";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":availability_id", $availabilityId);
        $checkStmt->execute();
        
        if ($checkStmt->rowCount() == 0) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Availability slot not found or already booked"
            ]);
            exit;
        }
        
        $availData = $checkStmt->fetch(PDO::FETCH_ASSOC);
        
        // Book the slot by setting is_active = 0 and booked_by_user_id
        $bookQuery = "UPDATE availability 
                      SET is_active = 0, booked_by_user_id = :user_id 
                      WHERE id = :availability_id";
        $bookStmt = $db->prepare($bookQuery);
        $bookStmt->bindParam(":availability_id", $availabilityId);
        $bookStmt->bindParam(":user_id", $userId);
        
        if ($bookStmt->execute()) {
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Session booked successfully. Please complete payment to confirm.",
                "data" => [
                    "availability_id" => (int)$availabilityId,
                    "mentor_id" => (int)$availData['mentor_id'],
                    "user_id" => (int)$userId,
                    "session_date" => $availData['session_date'],
                    "session_time" => $availData['session_time'],
                    "duration" => (int)$availData['duration'],
                    "topic" => $availData['topic'],
                    "status" => "pending"
                ]
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to book session"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Availability ID and user ID are required"
        ]);
    }
}

// Handle DELETE - Cancel session
else if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    
    // Get session ID from URL path
    $path = explode('/', $_SERVER['REQUEST_URI']);
    $session_id = end($path);
    
    if (!empty($session_id) && is_numeric($session_id)) {
        
        // Cancel the session
        $query = "UPDATE sessions SET status = 'cancelled' WHERE id = :id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":id", $session_id);
        
        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Session cancelled"
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to cancel session"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Valid session ID required"
        ]);
    }
}

// Handle PUT - Update session
else if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Get session ID from URL
    $path = explode('/', $_SERVER['REQUEST_URI']);
    $session_id = end($path);
    
    if (!empty($session_id) && !empty($data->status)) {
        
        $query = "UPDATE sessions SET status = :status WHERE id = :id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":status", $data->status);
        $stmt->bindParam(":id", $session_id);
        
        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Session updated",
                "id" => $session_id,
                "status" => $data->status
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to update session"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Session ID and status are required"
        ]);
    }
}
?>
