# Cloud-Based Task Scheduler and Priority Manager
**A College Computer Science Mini-Project demonstrating Design & Analysis of Algorithms (DAA) and Distributed & Cloud Computing (DCC)**

---

## 1. Project Overview
The **Cloud-Based Task Scheduler and Priority Manager** is an academic mini-project designed for engineering students to master core computer science competencies. It solves real student workload challenges (assignments, laboratory practicals, semester exam revisions, personal tasks) by integrating:
- **DAA Engine**: Greedy Density Interval Scheduling, Merge Sort ($O(n \log n)$), Insertion Sort ($O(n^2)$), Binary Search ($O(\log n)$), and Linear Search ($O(n)$).
- **DCC Infrastructure**: Cloud Database-as-a-Service (Supabase PostgreSQL), PostgREST RESTful APIs, zero-trust Row Level Security (RLS) tenant isolation, and offline-first client replication.

---

## 2. Technology Stack & Architecture
- **Mobile Client**: Native Android with Kotlin, Jetpack Compose, Material 3, and Kotlin Coroutines/Flow.
- **Local Persistence**: Android Room (SQLite) for offline-first caching and zero-latency access.
- **Cloud Database (DCC)**: Supabase managed PostgreSQL with Row Level Security.
- **Backend API (Reference Deliverable)**: Python 3 with Flask and REST endpoints.
- **Algorithm Implementations**: Written from first principles in Kotlin and Python with comparison and operation counters.

### 3-Tier Architecture
```
┌──────────────────────────────────────────────────────────────┐
│                    Presentation Tier                         │
│   Android Jetpack Compose UI (Dashboard, Lab, Tasks)        │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│                    Application / Logic Tier                  │
│   DAA Engine: Greedy Ratio Scheduler, Merge/Insertion Sort, │
│   Binary/Linear Search, Latency Simulator & Offline Queue    │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│                    Data Storage Tier                         │
│   • Client Cache: SQLite (Room Database)                    │
│   • Cloud DBaaS: Supabase PostgreSQL (Row Level Security)    │
└──────────────────────────────────────────────────────────────┘
```

---

## 3. DAA Algorithms Specification

### A. Smart Greedy Scheduling Engine
- **Greedy Rule**: Computes Value-to-Duration Density ratio:
  $$R(i) = \frac{\text{Priority Weight}(i) \times [1 + \text{Urgency Score}(i)]}{\text{Duration}(i)}$$
- Tasks are sorted in descending order of $R(i)$ and greedily allocated into available study time slots with 10-minute rest buffers.
- **Time Complexity**: $O(n \log n)$ dominated by sorting.
- **Academic Limitation**: For discrete tasks (0/1 Knapsack constraint), greedy is an approximation heuristic; true global optimum requires Dynamic Programming $O(n \cdot W)$.

### B. Sorting Algorithms
| Algorithm | Best Case | Average Case | Worst Case | Space | Stability | Mechanism |
|---|---|---|---|---|---|---|
| **Merge Sort** | $\Omega(n \log n)$ | $\Theta(n \log n)$ | $O(n \log n)$ | $O(n)$ | Stable | Divide & Conquer ($T(n) = 2T(n/2) + \Theta(n)$) |
| **Insertion Sort** | $\Omega(n)$ | $\Theta(n^2)$ | $O(n^2)$ | $O(1)$ | Stable | Incremental position insertion |

### C. Searching Algorithms
| Algorithm | Best Case | Average Case | Worst Case | Prerequisite |
|---|---|---|---|---|
| **Binary Search** | $\Omega(1)$ | $\Theta(\log n)$ | $O(\log n)$ | Input array MUST be pre-sorted |
| **Linear Search** | $\Omega(1)$ | $\Theta(n)$ | $O(n)$ | None (works on unsorted data) |

---

## 4. DCC & Cloud Security (Supabase RLS)
- **Zero-Trust Client Access**: Android app connects using the public anonymous key (`anon_key`).
- **Row Level Security**:
  ```sql
  CREATE POLICY "Users can manage own tasks"
  ON public.tasks
  FOR ALL
  USING (auth.uid() = user_id);
  ```
  Even with direct database access, queries are physically isolated by PostgreSQL at the database kernel level.
- **Offline Toleration (CAP Theorem)**: Supports offline task creation with automatic sync replay when connectivity resumes.

---

## 5. College Viva Voce Question Bank

1. **Q: What is the Greedy Choice Property in your scheduler?**
   *A:* At every step, the scheduler selects the task yielding the maximum benefit per unit time ($R_i$). A locally optimal selection strategy yields a high-utility overall schedule in polynomial time $O(n \log n)$.

2. **Q: Why can't Greedy solve the 0/1 Knapsack problem optimally?**
   *A:* Because items cannot be fractionally divided. A greedy choice might pick a high-density small task that leaves unused space that could have held two larger tasks of higher total value. Fractional Knapsack is solved optimally by greedy; 0/1 Knapsack requires Dynamic Programming or Branch & Bound.

3. **Q: Why does Merge Sort require $O(n)$ auxiliary space?**
   *A:* Unlike Quick Sort or Insertion Sort which can swap elements in-place, the merge step requires a temporary buffer to compare and merge two already-sorted subarrays.

4. **Q: What is the difference between IaaS, PaaS, and DBaaS?**
   *A:* IaaS provides raw virtual machines (AWS EC2). PaaS provides application runtimes (Heroku). DBaaS (Supabase) provides managed database storage where provisioning, backup, replication, and high availability are fully managed.

5. **Q: Why shouldn't Supabase service-role keys be bundled in the mobile app?**
   *A:* The service-role key bypasses all Row Level Security policies with root permissions. Embedding it in an APK would allow users to extract it and delete or read all other users' private database records.

---

## 6. How to Run Locally

### Android Application
1. Open the project in Android Studio (Ladybug / Koala or newer).
2. Sync Gradle files.
3. Run on an emulator (API 24+) or physical Android device.

### Python Flask Backend
```bash
cd backend
pip install -r requirements.txt
python app.py
```
Backend will start on `http://localhost:5000`.

### Supabase Cloud Setup
1. Create a project at [supabase.com](https://supabase.com).
2. Open the SQL Editor and execute the script in `/database/schema.sql`.
3. Copy your Project URL and Anon Key into the app Settings or `.env`.
