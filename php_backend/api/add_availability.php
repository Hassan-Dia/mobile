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
    $dayOfWeek = $data->day_of_week ?? $data->dayOfWeek ?? null;
    $startTime = $data->start_time ?? $data->startTime ?? null;
    $endTime = $data->end_time ?? $data->endTime ?? null;
    
    if (!empty($mentorId) && !empty($dayOfWeek) && !empty($startTime) && !empty($endTime)) {
        
        // Validate day of week
        $validDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
        if (!in_array($dayOfWeek, $validDays)) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Invalid day of week"
            ]);
            exit;
        }
        
        // Check for duplicate slot
        $checkQuery = "SELECT id FROM availability 
                       WHERE mentor_id = :mentor_id 
                       AND day_of_week = :day_of_week 
                       AND start_time = :start_time";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":mentor_id", $mentorId);
        $checkStmt->bindParam(":day_of_week", $dayOfWeek);
        $checkStmt->bindParam(":start_time", $startTime);
        $checkStmt->execute();
        
        if ($checkStmt->rowCount() > 0) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Availability slot already exists"
            ]);
            exit;
        }
        
        // Insert new availability slot
        $query = "INSERT INTO availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at) 
                  VALUES (:mentor_id, :day_of_week, :start_time, :end_time, 1, NOW())";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(":mentor_id", $mentorId);
        $stmt->bindParam(":day_of_week", $dayOfWeek);
        $stmt->bindParam(":start_time", $startTime);
        $stmt->bindParam(":end_time", $endTime);
        
        if ($stmt->execute()) {
            $availabilityId = $db->lastInsertId();
            
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Availability slot added successfully",
                "data" => [
                    "id" => $availabilityId,
                    "mentor_id" => $mentorId,
                    "day_of_week" => $dayOfWeek,
                    "start_time" => $startTime,
                    "end_time" => $endTime,
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
