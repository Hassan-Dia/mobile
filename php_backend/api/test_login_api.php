<?php
/**
 * Test actual login API call
 * Access via: http://localhost/mentorbridge/api/test_login_api.php
 */

echo "<h2>Testing Login API</h2>";

// Simulate POST request
$postData = json_encode([
    "email" => "shokor@gmail.com",
    "password" => "shkrsmart"
]);

$ch = curl_init('http://localhost/mentorbridge/api/login.php');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json',
    'Content-Length: ' . strlen($postData)
]);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "<p><strong>HTTP Status Code:</strong> $httpCode</p>";
echo "<p><strong>Raw Response:</strong></p>";
echo "<pre>" . htmlspecialchars($response) . "</pre>";

echo "<p><strong>Formatted JSON:</strong></p>";
$json = json_decode($response, true);
echo "<pre>" . print_r($json, true) . "</pre>";

if (isset($json['success']) && $json['success']) {
    echo "<p style='color:green;font-weight:bold'>✓ Login API works correctly!</p>";
} else {
    echo "<p style='color:red;font-weight:bold'>✗ Login API failed!</p>";
    if (isset($json['message'])) {
        echo "<p>Error message: " . $json['message'] . "</p>";
    }
}
?>
