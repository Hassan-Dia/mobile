<?php
/**
 * Get Mentor by User ID API
 * Endpoint: GET /mentorbridge/api/get_mentor_by_user.php?user_id=X
 * Returns mentor record for a given user_id
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;

if (!$userId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "User ID is required"
    ]);
    exit;
}

$database = new Database();
$db = $database->getConnection();

$query = "SELECT id, user_id, name, expertise, bio, experience, hourly_rate, approval_status 
          FROM mentors 
          WHERE user_id = :user_id 
          LIMIT 1";

$stmt = $db->prepare($query);
$stmt->bindParam(":user_id", $userId);
$stmt->execute();

if ($stmt->rowCount() > 0) {
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "data" => [
            "id" => (int)$row['id'],
            "user_id" => (int)$row['user_id'],
            "name" => $row['name'],
            "expertise" => $row['expertise'],
            "bio" => $row['bio'],
            "experience" => (int)$row['experience'],
            "hourly_rate" => (float)$row['hourly_rate'],
            "approval_status" => $row['approval_status']
        ]
    ]);
} else {
    http_response_code(404);
    echo json_encode([
        "success" => false,
        "message" => "Mentor not found for this user"
    ]);
}
?>
