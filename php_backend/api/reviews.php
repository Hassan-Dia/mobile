<?php
/**
 * Submit Review API
 * Endpoint: POST /mentorbridge/api/reviews.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case
    $mentorId = $data->mentor_id ?? $data->mentorId ?? null;
    $userId = $data->user_id ?? $data->userId ?? null;
    $rating = $data->rating ?? null;
    $comment = $data->comment ?? $data->review ?? "";
    
    if (!empty($mentorId) && !empty($rating) && !empty($userId)) {
        
        $database = new Database();
        $db = $database->getConnection();
        
        // Check if user already reviewed this mentor
        $checkQuery = "SELECT id FROM reviews WHERE mentor_id = :mentor_id AND user_id = :user_id";
        $checkStmt = $db->prepare($checkQuery);
        $checkStmt->bindParam(":mentor_id", $mentorId);
        $checkStmt->bindParam(":user_id", $userId);
        $checkStmt->execute();
        
        if ($checkStmt->rowCount() > 0) {
            // Update existing review
            $query = "UPDATE reviews SET rating = :rating, comment = :comment WHERE mentor_id = :mentor_id AND user_id = :user_id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":mentor_id", $mentorId);
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":rating", $rating);
            $stmt->bindParam(":comment", $comment);
            
            if ($stmt->execute()) {
                http_response_code(200);
                echo json_encode([
                    "success" => true,
                    "message" => "Review updated successfully",
                    "mentor_id" => $mentorId,
                    "rating" => $rating,
                    "comment" => $comment
                ]);
            } else {
                http_response_code(500);
                echo json_encode([
                    "success" => false,
                    "message" => "Failed to update review"
                ]);
            }
        } else {
            // Insert new review
            $query = "INSERT INTO reviews (mentor_id, user_id, rating, comment, created_at) 
                      VALUES (:mentor_id, :user_id, :rating, :comment, NOW())";
            
            $stmt = $db->prepare($query);
            $stmt->bindParam(":mentor_id", $mentorId);
            $stmt->bindParam(":user_id", $userId);
            $stmt->bindParam(":rating", $rating);
            $stmt->bindParam(":comment", $comment);
            
            if ($stmt->execute()) {
                $review_id = $db->lastInsertId();
                
                http_response_code(201);
                echo json_encode([
                    "success" => true,
                    "message" => "Review submitted successfully",
                    "id" => $review_id,
                    "mentor_id" => $mentorId,
                    "rating" => $rating,
                    "comment" => $comment
                ]);
            } else {
                http_response_code(500);
                echo json_encode([
                    "success" => false,
                    "message" => "Failed to submit review"
                ]);
            }
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Mentor ID, user ID and rating are required"
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
