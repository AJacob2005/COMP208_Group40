<?php
$path = isset($_GET['path']) ? $_GET['path'] : '';

// Only allow safe API paths
if (empty($path) || !preg_match('/^[a-zA-Z0-9\/\-_]+$/', $path)) {
    http_response_code(400);
    header('Content-Type: application/json');
    exit('{"error":"Bad request"}');
}

$method = $_SERVER['REQUEST_METHOD'];
$targetUrl = 'http://localhost:8080/api/' . $path;

// Forward query string (excluding our 'path' param)
$query = $_SERVER['QUERY_STRING'];
$query = preg_replace('/(^|&)path=[^&]*/', '', $query);
$query = ltrim($query, '&');
if ($query) {
    $targetUrl .= '?' . $query;
}

$ch = curl_init($targetUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);

if ($method === 'POST') {
    $body = file_get_contents('php://input');
    curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
}

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

http_response_code($httpCode);
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
echo $response;
