<?php
/**
 * Test Login Debug Script
 * Access via: http://localhost/mentorbridge/api/test_login.php?email=shokor@gmail.com&password=shkrsmart
 */

include_once '../config/database.php';

$email = $_GET['email'] ?? 'shokor@gmail.com';
$password = $_GET['password'] ?? 'shkrsmart';

echo "<h2>Login Debug Test</h2>";
echo "<p>Testing email: <strong>$email</strong></p>";
echo "<p>Testing password: <strong>$password</strong></p>";

$database = new Database();
$db = $database->getConnection();

// Check if user exists
$query = "SELECT id, name, email, password FROM users WHERE email = :email LIMIT 1";
$stmt = $db->prepare($query);
$stmt->bindParam(":email", $email);
$stmt->execute();

echo "<h3>Database Query Result:</h3>";

if ($stmt->rowCount() > 0) {
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "<p style='color:green'>✓ User found in database!</p>";
    echo "<p>User ID: " . $row['id'] . "</p>";
    echo "<p>Name: " . $row['name'] . "</p>";
    echo "<p>Email: " . $row['email'] . "</p>";
    echo "<p>Password Hash: " . substr($row['password'], 0, 20) . "...</p>";
    
    // Test password verification
    echo "<h3>Password Verification Test:</h3>";
    if (password_verify($password, $row['password'])) {
        echo "<p style='color:green;font-weight:bold'>✓ PASSWORD MATCH! Login should work.</p>";
    } else {
        echo "<p style='color:red;font-weight:bold'>✗ PASSWORD DOES NOT MATCH!</p>";
        echo "<p>The password '$password' does not match the stored hash.</p>";
        
        // Test with 'password' (the default password in schema)
        if (password_verify('password', $row['password'])) {
            echo "<p style='color:orange'>Note: The default password 'password' works. Use that instead.</p>";
        }
    }
} else {
    echo "<p style='color:red;font-weight:bold'>✗ User NOT found in database!</p>";
    echo "<p>The email '$email' does not exist in the users table.</p>";
    echo "<p><strong>Action needed:</strong> Run the schema.sql file in phpMyAdmin to insert the Shokor user.</p>";
}

// Show all users in database
echo "<h3>All Users in Database:</h3>";
$query = "SELECT id, name, email FROM users ORDER BY id";
$stmt = $db->query($query);
echo "<table border='1' cellpadding='5'>";
echo "<tr><th>ID</th><th>Name</th><th>Email</th></tr>";
while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
    echo "<tr><td>{$row['id']}</td><td>{$row['name']}</td><td>{$row['email']}</td></tr>";
}
echo "</table>";
?>
