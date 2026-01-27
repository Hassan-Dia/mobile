<?php
/**
 * Get Mentor Reviews API
 * Endpoint: GET /mentorbridge/api/get_reviews.php?mentorId=1
 */

include_once '../config/cors.php';
include_once '../config/database.php';

if (isset($_GET['mentorId']) && !empty($_GET['mentorId'])) {
    
    $database = new Database();
    $db = $database->getConnection();
    
    $query = "SELECT r.*, u.name as user_name 
              FROM reviews r
              INNER JOIN users u ON r.user_id = u.id
              WHERE r.mentor_id = :mentor_id
              ORDER BY r.created_at DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":mentor_id", $_GET['mentorId']);
    $stmt->execute();
    
    $reviews = [];
    
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $review = [
            "rating" => (int)$row['rating'],
            "comment" => $row['comment'],
            "created_at" => $row['created_at'],
            "mentee_name" => $row['user_name']
        ];
        array_push($reviews, $review);
    }
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "data" => $reviews
    ]);
    
} else {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Mentor ID is required"
    ]);
}
?>
