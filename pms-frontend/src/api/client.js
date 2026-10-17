import axios from "axios";

const api = axios.create({ baseURL: "/api" });

// ---- CSRF token handling (Spring Security session + CSRF) ----
let csrf = null;
export const resetCsrf = () => {
  csrf = null;
};

async function getCsrf() {
  if (!csrf) {
    const { data } = await api.get("/auth/csrf");
    csrf = data;
  }
  return csrf;
}

// POST / PUT / PATCH / DELETE se pehle token header mein lagao
api.interceptors.request.use(async (config) => {
  const method = (config.method || "get").toLowerCase();
  if (!["get", "head", "options"].includes(method)) {
    const t = await getCsrf();
    config.headers[t.headerName] = t.token;
  }
  return config;
});

// Token expire hua (403) to ek baar naya token lekar retry
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const { config, response } = error;
    if (response?.status === 403 && config && !config._retried) {
      config._retried = true;
      resetCsrf();
      return api(config);
    }
    return Promise.reject(error);
  },
);

// Backend ke error JSON se readable message nikalo
export function errorMessage(err) {
  const d = err?.response?.data;
  if (!d) return "Server se connect nahi ho paya";
  if (d.message) return d.message;
  if (typeof d === "object") return Object.values(d).join(", ");
  return "Something went wrong";
}

// Validation errors ({field: message}) alag se chahiye ho to
export function fieldErrors(err) {
  const d = err?.response?.data;
  if (err?.response?.status === 400 && d && typeof d === "object" && !d.message)
    return d;
  return null;
}

export default api;
