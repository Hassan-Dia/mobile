<?php
/**
 * Database Configuration for XAMPP MySQL
 * Place this file in: C:\xampp\htdocs\mentorbridge\api\config\database.php
 */

class Database {
    // Database credentials
    private $host = "localhost";
    private $db_name = "mentorbridge_db";
    private $username = "root";
    private $password = ""; // XAMPP default has no password
    public $conn;

    // Get database connection
    public function getConnection() {
        $this->conn = null;

        try {
            $this->conn = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password
            );
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $this->conn->exec("set names utf8");
        } catch(PDOException $exception) {
            echo json_encode([
                "success" => false,
                "message" => "Connection error: " . $exception->getMessage()
            ]);
        }

        return $this->conn;
    }
}
?>
