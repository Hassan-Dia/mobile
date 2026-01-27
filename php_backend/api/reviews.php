<?php
/**
 * Submit Review API
 * Endpoint: POST /mentorbridge/api/reviews.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    if (!empty($data->mentorId) && !empty($data->rating)) {
        
        // TODO: Get user_id from auth token
        $user_id = $data->userId ?? 1; // Default for testing
        
        $database = new Database();
        $db = $database->getConnection();
        
        $query = "INSERT INTO reviews (mentor_id, user_id, rating, comment, created_at) 
                  VALUES (:mentor_id, :user_id, :rating, :comment, NOW())";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(":mentor_id", $data->mentorId);
        $stmt->bindParam(":user_id", $user_id);
        $stmt->bindParam(":rating", $data->rating);
        $stmt->bindParam(":comment", $data->comment);
        
        if ($stmt->execute()) {
            $review_id = $db->lastInsertId();
            
            http_response_code(201);
            echo json_encode([
                "success" => true,
                "message" => "Review submitted successfully",
                "id" => $review_id,
                "mentorId" => $data->mentorId,
                "rating" => $data->rating,
                "comment" => $data->comment
            ]);
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to submit review"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Mentor ID and rating are required"
        ]);
    }
}
?>
