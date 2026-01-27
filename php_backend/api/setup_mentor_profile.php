<?php
/**
 * Setup Mentor Profile
 * Endpoint: POST /mentorbridge/api/setup_mentor_profile.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Validate input
if (!isset($data->user_id) || !isset($data->bio) || !isset($data->skills) || 
    !isset($data->experience) || !isset($data->hourly_rate) || !isset($data->categories)) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "All fields are required"
    ]);
    exit;
}

try {
    // Create database connection
    $database = new Database();
    $db = $database->getConnection();
    
    $user_id = (int)$data->user_id;
    $bio = $data->bio;
    $skills = $data->skills;
    $experience = (int)$data->experience;
    $hourly_rate = (float)$data->hourly_rate;
    $categories = $data->categories;
    
    // Check if mentor record already exists
    $query = "SELECT id FROM mentors WHERE user_id = :user_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(':user_id', $user_id);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        // Update existing mentor profile
        $query = "UPDATE mentors 
                  SET bio = :bio, 
                      expertise = :categories, 
                      experience = :experience, 
                      hourly_rate = :hourly_rate,
                      approval_status = 'pending'
                  WHERE user_id = :user_id";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(':bio', $bio);
        $stmt->bindParam(':categories', $categories);
        $stmt->bindParam(':experience', $experience);
        $stmt->bindParam(':hourly_rate', $hourly_rate);
        $stmt->bindParam(':user_id', $user_id);
        $stmt->execute();
        
    } else {
        // Get user name for new mentor entry
        $query = "SELECT name FROM users WHERE id = :user_id";
        $stmt = $db->prepare($query);
        $stmt->bindParam(':user_id', $user_id);
        $stmt->execute();
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        if (!$user) {
            http_response_code(404);
            echo json_encode([
                "success" => false,
                "message" => "User not found"
            ]);
            exit;
        }
        
        // Create new mentor profile
        $query = "INSERT INTO mentors (user_id, name, expertise, bio, experience, hourly_rate, approval_status) 
                  VALUES (:user_id, :name, :categories, :bio, :experience, :hourly_rate, 'pending')";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(':user_id', $user_id);
        $stmt->bindParam(':name', $user['name']);
        $stmt->bindParam(':categories', $categories);
        $stmt->bindParam(':bio', $bio);
        $stmt->bindParam(':experience', $experience);
        $stmt->bindParam(':hourly_rate', $hourly_rate);
        $stmt->execute();
    }
    
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "message" => "Profile submitted for approval",
        "data" => [
            "user_id" => $user_id,
            "approval_status" => "pending"
        ]
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Error submitting profile: " . $e->getMessage()
    ]);
}
?>
