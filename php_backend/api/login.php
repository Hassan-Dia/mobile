<?php
/**
 * User Login API
 * Endpoint: POST /mentorbridge/api/login.php
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Validate input
if (!empty($data->email) && !empty($data->password)) {
    
    // Create database connection
    $database = new Database();
    $db = $database->getConnection();
    
    // Prepare query
    $query = "SELECT id, name, email, password FROM users WHERE email = :email LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();
    
    $num = $stmt->rowCount();
    
    if ($num > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Verify password
        if (password_verify($data->password, $row['password'])) {
            
            // Generate simple token (in production, use JWT)
            $token = bin2hex(random_bytes(32));
            
            // Store token in database
            $query = "INSERT INTO user_tokens (user_id, token, created_at) VALUES (:user_id, :token, NOW())";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":user_id", $row['id']);
            $stmt->bindParam(":token", $token);
            $stmt->execute();
            
            // Determine user role
            $role = "mentee"; // Default role
            $approval_status = null;
            $is_profile_complete = false;
            
            // Check if admin (ID = 1)
            if ($row['id'] == 1) {
                $role = "admin";
            } else {
                // Check if user is a mentor
                $query = "SELECT approval_status FROM mentors WHERE user_id = :user_id LIMIT 1";
                $stmt = $db->prepare($query);
                $stmt->bindParam(":user_id", $row['id']);
                $stmt->execute();
                
                if ($stmt->rowCount() > 0) {
                    $mentor_row = $stmt->fetch(PDO::FETCH_ASSOC);
                    $role = "mentor";
                    $approval_status = $mentor_row['approval_status'];
                    
                    // Profile is complete if approved
                    if ($approval_status === 'approved') {
                        $is_profile_complete = true;
                    }
                }
            }
            
            // Prepare response data
            $response_data = [
                "user_id" => (int)$row['id'],
                "email" => $row['email'],
                "role" => $role
            ];
            
            // Add profile data
            $profile = [
                "id" => (int)$row['id'],
                "full_name" => $row['name'],
                "is_profile_complete" => $is_profile_complete
            ];
            
            // Add approval status for mentors
            if ($role === "mentor" && $approval_status !== null) {
                $profile["approval_status"] = $approval_status;
            }
            
            $response_data["profile"] = $profile;
            
            // Return success response
            http_response_code(200);
            echo json_encode([
                "success" => true,
                "message" => "Login successful",
                "token" => $token,
                "data" => $response_data
            ]);
            
        } else {
            http_response_code(401);
            echo json_encode([
                "success" => false,
                "message" => "Invalid password"
            ]);
        }
        
    } else {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "message" => "User not found"
        ]);
    }
    
} else {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Email and password are required"
    ]);
}
?>
