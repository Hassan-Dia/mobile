<?php
/**
 * Test Availability API - Debug endpoint
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

// Get mentor_id from query parameter
$mentorId = $_GET['mentor_id'] ?? 3; // Default to Emma Thompson

// Check availability table structure
$structureQuery = "DESCRIBE availability";
$structStmt = $db->prepare($structureQuery);
$structStmt->execute();
$structure = $structStmt->fetchAll(PDO::FETCH_ASSOC);

// Get all availability for this mentor
$availQuery = "SELECT * FROM availability WHERE mentor_id = :mentor_id ORDER BY session_date, session_time";
$availStmt = $db->prepare($availQuery);
$availStmt->bindParam(":mentor_id", $mentorId);
$availStmt->execute();
$availability = $availStmt->fetchAll(PDO::FETCH_ASSOC);

// Get only active availability (what the API returns)
$activeQuery = "SELECT * FROM availability 
                WHERE mentor_id = :mentor_id 
                AND is_active = 1 
                AND session_date >= CURDATE()
                ORDER BY session_date, session_time";
$activeStmt = $db->prepare($activeQuery);
$activeStmt->bindParam(":mentor_id", $mentorId);
$activeStmt->execute();
$activeAvailability = $activeStmt->fetchAll(PDO::FETCH_ASSOC);

echo json_encode([
    "success" => true,
    "mentor_id" => $mentorId,
    "current_date" => date('Y-m-d'),
    "table_structure" => $structure,
    "all_availability_count" => count($availability),
    "all_availability" => $availability,
    "active_availability_count" => count($activeAvailability),
    "active_availability" => $activeAvailability
], JSON_PRETTY_PRINT);
?>
