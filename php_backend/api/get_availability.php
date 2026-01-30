<?php
/**
 * Get Mentor Availability API
 * Endpoint: GET /mentorbridge/api/get_availability.php?user_id=X
 * Returns all availability slots for a mentor (by user_id)
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

// Get mentor_id from user_id
$mentorQuery = "SELECT id FROM mentors WHERE user_id = :user_id LIMIT 1";
$mentorStmt = $db->prepare($mentorQuery);
$mentorStmt->bindParam(":user_id", $userId);
$mentorStmt->execute();

if ($mentorStmt->rowCount() === 0) {
    http_response_code(404);
    echo json_encode([
        "success" => false,
        "message" => "Mentor not found for this user"
    ]);
    exit;
}

$mentorRow = $mentorStmt->fetch(PDO::FETCH_ASSOC);
$mentorId = $mentorRow['id'];

// Get all availability slots for this mentor (only active/available slots)
$query = "SELECT id, mentor_id, session_date, session_time, duration, topic, is_active, booked_by_user_id, created_at
          FROM availability 
          WHERE mentor_id = :mentor_id
          AND is_active = 1
          ORDER BY session_date ASC, session_time ASC";

$stmt = $db->prepare($query);
$stmt->bindParam(":mentor_id", $mentorId);
$stmt->execute();

$availability = [];

while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    // Calculate day of week from date
    $date = new DateTime($row['session_date']);
    $dayOfWeek = $date->format('l'); // Monday, Tuesday, etc.
    $timeStart = substr($row['session_time'], 0, 5); // HH:MM
    $timeEnd = date('H:i', strtotime($row['session_time']) + ($row['duration'] * 60));
    
    // Determine slot status
    $isAvailable = ($row['is_active'] == 1);
    
    $slot = [
        "id" => (int)$row['id'],
        "session_date" => $row['session_date'],
        "day_of_week" => $dayOfWeek,
        "time_slot" => $timeStart . '-' . $timeEnd,
        "session_time" => $timeStart,
        "duration" => (int)$row['duration'],
        "topic" => $row['topic'],
        "is_available" => $isAvailable,
        "is_active" => (int)$row['is_active'],
        "booked_by_user_id" => $row['booked_by_user_id'] ? (int)$row['booked_by_user_id'] : null,
        "created_at" => $row['created_at']
    ];
    
    array_push($availability, $slot);
}

http_response_code(200);
echo json_encode([
    "success" => true,
    "data" => $availability
]);
?>
