-- Initialize database for Biometric Stress Analysis System
-- This script is executed when the database container starts for the first time

-- Set timezone to UTC
SET timezone = 'UTC';

-- Create database if not exists (handled by POSTGRES_DB environment variable)
-- This file is mainly for documentation and future extensions

-- Grant all privileges to the postgres user
-- GRANT ALL PRIVILEGES ON DATABASE stress_analysis_db TO postgres;

-- Create any necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log initialization
SELECT 'Database initialized successfully' AS status;
