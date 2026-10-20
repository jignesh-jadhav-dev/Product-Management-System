import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage, fieldErrors } from "../api/client";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState("");

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setErrors({});
    setError("");
    try {
      await api.post("/auth/register", form);
      navigate("/login", {
        state: {
          success: "Registration successfully completed! Please login.",
        },
      });
    } catch (err) {
      const fe = fieldErrors(err);
      if (fe) setErrors(fe);
      else setError(errorMessage(err));
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Product Management and Order Processing System By Jignesh</h1>
        <h1>Create Account</h1>

        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={submit}>
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              name="name"
              placeholder="Your full name"
              value={form.name}
              onChange={change}
            />
            {errors.name && <span className="error-text">{errors.name}</span>}
          </div>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              name="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={change}
            />
            {errors.email && <span className="error-text">{errors.email}</span>}
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              placeholder="********"
              value={form.password}
              onChange={change}
            />
            {errors.password && (
              <span className="error-text">{errors.password}</span>
            )}
          </div>
          <button type="submit" className="btn btn-block">
            Register
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: 16, fontSize: 14 }}>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}
