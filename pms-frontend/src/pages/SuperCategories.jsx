import { useCallback, useEffect, useState } from "react";
import api, { errorMessage, fieldErrors } from "../api/client";

export default function SuperCategories() {
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState("");
  const [nameError, setNameError] = useState("");
  const [error, setError] = useState("");

  const load = useCallback(() => {
    api
      .get("/categories")
      .then((r) => setCategories(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  const add = async (e) => {
    e.preventDefault();
    setError("");
    setNameError("");
    try {
      await api.post("/superadmin/categories", { name });
      setName("");
      load();
    } catch (err) {
      const fe = fieldErrors(err);
      if (fe) setNameError(fe.name || "");
      else setError(errorMessage(err));
    }
  };

  const remove = async (id) => {
    setError("");
    try {
      await api.delete(`/superadmin/categories/${id}`);
      load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="container">
      <h1>Manage Categories</h1>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        <h2>Add Category</h2>
        <form onSubmit={add} className="inline-form">
          <input
            type="text"
            placeholder="Category name"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <button type="submit" className="btn">
            Add
          </button>
          {nameError && <span className="error-text">{nameError}</span>}
        </form>
      </div>

      <div className="card">
        {categories.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {categories.map((c) => (
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td>{c.name}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => remove(c.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="text-muted">No categories yet.</p>
        )}
      </div>
    </div>
  );
}
