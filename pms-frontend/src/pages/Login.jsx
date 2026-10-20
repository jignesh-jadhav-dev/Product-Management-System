import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { errorMessage } from "../api/client";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const success = location.state?.success;

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
      navigate("/");
    } catch (err) {
      setError(
        err.response?.status === 401
          ? "Invalid email or password."
          : errorMessage(err),
      );
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Product Management and Order Processing System By Jignesh</h1>
        <h1>Login</h1>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={submit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              required
              placeholder="Enter your email If"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              required
              placeholder="Enter your password... "
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-block">
            Login
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: 16, fontSize: 14 }}>
          New here? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </div>
  );
}
