import axios from "axios";

const api = axios.create({ baseURL: "/api" });

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

api.interceptors.request.use(async (config) => {
  const method = (config.method || "get").toLowerCase();
  if (!["get", "head", "options"].includes(method)) {
    const t = await getCsrf();
    config.headers[t.headerName] = t.token;
  }
  return config;
});

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

export function errorMessage(err) {
  const d = err?.response?.data;
  if (!d) return "Server is not connected now ";
  if (d.message) return d.message;
  if (typeof d === "object") return Object.values(d).join(", ");
  return "Something went wrong";
}

export function fieldErrors(err) {
  const d = err?.response?.data;
  if (err?.response?.status === 400 && d && typeof d === "object" && !d.message)
    return d;
  return null;
}

export default api;