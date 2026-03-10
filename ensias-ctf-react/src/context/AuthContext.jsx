import { createContext, useContext, useState, useCallback } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => sessionStorage.getItem('ctf_token'));
  const [user, setUser]   = useState(() => {
    const stored = sessionStorage.getItem('ctf_user');
    return stored ? JSON.parse(stored) : null;
  });
  const [solvedIds, setSolvedIds] = useState(() => {
    const stored = sessionStorage.getItem('ctf_solved');
    return new Set(stored ? JSON.parse(stored) : []);
  });

  const saveSession = useCallback((newToken, newUser) => {
    setToken(newToken);
    setUser(newUser);
    sessionStorage.setItem('ctf_token', newToken);
    sessionStorage.setItem('ctf_user', JSON.stringify(newUser));
  }, []);

  const clearSession = useCallback(() => {
    setToken(null);
    setUser(null);
    setSolvedIds(new Set());
    sessionStorage.clear();
  }, []);

  const markSolved = useCallback((id) => {
    setSolvedIds(prev => {
      const next = new Set(prev);
      next.add(id);
      sessionStorage.setItem('ctf_solved', JSON.stringify([...next]));
      return next;
    });
  }, []);

  return (
    <AuthContext.Provider value={{
      token, user, solvedIds,
      saveSession, clearSession, markSolved,
      isAdmin: user?.role === 'ADMIN',
      isLoggedIn: !!token,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
