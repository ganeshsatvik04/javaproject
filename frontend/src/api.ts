/**
 * CareerMatch REST API Client
 * Automatically connects to the Java REST Backend endpoints.
 */
const API_BASE = import.meta.env.VITE_API_URL || '';

export async function apiFetch<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${API_BASE}${endpoint}`;
  let response: Response;
  try {
    response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });
  } catch (err: any) {
    throw new Error(
      err?.message && (err.message.includes('Failed to fetch') || err.message.includes('NetworkError'))
        ? 'Cannot connect to authentication server. Please ensure the backend is running.'
        : err?.message || 'Network connection failed'
    );
  }

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    throw new Error(errorBody.error || `Authentication failed (status ${response.status})`);
  }

  return response.json();
}

export const api = {
  // Authentication
  login: (email: string, password: string) =>
    apiFetch<any>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (userData: { name: string; email: string; password: string; confirmPassword?: string; education?: string; interests?: string }) =>
    apiFetch<any>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ ...userData, confirmPassword: userData.confirmPassword || userData.password }),
    }),

  // User Skills
  getUserSkills: (userId: number) =>
    apiFetch<any[]>(`/api/users/${userId}/skills`),

  addSkill: (userId: number, name: string, level: string) =>
    apiFetch<any>(`/api/users/${userId}/skills`, {
      method: 'POST',
      body: JSON.stringify({ name, level }),
    }),

  deleteSkill: (userId: number, skillName: string) =>
    apiFetch<any>(`/api/users/${userId}/skills/${encodeURIComponent(skillName)}`, {
      method: 'DELETE',
    }),

  // Jobs & Recommendations
  getJobs: () =>
    apiFetch<any[]>('/api/jobs'),

  getRecommendations: (userId: number, keyword?: string, company?: string, minSalary?: number, minMatch?: number) => {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (company) params.append('company', company);
    if (minSalary) params.append('minSalary', String(minSalary));
    if (minMatch) params.append('minMatch', String(minMatch));
    const query = params.toString() ? `?${params.toString()}` : '';
    return apiFetch<any[]>(`/api/users/${userId}/recommendations${query}`);
  },

  // Company Comparison
  getCompanies: () =>
    apiFetch<any[]>('/api/companies'),

  compareCompanies: (comp1: string, comp2: string, userId: number = 1) =>
    apiFetch<any[]>(`/api/companies/compare?comp1=${encodeURIComponent(comp1)}&comp2=${encodeURIComponent(comp2)}&userId=${userId}`),

  // Skill Gap Analysis
  getSkillGap: (userId: number, jobId: number) =>
    apiFetch<any>(`/api/users/${userId}/skill-gap/${jobId}`),

  // Learning Roadmap
  getRoadmap: (userId: number, jobId?: number) => {
    const query = jobId ? `?jobId=${jobId}` : '';
    return apiFetch<any[]>(`/api/users/${userId}/roadmap${query}`);
  },

  updateRoadmapProgress: (userId: number, resourceId: number, completed: boolean) =>
    apiFetch<any>(`/api/users/${userId}/roadmap/progress`, {
      method: 'POST',
      body: JSON.stringify({ resourceId, completed }),
    }),

  // Placement Readiness
  getReadiness: (userId: number) =>
    apiFetch<any>(`/api/users/${userId}/readiness`),

  saveReadiness: (userId: number, scores: { technical: number; dsa: number; projects: number; interview: number }) =>
    apiFetch<any>(`/api/users/${userId}/readiness`, {
      method: 'POST',
      body: JSON.stringify(scores),
    }),

  // Admin Console
  getAdminMetrics: () =>
    apiFetch<any>('/api/admin/metrics'),

  getAdminUsers: () =>
    apiFetch<any[]>('/api/admin/users'),
};
