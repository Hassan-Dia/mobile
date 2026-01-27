<?php
/**
 * Delete Availability Slot API
 * DELETE - Remove availability slot by ID
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

if ($_SERVER['REQUEST_METHOD'] === 'DELETE') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case field names
    $availabilityId = $data->availability_id ?? $data->availabilityId ?? $data->id ?? null;
    $mentorId = $data->mentor_id ?? $data->mentorId ?? null;
    
    if (!empty($availabilityId)) {
        
        // Build query with optional mentor_id verification for security
        if (!empty($mentorId)) {
            $query = "DELETE FROM availability WHERE id = :id AND mentor_id = :mentor_id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":id", $availabilityId);
            $stmt->bindParam(":mentor_id", $mentorId);
        } else {
            $query = "DELETE FROM availability WHERE id = :id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":id", $availabilityId);
        }
        
        if ($stmt->execute()) {
            if ($stmt->rowCount() > 0) {
                http_response_code(200);
                echo json_encode([
                    "success" => true,
                    "message" => "Availability slot deleted successfully",
                    "data" => [
                        "id" => $availabilityId
                    ]
                ]);
            } else {
                http_response_code(404);
                echo json_encode([
                    "success" => false,
                    "message" => "Availability slot not found"
                ]);
            }
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to delete availability slot"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing availability ID"
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
