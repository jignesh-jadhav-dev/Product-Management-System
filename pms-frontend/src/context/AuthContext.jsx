import { createContext, useContext, useEffect, useState } from "react";
import api, { resetCsrf } from "../api/client";

const AuthContext = createContext(null);
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadUser = async () => {
    try {
      const { data } = await api.get("/auth/me");
      setUser(data);
      return data;
    } catch {
      setUser(null);
      return null;
    }
  };

  useEffect(() => {
    loadUser().finally(() => setLoading(false));
  }, []);

  const login = async (email, password) => {
    const body = new URLSearchParams({ username: email, password });
    await api.post("/auth/login", body); // 401 aaya to throw hoga
    resetCsrf(); // login ke baad session badalta hai -> naya CSRF token
    return loadUser();
  };

  const logout = async () => {
    await api.post("/auth/logout");
    resetCsrf();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
