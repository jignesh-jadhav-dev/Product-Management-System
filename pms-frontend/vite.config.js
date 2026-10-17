import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// // https://vite.dev/config/
// export default defineConfig({
//   plugins: [react()],
// })

// /api ki saari calls Spring Boot (8080) ko jaati hain -> CORS ki zaroorat nahi
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": { target: "http://localhost:8080" },
    },
  },
});
