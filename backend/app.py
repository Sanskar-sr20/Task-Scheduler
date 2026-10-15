"""
Flask Backend API: Cloud-Based Task Scheduler and Priority Manager
Demonstrating DAA & DCC Architectures with Supabase PostgREST Integration
"""

import os
from flask import Flask, request, jsonify
from dotenv import load_dotenv
from algorithms import (
    insertion_sort,
    merge_sort,
    linear_search,
    binary_search,
    greedy_schedule
)

load_dotenv()

app = Flask(__name__)

SUPABASE_URL = os.getenv("SUPABASE_URL", "https://xyzcompany.supabase.co")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY", "sb_anon_placeholder_key")

# In-memory fallback if Supabase cloud credentials are in local testing mode
MEMORY_DB = []

@app.route("/api/health", methods=["GET"])
def health():
    return jsonify({
        "status": "online",
        "service": "Cloud Task Scheduler API",
        "dcc_backend": "Flask + Supabase PostgREST",
        "daa_engine": "Active"
    })

@app.route("/api/tasks", methods=["GET"])
def get_tasks():
    user_id = request.headers.get("X-User-ID", "student_2026_demo")
    user_tasks = [t for t in MEMORY_DB if t.get("user_id") == user_id]
    return jsonify({"tasks": user_tasks, "count": len(user_tasks)})

@app.route("/api/tasks", methods=["POST"])
def create_task():
    data = request.get_json() or {}
    title = data.get("title", "").strip()
    if not title:
        return jsonify({"error": "Task title is required"}), 400
    
    user_id = request.headers.get("X-User-ID", data.get("user_id", "student_2026_demo"))
    task = {
        "id": data.get("id", str(len(MEMORY_DB) + 1)),
        "user_id": user_id,
        "title": title,
        "description": data.get("description", ""),
        "category": data.get("category", "ASSIGNMENT"),
        "priority": int(data.get("priority", 2)),
        "duration_minutes": int(data.get("duration_minutes", 60)),
        "deadline_epoch": int(data.get("deadline_epoch", 0)),
        "status": data.get("status", "PENDING")
    }
    MEMORY_DB.append(task)
    return jsonify({"message": "Task created", "task": task}), 201

@app.route("/api/schedule/generate", methods=["POST"])
def generate_schedule():
    payload = request.get_json() or {}
    available_mins = int(payload.get("available_minutes", 180))
    user_id = request.headers.get("X-User-ID", "student_2026_demo")
    
    tasks = [t for t in MEMORY_DB if t.get("user_id") == user_id]
    schedule_result = greedy_schedule(tasks, available_mins)
    return jsonify(schedule_result)

@app.route("/api/algorithms/benchmark", methods=["POST"])
def benchmark_algorithm():
    payload = request.get_json() or {}
    algo_name = payload.get("algorithm", "merge_sort")
    n = int(payload.get("n", 50))
    
    # Generate synthetic tasks
    synthetic = [
        {"id": f"t_{i}", "title": f"Task #{n - i}", "priority": (i % 3) + 1, "duration_minutes": (i * 7 % 120) + 15}
        for i in range(n)
    ]
    
    if algo_name == "insertion_sort":
        _, metrics = insertion_sort(synthetic, lambda x: x["duration_minutes"])
    elif algo_name == "merge_sort":
        _, metrics = merge_sort(synthetic, lambda x: x["duration_minutes"])
    elif algo_name == "linear_search":
        _, metrics = linear_search(synthetic, f"Task #{n // 2}")
    elif algo_name == "binary_search":
        _, metrics = binary_search(synthetic, f"Task #{n // 2}")
    else:
        return jsonify({"error": "Unknown algorithm"}), 400
        
    return jsonify({"metrics": metrics, "input_size": n})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
