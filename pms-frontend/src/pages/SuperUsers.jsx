import { useCallback, useEffect, useState } from "react";
import api, { errorMessage } from "../api/client";

const ROLES = ["USER", "ADMIN", "SUPER_ADMIN"];

function UserRow({ u, run }) {
  const [role, setRole] = useState(u.role);
  return (
    <tr>
      <td>{u.id}</td>
      <td>{u.name}</td>
      <td>{u.email}</td>
      <td>
        <span className="badge badge-enabled">{u.role}</span>
      </td>
      <td>
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            run(() => api.put(`/superadmin/users/${u.id}/role`, { role }));
          }}
        >
          <select value={role} onChange={(e) => setRole(e.target.value)}>
            {ROLES.map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
          <button type="submit" className="btn btn-sm">
            Update
          </button>
        </form>
      </td>
      <td>
        <button
          className="btn btn-sm btn-danger"
          onClick={() => run(() => api.delete(`/superadmin/users/${u.id}`))}
        >
          Delete
        </button>
      </td>
    </tr>
  );
}

export default function SuperUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    api
      .get("/superadmin/users")
      .then((r) => setUsers(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  const run = async (action) => {
    setError("");
    try {
      await action();
      load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="container">
      <h1>Manage Users</h1>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Change Role</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <UserRow key={u.id} u={u} run={run} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
