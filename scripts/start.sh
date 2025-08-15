#!/usr/bin/env bash
set -euo pipefail

# ---------- MySQL ----------
echo "[*] Starting MySQL..."
sudo mysqld_safe --datadir=/var/lib/mysql >/tmp/mysql.log 2>&1 &
# Wait for MySQL to accept connections
tries=0
until mysql -uroot -e "SELECT 1" >/dev/null 2>&1; do
  tries=$((tries+1))
  if [ $tries -gt 60 ]; then
    echo "MySQL failed to start"; tail -n 200 /tmp/mysql.log || true; exit 1
  fi
  sleep 1
done

# Initialize demo DB if not exists (adjust schema as you like)
mysql -uroot <<'SQL'
CREATE DATABASE IF NOT EXISTS bankmanagment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS bankmanagment.users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  balance DECIMAL(12,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bankmanagment.transactions (
  transaction_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  transaction_type VARCHAR(50),
  amount DECIMAL(12,2),
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
SQL

echo "[*] DB ready."

# ---------- X11 + Window Manager + noVNC ----------
echo "[*] Starting Xvfb + Fluxbox + x11vnc + noVNC..."
export DISPLAY=:1
Xvfb :1 -screen 0 1600x900x24 >/tmp/xvfb.log 2>&1 &
sleep 1
fluxbox >/tmp/fluxbox.log 2>&1 &
sleep 1
x11vnc -display :1 -nopw -forever -shared -rfbport 5901 >/tmp/x11vnc.log 2>&1 &
sleep 1
novnc_proxy --vnc localhost:5901 --listen 6080 >/tmp/novnc.log 2>&1 &

# ---------- App env ----------
export BMS_DB_URL="jdbc:mysql://127.0.0.1:3306/bankmanagment"
export BMS_DB_USER="root"
export BMS_DB_PASS=""

# ---------- Build & Run ----------
echo "[*] Building app (incremental)..."
mvn -B -DskipTests package

echo "[*] Launching app..."
# Use javafx:run so JavaFX modules resolve correctly
mvn -q javafx:run &
APP_PID=$!

echo "[*] All set! The 'GUI (noVNC)' browser tab should be open (port 6080)."
wait $APP_PID
