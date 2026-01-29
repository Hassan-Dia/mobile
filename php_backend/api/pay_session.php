<?php
/**
 * Payment Processing API
 * Endpoint: POST /mentorbridge/api/pay_session.php
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
    
    // Check if this is payment for an availability slot
    $availabilityId = $data->availability_id ?? $data->availabilityId ?? null;
    
    if ($availabilityId) {
        // NEW FLOW: Moving from availability to sessions table
        
        // Get availability details
        $availQuery = "SELECT mentor_id, booked_by_user_id, session_date, session_time, duration, topic, is_active 
                       FROM availability 
                       WHERE id = :availability_id";
        $availStmt = $db->prepare($availQuery);
        $availStmt->bindParam(":availability_id", $availabilityId);
        $availStmt->execute();
        
        if ($availStmt->rowCount() === 0) {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "Availability slot not found"
            ]);
            exit;
        }
        
        $availData = $availStmt->fetch(PDO::FETCH_ASSOC);
        
        if ($availData['is_active'] == 1) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Availability slot was not booked"
            ]);
            exit;
        }
        
        // Start transaction
        $db->beginTransaction();
        
        try {
            // Insert into sessions table
            $insertQuery = "INSERT INTO sessions (mentor_id, user_id, session_date, session_time, topic, status, created_at) 
                            VALUES (:mentor_id, :user_id, :session_date, :session_time, :topic, 'confirmed', NOW())";
            $insertStmt = $db->prepare($insertQuery);
            $insertStmt->bindParam(":mentor_id", $availData['mentor_id']);
            $insertStmt->bindParam(":user_id", $availData['booked_by_user_id']);
            $insertStmt->bindParam(":session_date", $availData['session_date']);
            $insertStmt->bindParam(":session_time", $availData['session_time']);
            $insertStmt->bindParam(":topic", $availData['topic']);
            $insertStmt->execute();
            
            $sessionId = $db->lastInsertId();
            
            // Delete from availability table
            $deleteQuery = "DELETE FROM availability WHERE id = :availability_id";
            $deleteStmt = $db->prepare($deleteQuery);
            $deleteStmt->bindParam(":availability_id", $availabilityId);
            $deleteStmt->execute();
            
            // Commit transaction
            $db->commit();
            
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Payment processed. Session confirmed and moved to sessions table.",
                "data" => [
                    "session_id" => (int)$sessionId,
                    "payment_status" => "paid",
                    "status" => "confirmed"
                ]
            ]);
        } catch (Exception $e) {
            $db->rollback();
            
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to process payment: " . $e->getMessage()
            ]);
        }
        
    } else {
        // OLD FLOW: Direct session payment (for backward compatibility)
        // This handles sessions already in sessions table
        
        if (!$sessionId) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Session ID or Availability ID is required"
            ]);
            exit;
        }
        
        // Check if session exists and get details
        $checkQuery = "SELECT s.id, s.status 
                       FROM sessions s 
                       WHERE s.id = :session_id";
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
        
        // Update session to confirmed
        $updateQuery = "UPDATE sessions SET status = 'confirmed' WHERE id = :session_id";
        $updateStmt = $db->prepare($updateQuery);
        $updateStmt->bindParam(":session_id", $sessionId);
        
        if ($updateStmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Payment processed successfully",
                "data" => [
                    "session_id" => (int)$sessionId,
                    "payment_status" => "paid",
                    "status" => "confirmed"
                ]
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to process payment"
            ]);
        }
    }
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>
