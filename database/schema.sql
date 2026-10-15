-- ==============================================================================
-- Cloud-Based Task Scheduler and Priority Manager
-- Supabase / PostgreSQL Database Schema and Security Configuration
-- DAA (Design and Analysis of Algorithms) & DCC (Distributed and Cloud Computing)
-- ==============================================================================

-- 1. Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Create Tasks Table
CREATE TABLE IF NOT EXISTS public.tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL DEFAULT 'ASSIGNMENT',
    priority INT NOT NULL CHECK (priority BETWEEN 1 AND 3), -- 1: Low, 2: Medium, 3: High
    duration_minutes INT NOT NULL DEFAULT 60 CHECK (duration_minutes > 0),
    deadline_epoch BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    created_at BIGINT DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. High Performance B-Tree Indexes for Asymptotic Query Optimization
CREATE INDEX IF NOT EXISTS idx_tasks_user_status ON public.tasks (user_id, status);
CREATE INDEX IF NOT EXISTS idx_tasks_deadline ON public.tasks (deadline_epoch ASC);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON public.tasks (priority DESC);

-- 4. Enable Row Level Security (RLS) - Zero-Trust Tenant Isolation (DCC)
ALTER TABLE public.tasks ENABLE ROW LEVEL SECURITY;

-- 5. Row Level Security Policies
-- Policy 1: Authenticated users can SELECT only their own tasks
CREATE POLICY "Users can view own tasks"
ON public.tasks
FOR SELECT
USING (auth.uid() = user_id OR user_id = current_setting('request.jwt.claim.sub', true));

-- Policy 2: Authenticated users can INSERT tasks for their own user_id
CREATE POLICY "Users can insert own tasks"
ON public.tasks
FOR INSERT
WITH CHECK (auth.uid() = user_id OR user_id = current_setting('request.jwt.claim.sub', true));

-- Policy 3: Authenticated users can UPDATE only their own tasks
CREATE POLICY "Users can update own tasks"
ON public.tasks
FOR UPDATE
USING (auth.uid() = user_id OR user_id = current_setting('request.jwt.claim.sub', true));

-- Policy 4: Authenticated users can DELETE only their own tasks
CREATE POLICY "Users can delete own tasks"
ON public.tasks
FOR DELETE
USING (auth.uid() = user_id OR user_id = current_setting('request.jwt.claim.sub', true));

-- 6. Trigger for updated_at timestamp maintenance
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER update_tasks_modtime
BEFORE UPDATE ON public.tasks
FOR EACH ROW
EXECUTE PROCEDURE update_modified_column();
