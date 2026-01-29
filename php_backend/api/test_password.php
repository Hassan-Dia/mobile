<?php
/**
 * Test Password Hash
 * This script tests if the password hash works correctly
 */

// The hash from schema.sql
$stored_hash = '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi';

// Test password
$test_password = 'password';

echo "Testing password verification:\n";
echo "Password: " . $test_password . "\n";
echo "Hash: " . $stored_hash . "\n\n";

// Test if password_verify works
if (password_verify($test_password, $stored_hash)) {
    echo "✓ Password verification PASSED\n";
    echo "The hash '$stored_hash' is valid for password '$test_password'\n";
} else {
    echo "✗ Password verification FAILED\n";
    echo "The hash does not match the password\n\n";
    
    // Generate correct hash
    $new_hash = password_hash($test_password, PASSWORD_DEFAULT);
    echo "Generated new hash for 'password':\n";
    echo $new_hash . "\n\n";
    
    echo "Update your database with this SQL:\n";
    echo "UPDATE users SET password = '$new_hash' WHERE email = 'admin@mentorbridge.com';\n";
}
?>
