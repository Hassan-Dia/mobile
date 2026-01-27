<?php
/**
 * Get Single Mentor API
 * Endpoint: GET /mentorbridge/api/get_mentor.php?id=1
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Validate input
if (isset($_GET['id']) && !empty($_GET['id'])) {
    
    // Create database connection
    $database = new Database();
    $db = $database->getConnection();
    
    // Prepare query
    $query = "SELECT m.*, AVG(r.rating) as rating, COUNT(r.id) as review_count 
              FROM mentors m 
              LEFT JOIN reviews r ON m.id = r.mentor_id 
              WHERE m.id = :id 
              GROUP BY m.id 
              LIMIT 1";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":id", $_GET['id']);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        
        $mentorId = (int)$row['id'];
        
        // Get mentor's reviews
        $reviewQuery = "SELECT r.rating, r.comment, r.created_at, u.name as mentee_name 
                        FROM reviews r
                        INNER JOIN users u ON r.user_id = u.id
                        WHERE r.mentor_id = :mentor_id
                        ORDER BY r.created_at DESC
                        LIMIT 10";
        $reviewStmt = $db->prepare($reviewQuery);
        $reviewStmt->bindParam(":mentor_id", $mentorId);
        $reviewStmt->execute();
        
        $reviews = [];
        while ($reviewRow = $reviewStmt->fetch(PDO::FETCH_ASSOC)) {
            $reviews[] = [
                "rating" => (int)$reviewRow['rating'],
                "comment" => $reviewRow['comment'],
                "created_at" => $reviewRow['created_at'],
                "mentee_name" => $reviewRow['mentee_name']
            ];
        }
        
        // Get booked sessions for this mentor (next 4 weeks only)
        $bookedQuery = "SELECT session_date, session_time 
                        FROM sessions 
                        WHERE mentor_id = :mentor_id 
                        AND status IN ('confirmed', 'pending')
                        AND session_date >= CURDATE()
                        AND session_date <= DATE_ADD(CURDATE(), INTERVAL 4 WEEK)";
        $bookedStmt = $db->prepare($bookedQuery);
        $bookedStmt->bindParam(":mentor_id", $mentorId);
        $bookedStmt->execute();
        
        $bookedSlots = [];
        while ($bookedRow = $bookedStmt->fetch(PDO::FETCH_ASSOC)) {
            // Calculate day of week from date
            $date = new DateTime($bookedRow['session_date']);
            $dayOfWeek = $date->format('l'); // Monday, Tuesday, etc.
            $time = substr($bookedRow['session_time'], 0, 5); // HH:MM
            $slotKey = $dayOfWeek . '|' . $time;
            
            // Add to booked array only if not already there (avoid duplicates from multiple weeks)
            if (!isset($bookedSlots[$slotKey])) {
                $bookedSlots[$slotKey] = 1;
            }
        }
        
        // Get real availability from database
        $availQuery = "SELECT id, day_of_week, start_time, end_time, is_active 
                       FROM availability 
                       WHERE mentor_id = :mentor_id 
                       AND is_active = 1
                       ORDER BY FIELD(day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), 
                                start_time";
        $availStmt = $db->prepare($availQuery);
        $availStmt->bindParam(":mentor_id", $mentorId);
        $availStmt->execute();
        
        $availability = [];
        while ($availRow = $availStmt->fetch(PDO::FETCH_ASSOC)) {
            $startTime = substr($availRow['start_time'], 0, 5); // HH:MM
            $endTime = substr($availRow['end_time'], 0, 5); // HH:MM
            $dayOfWeek = $availRow['day_of_week'];
            $slotKey = $dayOfWeek . '|' . $startTime;
            
            // Only include slots that are NOT booked
            if (!isset($bookedSlots[$slotKey])) {
                $availability[] = [
                    "id" => (int)$availRow['id'],
                    "day_of_week" => $dayOfWeek,
                    "time_slot" => $startTime . '-' . $endTime,
                    "is_available" => 1
                ];
            }
        }
        
        $mentor = [
            "id" => $mentorId,
            "full_name" => $row['name'],
            "email" => "",
            "bio" => $row['bio'],
            "skills" => $row['expertise'],
            "experience" => (int)$row['experience'] . " years",
            "hourly_rate" => (float)$row['hourly_rate'],
            "phone" => "",
            "categories" => $row['expertise'],
            "average_rating" => $row['rating'] ? round((float)$row['rating'], 1) : 0.0,
            "total_reviews" => (int)$row['review_count'],
            "availability" => $availability,
            "reviews" => $reviews
        ];
        
        http_response_code(200);
        echo json_encode([
            "success" => true,
            "data" => $mentor
        ]);
        
    } else {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "Mentor not found"
        ]);
    }
    
} else {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Mentor ID is required"
    ]);
}
?>
