import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api, { errorMessage, fieldErrors } from "../api/client";

export default function ProductForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    categoryId: "",
    quantity: "",
  });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/categories")
      .then((r) => setCategories(r.data))
      .catch((e) => setError(errorMessage(e)));
    if (id) {
      api
        .get(`/admin/products/${id}`)
        .then((r) => {
          const d = r.data;
          setForm({
            name: d.name ?? "",
            description: d.description ?? "",
            price: d.price ?? "",
            categoryId: d.categoryId ?? "",
            quantity: d.quantity ?? "",
          });
        })
        .catch((e) => setError(errorMessage(e)));
    }
  }, [id]);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setErrors({});
    setError("");
    const payload = {
      name: form.name,
      description: form.description,
      price: form.price === "" ? null : Number(form.price),
      categoryId: form.categoryId === "" ? null : Number(form.categoryId),
      quantity: form.quantity === "" ? null : Number(form.quantity),
    };
    try {
      if (id) await api.put(`/admin/products/${id}`, payload);
      else await api.post("/admin/products", payload);
      navigate("/admin/products");
    } catch (err) {
      const fe = fieldErrors(err);
      if (fe) setErrors(fe);
      else setError(errorMessage(err));
    }
  };

  const err = (name) =>
    errors[name] && <span className="error-text">{errors[name]}</span>;

  return (
    <div className="container">
      <h1>{id ? "Edit Product" : "Add New Product"}</h1>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        <form onSubmit={submit}>
          <div className="form-group">
            <label>Product Name</label>
            <input
              type="text"
              name="name"
              value={form.name}
              onChange={change}
            />
            {err("name")}
          </div>
          <div className="form-group">
            <label>Product Description</label>
            <input
              type="text"
              name="description"
              value={form.description}
              onChange={change}
            />
          </div>
          <div className="form-group">
            <label>Product Price</label>
            <input
              type="number"
              step="0.01"
              name="price"
              value={form.price}
              onChange={change}
            />
            {err("price")}
          </div>
          <div className="form-group">
            <label>Select Product Category</label>
            <select name="categoryId" value={form.categoryId} onChange={change}>
              <option value="">-- Select Category --</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
            {err("categoryId")}
          </div>
          <div className="form-group">
            <label>{id ? "Stock Quantity" : "Initial Quantity"}</label>
            <input
              type="number"
              name="quantity"
              value={form.quantity}
              onChange={change}
            />
            {err("quantity")}
          </div>
          <button type="submit" className="btn">
            {id ? "Save Changes" : "Save Product"}
          </button>{" "}
          <Link
            to="/admin/products"
            className="btn btn-sm"
            style={{ background: "#94a3b8" }}
          >
            Cancel
          </Link>
        </form>
      </div>
    </div>
  );
}
