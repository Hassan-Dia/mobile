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
        
        // Get available session slots from availability table (specific dates, not recurring)
        $availQuery = "SELECT id, session_date, session_time, duration, topic, is_active 
                       FROM availability 
                       WHERE mentor_id = :mentor_id 
                       AND is_active = 1
                       AND session_date >= CURDATE()
                       ORDER BY session_date, session_time";
        $availStmt = $db->prepare($availQuery);
        $availStmt->bindParam(":mentor_id", $mentorId);
        $availStmt->execute();
        
        $availability = [];
        while ($availRow = $availStmt->fetch(PDO::FETCH_ASSOC)) {
            // Calculate day of week from date for display
            $date = new DateTime($availRow['session_date']);
            $dayOfWeek = $date->format('l'); // Monday, Tuesday, etc.
            $timeStart = substr($availRow['session_time'], 0, 5); // HH:MM
            $timeEnd = date('H:i', strtotime($availRow['session_time']) + ($availRow['duration'] * 60));
            
            $availability[] = [
                "id" => (int)$availRow['id'],
                "session_date" => $availRow['session_date'],
                "session_time" => $timeStart,
                "day_of_week" => $dayOfWeek,
                "time_slot" => $timeStart . '-' . $timeEnd,
                "duration" => (int)$availRow['duration'],
                "topic" => $availRow['topic'],
                "is_available" => 1
            ];
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
