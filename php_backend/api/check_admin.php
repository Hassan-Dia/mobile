<?php
/**
 * Check if admin user exists in database
 */

include_once '../config/database.php';

$database = new Database();
$db = $database->getConnection();

echo "<h2>Database Connection Test</h2>\n";

// Check if users table exists
$query = "SHOW TABLES LIKE 'users'";
$stmt = $db->prepare($query);
$stmt->execute();

if ($stmt->rowCount() > 0) {
    echo "✓ Users table exists<br><br>\n";
    
    // Check for admin user
    $query = "SELECT id, name, email, password FROM users WHERE email = 'admin@mentorbridge.com'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        echo "✓ Admin user found!<br>\n";
        echo "ID: " . $row['id'] . "<br>\n";
        echo "Name: " . $row['name'] . "<br>\n";
        echo "Email: " . $row['email'] . "<br>\n";
        echo "Password Hash: " . substr($row['password'], 0, 30) . "...<br><br>\n";
        
        // Test password verification
        if (password_verify('password', $row['password'])) {
            echo "✓ Password 'password' verified successfully<br>\n";
            echo "<strong>You can login with:</strong><br>\n";
            echo "Email: admin@mentorbridge.com<br>\n";
            echo "Password: password<br>\n";
        } else {
            echo "✗ Password verification failed<br>\n";
        }
    } else {
        echo "✗ Admin user NOT found in database<br><br>\n";
        echo "<strong>The users table exists but has no admin user.</strong><br>\n";
        echo "You need to run the INSERT statements from schema.sql in phpMyAdmin.<br><br>\n";
        
        // Count total users
        $query = "SELECT COUNT(*) as count FROM users";
        $stmt = $db->prepare($query);
        $stmt->execute();
        $count = $stmt->fetch(PDO::FETCH_ASSOC)['count'];
        echo "Total users in database: " . $count . "<br>\n";
    }
} else {
    echo "✗ Users table does NOT exist<br><br>\n";
    echo "<strong>The database schema has not been created.</strong><br>\n";
    echo "Please run schema.sql in phpMyAdmin to create all tables and insert sample data.<br>\n";
}
?>
