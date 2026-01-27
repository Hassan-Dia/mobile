<?php
/**
 * Session Management API
 * POST - Book new session
 * PUT - Update session
 * DELETE - Cancel session
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

// Handle POST - Book new session
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case field names
    $mentorId = $data->mentor_id ?? $data->mentorId ?? null;
    $userId = $data->mentee_user_id ?? $data->userId ?? $data->user_id ?? null;
    $selectedDay = $data->selected_day ?? $data->day ?? null;
    $selectedTime = $data->selected_time ?? $data->time ?? null;
    $topic = $data->topic ?? "General Session";
    
    if (!empty($mentorId) && !empty($userId) && !empty($selectedDay) && !empty($selectedTime)) {
        
        // Extract time from time slot (e.g., "09:00-10:00" -> "09:00")
        $timeParts = explode('-', $selectedTime);
        $timeStart = $timeParts[0];
        $timeEnd = $timeParts[1] ?? null;
        
        // Validate that this availability exists for the mentor and is active
        $validateQuery = "SELECT id FROM availability 
                          WHERE mentor_id = :mentor_id 
                          AND day_of_week = :day_of_week 
                          AND TIME_FORMAT(start_time, '%H:%i') = :start_time
                          AND is_active = 1";
        $validateStmt = $db->prepare($validateQuery);
        $validateStmt->bindParam(":mentor_id", $mentorId);
        $validateStmt->bindParam(":day_of_week", $selectedDay);
        $validateStmt->bindParam(":start_time", $timeStart);
        $validateStmt->execute();
        
        if ($validateStmt->rowCount() == 0) {
            http_response_code(400);
            echo json_encode([
                "success" => false,
                "message" => "Selected time slot is not available for this mentor"
            ]);
            exit;
        }
        
        // Get availability ID for later update
        $availabilityRow = $validateStmt->fetch(PDO::FETCH_ASSOC);
        $availabilityId = $availabilityRow['id'];
        
        // Calculate next occurrence of the selected day
        $daysOfWeek = ['Sunday' => 0, 'Monday' => 1, 'Tuesday' => 2, 'Wednesday' => 3, 'Thursday' => 4, 'Friday' => 5, 'Saturday' => 6];
        $targetDay = $daysOfWeek[$selectedDay] ?? 1;
        $today = new DateTime();
        $currentDay = (int)$today->format('w'); // 0 = Sunday, 1 = Monday, etc.
        
        // Calculate days until target day
        $daysUntil = ($targetDay - $currentDay + 7) % 7;
        if ($daysUntil == 0) $daysUntil = 7; // If today is the target day, book for next week
        
        $sessionDate = clone $today;
        $sessionDate->modify("+{$daysUntil} days");
        $formattedDate = $sessionDate->format('Y-m-d');
        
        // Format time for database (HH:MM:00)
        $sessionTime = $timeStart . ':00';
        
        // Start transaction to ensure both operations succeed
        $db->beginTransaction();
        
        try {
            // Insert session with 'pending' status (waiting for payment)
            $query = "INSERT INTO sessions (mentor_id, user_id, session_date, session_time, topic, status, created_at) 
                      VALUES (:mentor_id, :user_id, :session_date, :session_time, :topic, 'pending', NOW())";
            
            $stmt = $db->prepare($query);
            $stmt->bindParam(":mentor_id", $mentorId);
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":session_date", $formattedDate);
            $stmt->bindParam(":session_time", $sessionTime);
            $stmt->bindParam(":topic", $topic);
            $stmt->execute();
            
            $session_id = $db->lastInsertId();
            
            // Disable the availability slot (set is_active = 0)
            $disableQuery = "UPDATE availability SET is_active = 0 WHERE id = :availability_id";
            $disableStmt = $db->prepare($disableQuery);
            $disableStmt->bindParam(":availability_id", $availabilityId);
            $disableStmt->execute();
            
            // Commit transaction
            $db->commit();
            
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Session booked successfully. Please complete payment to confirm.",
                "data" => [
                    "session_id" => $session_id,
                    "mentor_id" => $mentorId,
                    "user_id" => $userId,
                    "session_date" => $formattedDate,
                    "session_time" => $sessionTime,
                    "topic" => $topic,
                    "status" => "pending",
                    "availability_id" => $availabilityId
                ]
            ]);
        } catch (Exception $e) {
            // Rollback transaction on error
            $db->rollback();
            
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to book session: " . $e->getMessage()
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Mentor ID, user ID, day, and time are required"
        ]);
    }
}

// Handle DELETE - Cancel session
else if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    
    // Get session ID from URL path
    $path = explode('/', $_SERVER['REQUEST_URI']);
    $session_id = end($path);
    
    if (!empty($session_id) && is_numeric($session_id)) {
        
        // Get session details before cancelling
        $getQuery = "SELECT mentor_id, session_date, session_time, status 
                     FROM sessions WHERE id = :id";
        $getStmt = $db->prepare($getQuery);
        $getStmt->bindParam(":id", $session_id);
        $getStmt->execute();
        
        if ($getStmt->rowCount() === 0) {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "Session not found"
            ]);
            exit;
        }
        
        $sessionData = $getStmt->fetch(PDO::FETCH_ASSOC);
        
        // Start transaction
        $db->beginTransaction();
        
        try {
            // Cancel the session
            $query = "UPDATE sessions SET status = 'cancelled' WHERE id = :id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":id", $session_id);
            $stmt->execute();
            
            // If session was pending (not paid yet), re-enable the availability slot
            if ($sessionData['status'] === 'pending') {
                $sessionDate = new DateTime($sessionData['session_date']);
                $dayOfWeek = $sessionDate->format('l');
                $sessionTime = substr($sessionData['session_time'], 0, 5);
                
                // Re-enable availability (set is_active = 1)
                $enableQuery = "UPDATE availability 
                                SET is_active = 1 
                                WHERE mentor_id = :mentor_id 
                                AND day_of_week = :day_of_week 
                                AND TIME_FORMAT(start_time, '%H:%i') = :start_time";
                $enableStmt = $db->prepare($enableQuery);
                $enableStmt->bindParam(":mentor_id", $sessionData['mentor_id']);
                $enableStmt->bindParam(":day_of_week", $dayOfWeek);
                $enableStmt->bindParam(":start_time", $sessionTime);
                $enableStmt->execute();
            }
            // If confirmed (already paid), slot was already deleted, so nothing to restore
            
            $db->commit();
            
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Session cancelled" . ($sessionData['status'] === 'pending' ? " and availability slot restored" : "")
            ]);
        } catch (Exception $e) {
            $db->rollback();
            
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to cancel session: " . $e->getMessage()
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Valid session ID required"
        ]);
    }
}

// Handle PUT - Update session
else if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Get session ID from URL
    $path = explode('/', $_SERVER['REQUEST_URI']);
    $session_id = end($path);
    
    if (!empty($session_id) && !empty($data->status)) {
        
        $query = "UPDATE sessions SET status = :status WHERE id = :id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":status", $data->status);
        $stmt->bindParam(":id", $session_id);
        
        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Session updated",
                "id" => $session_id,
                "status" => $data->status
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to update session"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Session ID and status are required"
        ]);
    }
}
?>
