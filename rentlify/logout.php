<?php
session_start();
session_destroy();
header("Location: web_login.php");
exit();
?>
