// ============================================================
// api.js — All HTTP calls to the Spring Boot backend
// ============================================================

const API_BASE = import.meta.env.VITE_API_BASE ?? '';

export async function apiCall(method, path, body = null) {
  const token = sessionStorage.getItem('ctf_token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE}${path}`, opts);
  if (res.status === 204) return null;

  const data = await res.json().catch(() => ({ message: 'Unexpected server error' }));
  if (!res.ok) throw new Error(data.message || data.error || `Error ${res.status}`);
  return data;
}

// ---- Auth ----
export const authAPI = {
  login:    (email, password)           => apiCall('POST', '/api/auth/login',    { email, password }),
  register: (username, email, password) => apiCall('POST', '/api/auth/register', { username, email, password }),
  logout:   ()                          => apiCall('POST', '/api/auth/logout'),
};

// ---- Challenges ----
export const challengeAPI = {
  list:    () => apiCall('GET',   '/api/challenges'),
  listAll: () => apiCall('GET',   '/api/challenges/all'),
  
  // Uses multipart/form-data to support optional file upload
  create: (challengeData, file = null) => {
    const token = sessionStorage.getItem('ctf_token');
    const formData = new FormData();
    formData.append('challenge', JSON.stringify(challengeData));
    if (file) formData.append('file', file);

    return fetch(`${API_BASE}/api/challenges`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      // Do NOT set Content-Type — browser sets it with boundary automatically
      body: formData,
    }).then(async res => {
      if (res.status === 204) return null;
      const data = await res.json().catch(() => ({ message: 'Unexpected server error' }));
      if (!res.ok) throw new Error(data.message || data.error || `Error ${res.status}`);
      return data;
    });
  },

  toggle:    (id)                    => apiCall('PATCH',  `/api/challenges/${id}/toggle`),
  delete:    (id)                    => apiCall('DELETE', `/api/challenges/${id}`),
  addFlag:   (id, flag)              => apiCall('POST',   `/api/challenges/${id}/flags?flag=${encodeURIComponent(flag)}`),
  addHint:   (id, content, cost)     => apiCall('POST',   `/api/challenges/${id}/hints?content=${encodeURIComponent(content)}&cost=${cost}`),
  revealHint:(hintId)                => apiCall('GET',    `/api/challenges/hints/${hintId}/reveal`),
};

// ---- Submissions ----
export const submissionAPI = {
  submit: (challengeId, flag) => apiCall('POST', '/api/submissions', { challengeId, flag }),
};

// ---- Scoreboard ----
export const scoreboardAPI = {
  public: () => apiCall('GET', '/api/scoreboard'),
  admin:  () => apiCall('GET', '/api/scoreboard/admin'),
};

// ---- Teams ----
export const teamAPI = {
  create: (name, password) => apiCall('POST', '/api/teams',      { name, password }),
  join:   (name, password) => apiCall('POST', '/api/teams/join', { name, password }),
};

// ---- Tickets ----
export const ticketAPI = {
  list:    ()       => apiCall('GET',   '/api/tickets'),
  listAll: ()       => apiCall('GET',   '/api/tickets/all'),
  create:  (data)   => apiCall('POST',  '/api/tickets', data),
  close:   (id)     => apiCall('PATCH', `/api/tickets/${id}/close`),
};

// ---- Competition ----
export const competitionAPI = {
  list:             ()               => apiCall('GET',   '/api/competitions'),
  toggleScoreboard: (id, visible)    => apiCall('PATCH', `/api/competitions/${id}/scoreboard?visible=${visible}`),
};