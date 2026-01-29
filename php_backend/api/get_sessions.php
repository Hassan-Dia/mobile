<?php
/**
 * Get User Sessions API
 * Endpoint: GET /mentorbridge/api/get_sessions.php?user_id=1
 * Optional: &status=upcoming, &role=mentee|mentor
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Validate input - accept both user_id and userId
$userId = isset($_GET['user_id']) ? $_GET['user_id'] : (isset($_GET['userId']) ? $_GET['userId'] : null);
$role = isset($_GET['role']) ? $_GET['role'] : 'mentee';

if ($userId) {
    
    $database = new Database();
    $db = $database->getConnection();
    
    $sessions = [];
    
    // Get pending bookings from availability table
    if ($role === 'mentee') {
        // For mentees: Get slots they booked but haven't paid yet
        $pendingQuery = "SELECT a.id, a.session_date, a.session_time, a.duration, a.topic, 
                         m.name as mentor_name, m.hourly_rate, m.id as mentor_id
                         FROM availability a
                         INNER JOIN mentors m ON a.mentor_id = m.id
                         WHERE a.booked_by_user_id = :user_id 
                         AND a.is_active = 0
                         ORDER BY a.session_date DESC, a.session_time DESC";
        
        $pendingStmt = $db->prepare($pendingQuery);
        $pendingStmt->bindParam(":user_id", $userId);
        $pendingStmt->execute();
        
        while ($row = $pendingStmt->fetch(PDO::FETCH_ASSOC)) {
            $session = [
                "id" => (int)$row['id'],
                "availability_id" => (int)$row['id'],
                "mentor_id" => (int)$row['mentor_id'],
                "mentor_name" => $row['mentor_name'],
                "mentee_name" => "You",
                "scheduled_at" => $row['session_date'] . ' ' . $row['session_time'],
                "duration" => (int)$row['duration'],
                "status" => "pending",
                "payment_status" => "pending",
                "amount" => (float)$row['hourly_rate'],
                "has_feedback" => false,
                "source" => "availability"
            ];
            array_push($sessions, $session);
        }
    } else if ($role === 'mentor') {
        // For mentors: Get slots that are booked but awaiting payment
        $pendingQuery = "SELECT a.id, a.session_date, a.session_time, a.duration, a.topic, 
                         a.booked_by_user_id, u.name as mentee_name, m.hourly_rate, m.id as mentor_id
                         FROM availability a
                         INNER JOIN mentors m ON a.mentor_id = m.id
                         LEFT JOIN users u ON a.booked_by_user_id = u.id
                         WHERE m.user_id = :user_id 
                         AND a.is_active = 0
                         AND a.booked_by_user_id IS NOT NULL
                         ORDER BY a.session_date DESC, a.session_time DESC";
        
        $pendingStmt = $db->prepare($pendingQuery);
        $pendingStmt->bindParam(":user_id", $userId);
        $pendingStmt->execute();
        
        while ($row = $pendingStmt->fetch(PDO::FETCH_ASSOC)) {
            $session = [
                "id" => (int)$row['id'],
                "availability_id" => (int)$row['id'],
                "mentor_id" => (int)$row['mentor_id'],
                "mentor_name" => "You",
                "mentee_name" => $row['mentee_name'] ?? "Unknown",
                "scheduled_at" => $row['session_date'] . ' ' . $row['session_time'],
                "duration" => (int)$row['duration'],
                "status" => "pending",
                "payment_status" => "pending",
                "amount" => (float)$row['hourly_rate'],
                "has_feedback" => false,
                "source" => "availability"
            ];
            array_push($sessions, $session);
        }
    }
    
    // Get confirmed sessions from sessions table
    $query = "SELECT s.*, m.name as mentor_name, m.expertise as mentor_expertise, 
              m.image_url as mentor_image, m.hourly_rate, u.name as mentee_name
              FROM sessions s
              INNER JOIN mentors m ON s.mentor_id = m.id
              LEFT JOIN users u ON s.user_id = u.id";
    
    if ($role === 'mentee') {
        $query .= " WHERE s.user_id = :user_id";
    } else {
        // For mentors, get sessions where they are the mentor
        $query .= " WHERE s.mentor_id IN (SELECT id FROM mentors WHERE user_id = :user_id)";
    }
    
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
    
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Check if review exists
        $reviewQuery = "SELECT id FROM reviews WHERE mentor_id = :mentor_id AND user_id = :user_id";
        $reviewStmt = $db->prepare($reviewQuery);
        $reviewStmt->bindParam(":mentor_id", $row['mentor_id']);
        $reviewStmt->bindParam(":user_id", $userId);
        $reviewStmt->execute();
        $hasFeedback = $reviewStmt->rowCount() > 0;
        
        $session = [
            "id" => (int)$row['id'],
            "mentor_id" => (int)$row['mentor_id'],
            "mentor_name" => $row['mentor_name'],
            "mentee_name" => $row['mentee_name'] ?? "User",
            "scheduled_at" => $row['session_date'] . ' ' . $row['session_time'],
            "duration" => 60,
            "status" => $row['status'],
            "payment_status" => "paid",
            "amount" => (float)$row['hourly_rate'],
            "has_feedback" => $hasFeedback,
            "source" => "sessions"
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
