<?php
/**
 * Admin Dashboard Statistics API
 * Endpoint: GET /mentorbridge/api/admin_stats.php
 * Returns real-time statistics from the database
 */

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

include_once '../config/database.php';

// Only allow GET requests
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ]);
    exit;
}

try {
    // Create database connection
    $database = new Database();
    $db = $database->getConnection();
    
    if (!$db) {
        throw new Exception("Database connection failed");
    }
    
    // 1. Count PENDING mentor approvals (users awaiting approval)
    $query = "SELECT COUNT(*) as total 
              FROM mentors 
              WHERE approval_status = 'pending'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $pending_approvals = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // 2. Count APPROVED mentors only
    $query = "SELECT COUNT(*) as total 
              FROM mentors 
              WHERE approval_status = 'approved'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $total_mentors = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // 3. Count ACCEPTED users (all users EXCEPT those with pending mentor status)
    //    Users with pending status are not yet accepted into the system
    $query = "SELECT COUNT(*) as total 
              FROM users u 
              WHERE NOT EXISTS (
                  SELECT 1 FROM mentors m 
                  WHERE m.user_id = u.id AND m.approval_status = 'pending'
              )";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $total_users = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // 4. Count mentees from mentees table
    $query = "SELECT COUNT(*) as total FROM mentees";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $total_mentees = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // 5. Count all sessions
    $query = "SELECT COUNT(*) as total FROM sessions";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $total_sessions = (int)$stmt->fetch(PDO::FETCH_ASSOC)['total'];
    
    // 6. Calculate total revenue from completed sessions only
    $query = "SELECT COALESCE(SUM(m.hourly_rate), 0) as revenue 
              FROM sessions s 
              INNER JOIN mentors m ON s.mentor_id = m.id 
              WHERE s.status = 'completed'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    $total_revenue = (float)$result['revenue'];
    
    // Return success response
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "data" => [
            "total_users" => $total_users,
            "total_mentors" => $total_mentors,
            "total_mentees" => $total_mentees,
            "total_sessions" => $total_sessions,
            "pending_approvals" => $pending_approvals,
            "total_revenue" => $total_revenue
        ],
        "timestamp" => date('Y-m-d H:i:s')
    ]);
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Database error: " . $e->getMessage()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>
