<?php
/**
 * Get All Mentors API
 * Endpoint: GET /mentorbridge/api/get_mentors.php
 * Optional: ?expertise=Software%20Development
 */

include_once '../config/cors.php';
include_once '../config/database.php';

// Create database connection
$database = new Database();
$db = $database->getConnection();

// Build query - only get approved mentors
$query = "SELECT m.*, AVG(r.rating) as rating, COUNT(r.id) as review_count 
          FROM mentors m 
          LEFT JOIN reviews r ON m.id = r.mentor_id 
          WHERE m.approval_status = 'approved'";

// Filter by category/expertise if provided
if (isset($_GET['category']) && !empty($_GET['category'])) {
    $query .= " AND m.expertise LIKE :category";
}

// Filter by search query (search in name, bio, expertise)
if (isset($_GET['search']) && !empty($_GET['search'])) {
    $query .= " AND (m.name LIKE :search OR m.bio LIKE :search OR m.expertise LIKE :search)";
}

$query .= " GROUP BY m.id ORDER BY rating DESC";

$stmt = $db->prepare($query);

// Bind category parameter if exists
if (isset($_GET['category']) && !empty($_GET['category'])) {
    $category = "%" . $_GET['category'] . "%";
    $stmt->bindParam(":category", $category);
}

// Bind search parameter if exists
if (isset($_GET['search']) && !empty($_GET['search'])) {
    $search = "%" . $_GET['search'] . "%";
    $stmt->bindParam(":search", $search);
}

$stmt->execute();

$mentors = [];

while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    $mentor = [
        "id" => (int)$row['id'],
        "full_name" => $row['name'],
        "bio" => $row['bio'],
        "skills" => $row['expertise'],
        "categories" => $row['expertise'],
        "experience" => (int)$row['experience'],
        "hourly_rate" => (float)$row['hourly_rate'],
        "image_url" => $row['image_url'],
        "average_rating" => $row['rating'] ? round((float)$row['rating'], 1) : 0.0,
        "total_reviews" => (int)$row['review_count']
    ];
    array_push($mentors, $mentor);
}

http_response_code(200);
echo json_encode([
    "success" => true,
    "data" => $mentors
]);
?>
