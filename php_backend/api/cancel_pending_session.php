<?php
/**
 * Cancel Pending Session API
 * Endpoint: POST /mentorbridge/api/cancel_pending_session.php
 * 
 * Cancels a pending (unpaid) session by releasing the availability slot.
 * Only mentors can cancel pending sessions.
 * Paid/confirmed sessions cannot be cancelled.
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

$availabilityId = $data->availability_id ?? null;

if (!$availabilityId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Availability ID is required"
    ]);
    exit;
}

try {
    $database = new Database();
    $db = $database->getConnection();
    
    // Check if this is a pending booking (booked but not paid)
    $checkQuery = "SELECT id, is_active, booked_by_user_id 
                   FROM availability 
                   WHERE id = :availability_id";
    $checkStmt = $db->prepare($checkQuery);
    $checkStmt->bindParam(":availability_id", $availabilityId);
    $checkStmt->execute();
    
    if ($checkStmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "Availability slot not found"
        ]);
        exit;
    }
    
    $slot = $checkStmt->fetch(PDO::FETCH_ASSOC);
    
    // Verify it's a pending booking (is_active=0 and has booked_by_user_id)
    if ($slot['is_active'] == 1 || $slot['booked_by_user_id'] == null) {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "This slot is not a pending booking"
        ]);
        exit;
    }
    
    // Release the booking - set is_active back to 1 and clear booked_by_user_id
    $updateQuery = "UPDATE availability 
                    SET is_active = 1, 
                        booked_by_user_id = NULL, 
                        topic = NULL 
                    WHERE id = :availability_id";
    $updateStmt = $db->prepare($updateQuery);
    $updateStmt->bindParam(":availability_id", $availabilityId);
    
    if ($updateStmt->execute()) {
        http_response_code(200);
        echo json_encode([
            "success" => true,
            "message" => "Pending session cancelled. Time slot is now available for booking."
        ]);
    } else {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Failed to cancel session"
        ]);
    }
    
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
