<?php
/**
 * User Registration API
 * Endpoint: POST /mentorbridge/api/register.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Support both 'name' and 'full_name' fields
$name = isset($data->full_name) ? $data->full_name : (isset($data->name) ? $data->name : null);

// Validate input
if (!empty($name) && !empty($data->email) && !empty($data->password)) {
    
    // Create database connection
    $database = new Database();
    $db = $database->getConnection();
    
    // Check if email already exists
    $query = "SELECT id FROM users WHERE email = :email LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        http_response_code(409);
        echo json_encode([
            "success" => false,
            "message" => "Email already exists"
        ]);
        exit();
    }
    
    // Hash password
    $hashed_password = password_hash($data->password, PASSWORD_DEFAULT);
    
    // Insert new user
    $query = "INSERT INTO users (name, email, password, created_at) VALUES (:name, :email, :password, NOW())";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":name", $name);
    $stmt->bindParam(":email", $data->email);
    $stmt->bindParam(":password", $hashed_password);
    
    if ($stmt->execute()) {
        $user_id = $db->lastInsertId();
        
        // Generate token
        $token = bin2hex(random_bytes(32));
        
        // Store token
        $query = "INSERT INTO user_tokens (user_id, token, created_at) VALUES (:user_id, :token, NOW())";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_id", $user_id);
        $stmt->bindParam(":token", $token);
        $stmt->execute();
        
        // If registering as mentor, create pending mentor entry
        if (isset($data->role) && $data->role === 'mentor') {
            $query = "INSERT INTO mentors (user_id, name, expertise, bio, experience, hourly_rate, approval_status) 
                      VALUES (:user_id, :name, 'General', 'New Mentor', 0, 50.00, 'pending')";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":user_id", $user_id);
            $stmt->bindParam(":name", $name);
            $stmt->execute();
        } else {
            // If registering as mentee (default), create mentee entry
            $query = "INSERT INTO mentees (user_id, full_name, email, created_at) 
                      VALUES (:user_id, :name, :email, NOW())";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":user_id", $user_id);
            $stmt->bindParam(":name", $name);
            $stmt->bindParam(":email", $data->email);
            $stmt->execute();
        }
        
        // Prepare response data
        $response_data = [
            "user_id" => (int)$user_id,
            "email" => $data->email,
            "role" => isset($data->role) ? $data->role : "mentee"
        ];
        
        // Add profile data
        $profile = [
            "id" => (int)$user_id,
            "full_name" => $name,
            "is_profile_complete" => false
        ];
        
        // If mentor, add approval status
        if (isset($data->role) && $data->role === 'mentor') {
            $profile["approval_status"] = "pending";
        }
        
        $response_data["profile"] = $profile;
        
        http_response_code(201);
        echo json_encode([
            "success" => true,
            "message" => "Registration successful",
            "token" => $token,
            "data" => $response_data
        ]);
        
    } else {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Registration failed"
        ]);
    }
    
} else {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Name, email, and password are required"
    ]);
}
?>
