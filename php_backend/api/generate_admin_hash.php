<?php
/**
 * Generate hash for admin123 password
 */

$password = 'admin123';
$hash = password_hash($password, PASSWORD_DEFAULT);

echo "<h2>Password Hash Generator</h2>\n";
echo "Password: <strong>" . $password . "</strong><br>\n";
echo "Hash: <code>" . $hash . "</code><br><br>\n";

echo "<h3>Run this SQL in phpMyAdmin to update the admin password:</h3>\n";
echo "<textarea style='width:100%; height:100px; font-family:monospace;'>";
echo "UPDATE users SET password = '$hash' WHERE email = 'admin@mentorbridge.com';";
echo "</textarea><br><br>\n";

echo "<button onclick=\"navigator.clipboard.writeText(this.previousElementSibling.value)\">Copy SQL</button>";
?>
