#!/usr/bin/env bash
set -e

# === DB env consumed by DataConnection.java ===
export DB_HOST="127.0.0.1"
export DB_PORT="3306"
export DB_NAME="bankmanagement"
export DB_USER="root"
export DB_PASS="root"   # must match MYSQL_ROOT_PASSWORD in docker-compose.yml

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL..."
for i in {1..60}; do
  docker compose exec -T db mysqladmin ping -h "localhost" -proot --silent && break
  sleep 2
done
echo "✅ MySQL ready."

# JavaFX via Gitpod web desktop
export DISPLAY=:1

# This is your actual entry class (with package)
MAIN_CLASS=application.Main

echo "🚀 Launching JavaFX app..."
java \
  --module-path /usr/share/openjfx/lib \
  --add-modules javafx.controls,javafx.fxml \
  -cp out:lib/mysql-connector-j.jar \
  $MAIN_CLASS
