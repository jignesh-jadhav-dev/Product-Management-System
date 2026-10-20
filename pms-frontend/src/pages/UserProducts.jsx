import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api/client";
import { money } from "../utils";

function ProductCard({ p, onError }) {
  const [qty, setQty] = useState(1);
  const navigate = useNavigate();

  const addToCart = async (e) => {
    e.preventDefault();
    try {
      await api.post("/user/cart/items", {
        productId: p.id,
        quantity: Number(qty),
      });
      navigate("/user/cart");
    } catch (err) {
      onError(errorMessage(err));
    }
  };

  return (
    <div className="product-card">
      <h3>{p.name}</h3>
      <p className="text-muted">{p.categoryName}</p>
      <p className="price">{money(p.price)}</p>
      <p className="stock">In stock: {p.availableQuantity}</p>
      <form onSubmit={addToCart} className="inline-form">
        <input
          type="number"
          min="1"
          max={p.availableQuantity}
          value={qty}
          onChange={(e) => setQty(e.target.value)}
          style={{ width: 70 }}
        />
        <button
          type="submit"
          className="btn btn-sm"
          disabled={p.availableQuantity === 0}
        >
          Add to Cart
        </button>
      </form>
    </div>
  );
}

export default function UserProducts() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/categories")
      .then((r) => setCategories(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);

  useEffect(() => {
    api
      .get("/user/products", { params: categoryId ? { categoryId } : {} })
      .then((r) => setProducts(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, [categoryId]);

  return (
    <div className="container">
      <h1>Available Products</h1>
      {error && <div className="alert alert-error">{error}</div>}

      <div style={{ marginBottom: 16 }}>
        <button
          className={
            categoryId === null ? "btn btn-sm" : "btn btn-sm btn-outline"
          }
          onClick={() => setCategoryId(null)}
        >
          All
        </button>{" "}
        {categories.map((c) => (
          <button
            key={c.id}
            className={
              categoryId === c.id ? "btn btn-sm" : "btn btn-sm btn-outline"
            }
            onClick={() => setCategoryId(c.id)}
          >
            {c.name}
          </button>
        ))}
      </div>

      {products.length > 0 ? (
        <div className="product-grid">
          {products.map((p) => (
            <ProductCard key={p.id} p={p} onError={setError} />
          ))}
        </div>
      ) : (
        <div className="empty-state">
          No products available in this category.
        </div>
      )}
    </div>
  );
}
