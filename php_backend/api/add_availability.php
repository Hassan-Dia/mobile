<?php
/**
 * Add Availability Slot API
 * POST - Add new availability slot for a mentor
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case field names
    $mentorId = $data->mentor_id ?? $data->mentorId ?? null;
    $sessionDate = $data->session_date ?? $data->sessionDate ?? null;
    $sessionTime = $data->session_time ?? $data->sessionTime ?? null;
    $duration = $data->duration ?? 60;
    $topic = $data->topic ?? "General Session";
    
    if (!empty($mentorId) && !empty($sessionDate) && !empty($sessionTime)) {
        
        // Validate date is in the future
        if (strtotime($sessionDate) < strtotime('today')) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Session date must be in the future"
            ]);
            exit;
        }
        
        // Check for duplicate slot
        $checkQuery = "SELECT id FROM availability 
                       WHERE mentor_id = :mentor_id 
                       AND session_date = :session_date 
                       AND session_time = :session_time";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":mentor_id", $mentorId);
        $checkStmt->bindParam(":session_date", $sessionDate);
        $checkStmt->bindParam(":session_time", $sessionTime);
        $checkStmt->execute();
        
        if ($checkStmt->rowCount() > 0) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Availability slot already exists for this date and time"
            ]);
            exit;
        }
        
        // Insert new availability slot
        $query = "INSERT INTO availability (mentor_id, session_date, session_time, duration, topic, is_active, created_at) 
                  VALUES (:mentor_id, :session_date, :session_time, :duration, :topic, 1, NOW())";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(":mentor_id", $mentorId);
        $stmt->bindParam(":session_date", $sessionDate);
        $stmt->bindParam(":session_time", $sessionTime);
        $stmt->bindParam(":duration", $duration);
        $stmt->bindParam(":topic", $topic);
        
        if ($stmt->execute()) {
            $availabilityId = $db->lastInsertId();
            
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Availability slot added successfully",
                "data" => [
                    "id" => $availabilityId,
                    "mentor_id" => $mentorId,
                    "session_date" => $sessionDate,
                    "session_time" => $sessionTime,
                    "duration" => $duration,
                    "topic" => $topic,
                    "is_active" => 1
                ]
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to add availability slot"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields"
        ]);
    }
    
} else {
    http_response_code(405);
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed"
    ]);
}
?>
