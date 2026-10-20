import { useCallback, useEffect, useState } from "react";
import api, { errorMessage, fieldErrors } from "../api/client";

const EMPTY = { street: "", city: "", state: "", pincode: "" };

export default function Address() {
  const [addresses, setAddresses] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState("");

  const load = useCallback(() => {
    api
      .get("/user/addresses")
      .then((r) => setAddresses(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setErrors({});
    setError("");
    try {
      await api.post("/user/addresses", form);
      setForm(EMPTY);
      load();
    } catch (err) {
      const fe = fieldErrors(err);
      if (fe) setErrors(fe);
      else setError(errorMessage(err));
    }
  };

  const field = (name, label) => (
    <div className="form-group">
      <label>{label}</label>
      <input type="text" name={name} value={form[name]} onChange={change} />
      {errors[name] && <span className="error-text">{errors[name]}</span>}
    </div>
  );

  return (
    <div className="container">
      <h1>My Addresses</h1>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        <h2>Add New Address</h2>
        <form onSubmit={submit}>
          {field("street", "Street")}
          {field("city", "City")}
          {field("state", "State")}
          {field("pincode", "Pin code")}
          <button type="submit" className="btn">
            Save Address
          </button>
        </form>
      </div>

      <div className="card">
        <h2>Saved Addresses</h2>
        {addresses.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>Street</th>
                <th>City</th>
                <th>State</th>
                <th>Pin code</th>
              </tr>
            </thead>
            <tbody>
              {addresses.map((a) => (
                <tr key={a.id}>
                  <td>{a.street}</td>
                  <td>{a.city}</td>
                  <td>{a.state}</td>
                  <td>{a.pincode}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="text-muted">No addresses saved yet.</p>
        )}
      </div>
    </div>
  );
}
