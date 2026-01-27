<?php
/**
 * Get User Sessions API
 * Endpoint: GET /mentorbridge/api/get_sessions.php?user_id=1
 * Optional: &status=upcoming
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Validate input - accept both user_id and userId
$userId = isset($_GET['user_id']) ? $_GET['user_id'] : (isset($_GET['userId']) ? $_GET['userId'] : null);

if ($userId) {
    
    $database = new Database();
    $db = $database->getConnection();
    
    // Build query
    $query = "SELECT s.*, m.name as mentor_name, m.expertise as mentor_expertise, m.image_url as mentor_image
              FROM sessions s
              INNER JOIN mentors m ON s.mentor_id = m.id
              WHERE s.user_id = :user_id";
    
    // Filter by status if provided
    if (isset($_GET['status']) && !empty($_GET['status'])) {
        if ($_GET['status'] === 'upcoming') {
            $query .= " AND s.session_date >= CURDATE() AND s.status != 'cancelled'";
        } else {
            $query .= " AND s.status = :status";
        }
    }
    
    $query .= " ORDER BY s.session_date DESC, s.session_time DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_id", $userId);
    
    if (isset($_GET['status']) && !empty($_GET['status']) && $_GET['status'] !== 'upcoming') {
        $stmt->bindParam(":status", $_GET['status']);
    }
    
    $stmt->execute();
    
    $sessions = [];
    
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $session = [
            "id" => (int)$row['id'],
            "mentor_name" => $row['mentor_name'],
            "mentee_name" => "User",
            "scheduled_at" => $row['session_date'] . ' ' . $row['session_time'],
            "duration" => 60,
            "status" => $row['status'],
            "payment_status" => "pending",
            "amount" => 50.0,
            "has_feedback" => false
        ];
        array_push($sessions, $session);
    }
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "data" => $sessions
    ]);
    
} else {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "User ID is required"
    ]);
}
?>
