#!/bin/bash
set -e

function create_db() {
  local DB=$1
  echo "Creating database: $DB"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    SELECT 'CREATE DATABASE $DB'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB')\\gexec
    \\c $DB
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS postgis;
EOSQL
}

create_db "agriconnect_auth"
create_db "agriconnect_users"
create_db "agriconnect_labor"

echo "All databases initialized."
