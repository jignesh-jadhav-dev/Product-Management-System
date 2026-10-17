import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { errorMessage } from "../api/client";
import { money } from "../utils";

function ProductRow({ p, run }) {
  const [price, setPrice] = useState("");
  const [qty, setQty] = useState("");
  const enabled = p.status === "ENABLED";

  return (
    <div className="card">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-start",
        }}
      >
        <div>
          <h3>{p.name}</h3>
          <p className="text-muted" style={{ margin: "2px 0" }}>
            ID: {p.id} &nbsp;|&nbsp; Category: {p.categoryName}
          </p>
          {p.description && (
            <p style={{ margin: "8px 0", fontSize: 14 }}>{p.description}</p>
          )}
        </div>
        <span
          className={`badge ${enabled ? "badge-enabled" : "badge-disabled"}`}
        >
          {p.status}
        </span>
      </div>

      <p>
        Price: {money(p.price)} &nbsp;|&nbsp; Stock: {p.availableQuantity}
      </p>

      <div
        style={{ display: "flex", gap: 16, flexWrap: "wrap", marginTop: 12 }}
      >
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            run(() =>
              api.patch(`/admin/products/${p.id}/price`, {
                price: Number(price),
              }),
            ).then(() => setPrice(""));
          }}
        >
          <input
            type="number"
            step="0.01"
            required
            placeholder={p.price}
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            style={{ width: 100 }}
          />
          <button type="submit" className="btn btn-sm">
            Update Price
          </button>
        </form>

        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            run(() =>
              api.patch(`/admin/products/${p.id}/inventory`, {
                quantity: Number(qty),
              }),
            ).then(() => setQty(""));
          }}
        >
          <input
            type="number"
            required
            placeholder={p.availableQuantity}
            value={qty}
            onChange={(e) => setQty(e.target.value)}
            style={{ width: 90 }}
          />
          <button type="submit" className="btn btn-sm">
            Update Stock
          </button>
        </form>

        <button
          className="btn btn-sm btn-danger"
          onClick={() =>
            run(() =>
              api.patch(`/admin/products/${p.id}/status`, {
                status: enabled ? "DISABLED" : "ENABLED",
              }),
            )
          }
        >
          {enabled ? "Disable" : "Enable"}
        </button>

        <Link to={`/admin/products/${p.id}/edit`} className="btn btn-sm">
          Edit
        </Link>
      </div>
    </div>
  );
}

export default function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    api
      .get("/admin/products")
      .then((r) => setProducts(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  // koi bhi action chalao, fir list refresh; error aaye to dikhao
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
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <h1>Manage Products</h1>
        <Link to="/admin/products/add" className="btn">
          + Add Product
        </Link>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {products.map((p) => (
        <ProductRow key={p.id} p={p} run={run} />
      ))}
      {products.length === 0 && (
        <div className="empty-state">
          No products yet. Add your first product.
        </div>
      )}
    </div>
  );
}
