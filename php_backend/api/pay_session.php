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
    
    // Check if session exists and get details
    $checkQuery = "SELECT s.id, s.status, s.mentor_id, s.session_date, s.session_time 
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
    
    $sessionData = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    // In a real app, you would integrate with a payment gateway (Stripe, PayPal, etc.)
    // For now, we'll process the payment and remove availability
    
    // Start transaction
    $db->beginTransaction();
    
    try {
        // Update session status to confirmed
        $updateQuery = "UPDATE sessions 
                        SET status = 'confirmed'
                        WHERE id = :session_id";
        $updateStmt = $db->prepare($updateQuery);
        $updateStmt->bindParam(":session_id", $sessionId);
        $updateStmt->execute();
        
        // Calculate day of week from session date
        $sessionDate = new DateTime($sessionData['session_date']);
        $dayOfWeek = $sessionDate->format('l'); // Monday, Tuesday, etc.
        $sessionTime = substr($sessionData['session_time'], 0, 5); // HH:MM
        
        // Remove the availability slot permanently after payment
        $deleteQuery = "DELETE FROM availability 
                        WHERE mentor_id = :mentor_id 
                        AND day_of_week = :day_of_week 
                        AND TIME_FORMAT(start_time, '%H:%i') = :start_time";
        $deleteStmt = $db->prepare($deleteQuery);
        $deleteStmt->bindParam(":mentor_id", $sessionData['mentor_id']);
        $deleteStmt->bindParam(":day_of_week", $dayOfWeek);
        $deleteStmt->bindParam(":start_time", $sessionTime);
        $deleteStmt->execute();
        
        // Commit transaction
        $db->commit();
        
        http_response_code(200);
        echo json_encode([
            "success" => true,
            "message" => "Payment processed successfully. Availability slot removed.",
            "data" => [
                "session_id" => (int)$sessionId,
                "payment_status" => "paid",
                "status" => "confirmed"
            ]
        ]);
    } catch (Exception $e) {
        // Rollback on error
        $db->rollback();
        
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Failed to process payment: " . $e->getMessage()
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
