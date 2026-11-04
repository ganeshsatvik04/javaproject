# 🚀 CareerMatch – Railway Cloud Deployment Guide

This project is now fully configured for seamless deployment to **Railway** (with an automatic free HTTPS domain, persistent SQLite database storage, and a modern web UI).

---

## 🌟 Recommended: Option 1 — Unified 1-Click Deployment (Simplest & Best)

In this setup, Railway uses the root `Dockerfile` to:
1. Build the modern **React Web Frontend**.
2. Embed the web application into the **Java Backend REST Server**.
3. Run both on a **single port and single domain**.
4. **Benefits:** 0 CORS issues, 1 click, lowest memory usage, 1 unified public URL!

### Step 1: Push Your Code to GitHub
1. Open your terminal in this project root (`CareerMatch_Backend_Java`).
2. Initialize git and commit:
   ```bash
   git init
   git add .
   git commit -m "feat: complete web frontend, java REST API and railway deployment setup"
   ```
3. Push to your GitHub account:
   ```bash
   git remote add origin https://github.com/<your-username>/careermatch.git
   git branch -M main
   git push -u origin main
   ```

### Step 2: Deploy on Railway
1. Go to **[railway.com](https://railway.com)** (or [railway.app](https://railway.app)) and log in with GitHub.
2. Click **"+ New Project"** -> **"Deploy from GitHub repo"**.
3. Select your `careermatch` repository.
4. Click **"Deploy Now"**.

### Step 3: Add a Persistent Volume (For SQLite Database)
To ensure your database changes, new users, and skills are never erased on redeployments:
1. Click on your deployed service on the Railway canvas.
2. Go to the **"Volumes"** tab.
3. Click **"+ Add Volume"**.
4. Set the Mount Path to:
   ```
   /app/data
   ```
5. Click **Add**.

### Step 4: Generate Your Free Public Domain
1. In your service settings, navigate to the **"Networking"** section.
2. Click **"Generate Domain"**.
3. Railway will instantly assign your free live HTTPS link, for example:
   ```
   https://careermatch-production.up.railway.app
   ```
4. Open the link in your browser!
   * The root URL (`/`) displays the SaaS Web Frontend.
   * The API endpoints (`/api/health`, `/api/jobs`, etc.) connect directly to the Java backend.

---

## 🛠️ Option 2 — Two Separate Services (Frontend + Backend)

If you prefer deploying the Web Frontend and Java Backend as two distinct services in the same Railway project:

### 1. Backend Service:
* Click **New Service > GitHub Repo**.
* Set **Root Directory** to: `CareerMatch_Backend`
* Set **Volume Mount Path** to: `/app/data`
* Generate Domain -> Copy your Backend URL (e.g. `https://careermatch-api.up.railway.app`).

### 2. Frontend Service:
* Click **New Service > GitHub Repo** (same repo).
* Set **Root Directory** to: `frontend`
* Add Environment Variable:
  ```
  VITE_API_URL=https://careermatch-api.up.railway.app
  ```
* Generate Domain -> Open your frontend web link!

---

## 📡 Live API Endpoints Reference

| Endpoint | Method | Description |
|---|---|---|
| `/api/health` | `GET` | System heartbeat & database status |
| `/api/auth/login` | `POST` | User login (`{email, password}`) |
| `/api/auth/register` | `POST` | User registration |
| `/api/jobs` | `GET` | List all available job roles |
| `/api/users/{id}/recommendations` | `GET` | Algorithmic job matches (with filters: `keyword`, `company`, `minSalary`, `minMatch`) |
| `/api/users/{id}/skills` | `GET / POST` | Retrieve or add candidate skills |
| `/api/companies` | `GET` | List all partner hiring companies |
| `/api/companies/compare` | `GET` | Side-by-side comparison (`?comp1=Microsoft&comp2=Stripe`) |
| `/api/users/{id}/skill-gap/{jobId}` | `GET` | Gap analysis for a specific target job |
| `/api/users/{id}/roadmap` | `GET` | Personalized learning milestone curriculum |
| `/api/users/{id}/readiness` | `GET / POST` | Placement readiness score (Technical, DSA, Projects, Interview) |
| `/api/admin/metrics` | `GET` | Platform-wide analytics for admin console |
