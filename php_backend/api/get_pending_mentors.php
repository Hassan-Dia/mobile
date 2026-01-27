<?php
/**
 * Get Pending Mentors for Admin Approval
 * Endpoint: GET /mentorbridge/api/get_pending_mentors.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Create database connection
$database = new Database();
$db = $database->getConnection();

try {
    // Get all mentors with pending approval status
    $query = "SELECT m.id, m.user_id, m.name as full_name, u.email, m.expertise as category, 
                     m.experience as experience_years, m.hourly_rate, m.approval_status
              FROM mentors m
              INNER JOIN users u ON m.user_id = u.id
              WHERE m.approval_status = 'pending'
              ORDER BY m.created_at DESC";
    
    $stmt = $db->prepare($query);
    $stmt->execute();
    
    $mentors = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $mentors[] = [
            "id" => (int)$row['user_id'], // Use user_id for consistency with app
            "full_name" => $row['full_name'],
            "email" => $row['email'],
            "category" => $row['category'],
            "experience_years" => (int)$row['experience_years'],
            "hourly_rate" => (float)$row['hourly_rate']
        ];
    }
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "data" => $mentors
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Error fetching pending mentors: " . $e->getMessage()
    ]);
}
?>
