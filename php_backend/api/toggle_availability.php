<?php
/**
 * Toggle Availability Slot API
 * PUT - Enable/disable availability slot without deleting
 */

include_once '../config/cors.php';
include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    
    $data = json_decode(file_get_contents("php://input"));
    
    // Accept both camelCase and snake_case field names
    $availabilityId = $data->availability_id ?? $data->availabilityId ?? $data->id ?? null;
    $mentorId = $data->mentor_id ?? $data->mentorId ?? null;
    $isActive = $data->is_active ?? $data->isActive ?? null;
    
    if (!empty($availabilityId) && $isActive !== null) {
        
        // Convert to boolean int
        $activeStatus = $isActive ? 1 : 0;
        
        // Build query with optional mentor_id verification for security
        if (!empty($mentorId)) {
            $query = "UPDATE availability SET is_active = :is_active WHERE id = :id AND mentor_id = :mentor_id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":id", $availabilityId);
            $stmt->bindParam(":is_active", $activeStatus);
            $stmt->bindParam(":mentor_id", $mentorId);
        } else {
            $query = "UPDATE availability SET is_active = :is_active WHERE id = :id";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":id", $availabilityId);
            $stmt->bindParam(":is_active", $activeStatus);
        }
        
        if ($stmt->execute()) {
            if ($stmt->rowCount() > 0) {
                http_response_code(200);
                echo json_encode([
                    "success" => true,
                    "message" => "Availability slot " . ($activeStatus ? "enabled" : "disabled") . " successfully",
                    "data" => [
                        "id" => $availabilityId,
                        "is_active" => $activeStatus
                    ]
                ]);
            } else {
                http_response_code(404);
                echo json_encode([
                    "success" => false,
                    "message" => "Availability slot not found or no change needed"
                ]);
            }
        } else {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Failed to toggle availability slot"
            ]);
        }
        
    } else {
        http_response_code(400);
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields (id and is_active)"
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
