"""
Design and Analysis of Algorithms (DAA) Module
College Mini-Project: Cloud-Based Task Scheduler and Priority Manager
Implements:
1. Insertion Sort (O(n) best, O(n^2) worst, in-place)
2. Merge Sort (Theta(n log n) best, avg, worst, divide & conquer)
3. Linear Search (O(n))
4. Binary Search (O(log n), verifies sorted invariant)
5. Greedy Scheduling Engine (Urgency & Priority Density heuristic)
"""

import time
import math

def insertion_sort(tasks, key_func, ascending=True):
    """
    Sorts a list of task dicts using Insertion Sort with step instrumentation.
    Complexity: Best Omega(n), Worst O(n^2), Space O(1).
    """
    arr = list(tasks)
    n = len(arr)
    comparisons = 0
    shifts = 0
    trace = []
    
    start_time = time.perf_counter_ns()
    
    for i in range(1, n):
        key_task = arr[i]
        key_val = key_func(key_task)
        j = i - 1
        
        while j >= 0:
            comparisons += 1
            curr_val = key_func(arr[j])
            condition = (curr_val > key_val) if ascending else (curr_val < key_val)
            if condition:
                arr[j + 1] = arr[j]
                shifts += 1
                j -= 1
            else:
                break
        arr[j + 1] = key_task
        trace.append(f"Pass {i}: Placed '{key_task.get('title')}' at index {j + 1}")
        
    elapsed_nanos = time.perf_counter_ns() - start_time
    
    return arr, {
        "algorithm": "Insertion Sort",
        "comparisons": comparisons,
        "shifts": shifts,
        "execution_time_micros": elapsed_nanos / 1000.0,
        "time_complexity": "O(n^2) Quadratic",
        "space_complexity": "O(1) In-place",
        "trace": trace[:30]
    }

def merge_sort(tasks, key_func, ascending=True):
    """
    Sorts a list of task dicts using Merge Sort (Divide & Conquer).
    Recurrence: T(n) = 2T(n/2) + Theta(n) => Theta(n log n).
    """
    comparisons = [0]
    merges = [0]
    
    def merge(left, right):
        result = []
        i = j = 0
        while i < len(left) and j < len(right):
            comparisons[0] += 1
            cond = (key_func(left[i]) <= key_func(right[j])) if ascending else (key_func(left[i]) >= key_func(right[j]))
            if cond:
                result.append(left[i])
                i += 1
            else:
                result.append(right[j])
                j += 1
        result.extend(left[i:])
        result.extend(right[j:])
        merges[0] += 1
        return result

    def sort_rec(arr):
        if len(arr) <= 1:
            return arr
        mid = len(arr) // 2
        left = sort_rec(arr[:mid])
        right = sort_rec(arr[mid:])
        return merge(left, right)

    start_time = time.perf_counter_ns()
    sorted_arr = sort_rec(list(tasks))
    elapsed_nanos = time.perf_counter_ns() - start_time

    return sorted_arr, {
        "algorithm": "Merge Sort",
        "comparisons": comparisons[0],
        "merges": merges[0],
        "execution_time_micros": elapsed_nanos / 1000.0,
        "time_complexity": "Theta(n log n)",
        "space_complexity": "O(n) Auxiliary buffer",
        "rationale": "Consistent performance governed by Master Theorem Case 2"
    }

def linear_search(tasks, query):
    """
    Sequential search across tasks. O(n) worst case.
    """
    start_time = time.perf_counter_ns()
    q = query.strip().lower()
    matches = []
    comparisons = 0
    
    for i, t in enumerate(tasks):
        comparisons += 1
        title = t.get("title", "").lower()
        desc = t.get("description", "").lower()
        if q in title or q in desc:
            matches.append(t)
            
    elapsed_nanos = time.perf_counter_ns() - start_time
    return matches, {
        "algorithm": "Linear Search",
        "comparisons": comparisons,
        "execution_time_micros": elapsed_nanos / 1000.0,
        "time_complexity": "O(n)",
        "matches_found": len(matches)
    }

def binary_search(tasks, target_title):
    """
    Binary search for exact title match.
    Enforces sorted invariant. O(log n).
    """
    start_time = time.perf_counter_ns()
    target = target_title.strip().lower()
    
    # Check if sorted
    is_sorted = all(tasks[i].get("title", "").lower() <= tasks[i+1].get("title", "").lower() for i in range(len(tasks) - 1))
    working_list = tasks if is_sorted else merge_sort(tasks, lambda x: x.get("title", "").lower())[0]
    
    low = 0
    high = len(working_list) - 1
    found_task = None
    iterations = 0
    
    while low <= high:
        iterations += 1
        mid = (low + high) // 2
        mid_title = working_list[mid].get("title", "").lower()
        if mid_title == target:
            found_task = working_list[mid]
            break
        elif mid_title < target:
            low = mid + 1
        else:
            high = mid - 1
            
    elapsed_nanos = time.perf_counter_ns() - start_time
    return found_task, {
        "algorithm": "Binary Search",
        "iterations": iterations,
        "execution_time_micros": elapsed_nanos / 1000.0,
        "time_complexity": "O(log n)",
        "pre_sorted_verified": is_sorted
    }

def greedy_schedule(tasks, available_minutes):
    """
    Greedy Scheduler using Urgency and Priority Density.
    Score = (PriorityWeight * UrgencyMultiplier) / Duration.
    """
    now = time.time() * 1000.0
    pending = [t for t in tasks if t.get("status") != "COMPLETED"]
    
    scored = []
    for t in pending:
        priority_weight = float(t.get("priority", 2))
        deadline_epoch = float(t.get("deadline_epoch", now + 86400000))
        hours_remaining = (deadline_epoch - now) / (1000.0 * 3600.0)
        days_remaining = max(0.1, hours_remaining / 24.0)
        
        urgency = 4.0 if hours_remaining < 0 else (2.5 if hours_remaining <= 24 else 1.0 / math.log10(days_remaining + 9))
        duration = max(15, int(t.get("duration_minutes", 60)))
        
        score = (priority_weight * 100.0 * urgency) / duration
        scored.append((t, score, duration))
        
    # Sort descending by score O(n log n)
    scored.sort(key=lambda x: x[1], reverse=True)
    
    scheduled = []
    unscheduled = []
    capacity = available_minutes
    current_time_offset = 0 # 9:00 AM start
    
    for item, score, dur in scored:
        if dur <= capacity:
            start_m = 9 * 60 + current_time_offset
            end_m = start_m + dur
            start_fmt = f"{start_m // 60:02d}:{start_m % 60:02d}"
            end_fmt = f"{end_m // 60:02d}:{end_m % 60:02d}"
            
            scheduled.append({
                "task": item,
                "start_time": start_fmt,
                "end_time": end_fmt,
                "duration_minutes": dur,
                "greedy_score": round(score, 2)
            })
            capacity -= dur
            break_time = 10 if capacity >= 40 else 0
            current_time_offset += (dur + break_time)
        else:
            unscheduled.append(item)
            
    allocated = available_minutes - capacity
    efficiency = (allocated / available_minutes * 100.0) if available_minutes > 0 else 0.0
    
    return {
        "available_minutes": available_minutes,
        "allocated_minutes": allocated,
        "efficiency_percentage": round(efficiency, 1),
        "scheduled_tasks": scheduled,
        "unscheduled_tasks": unscheduled,
        "greedy_rule": "R(i) = (Priority_i * Urgency_i) / Duration_i",
        "limitations": "Approximation heuristic for 0/1 Knapsack; exact optimum requires DP O(n*W)"
    }
