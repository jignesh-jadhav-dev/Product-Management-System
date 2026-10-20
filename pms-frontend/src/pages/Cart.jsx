import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { errorMessage } from "../api/client";
import { money } from "../utils";

function CartRow({ item, onUpdate, onRemove }) {
  const [qty, setQty] = useState(item.quantity);
  return (
    <tr>
      <td>{item.productName}</td>
      <td>
        <form
          className="inline-form"
          onSubmit={(e) => {
            e.preventDefault();
            onUpdate(item.cartItemId, Number(qty));
          }}
        >
          <input
            type="number"
            min="1"
            max={item.availableQuantity}
            value={qty}
            onChange={(e) => setQty(e.target.value)}
            style={{ width: 70 }}
          />
          <button type="submit" className="btn btn-sm">
            Update
          </button>
        </form>
      </td>
      <td>{money(item.price)}</td>
      <td>{money(item.price * item.quantity)}</td>
      <td>
        <button
          className="btn btn-sm btn-danger"
          onClick={() => onRemove(item.cartItemId)}
        >
          Remove
        </button>
      </td>
    </tr>
  );
}

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    api
      .get("/user/cart")
      .then((r) => setCart(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  const update = async (id, quantity) => {
    setError("");
    try {
      const { data } = await api.put(`/user/cart/items/${id}`, { quantity });
      setCart(data);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const remove = async (id) => {
    setError("");
    try {
      await api.delete(`/user/cart/items/${id}`);
      load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  if (!cart)
    return (
      <div className="container">
        <h1>My Cart</h1>
        {error && <div className="alert alert-error">{error}</div>}
      </div>
    );

  return (
    <div className="container">
      <h1>My Cart</h1>
      {error && <div className="alert alert-error">{error}</div>}

      {cart.items.length > 0 ? (
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>Product Name</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Total</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {cart.items.map((item) => (
                <CartRow
                  key={item.cartItemId}
                  item={item}
                  onUpdate={update}
                  onRemove={remove}
                />
              ))}
            </tbody>
          </table>
          <h2 style={{ textAlign: "right" }}>
            Total: {money(cart.totalAmount)}
          </h2>
          <div style={{ textAlign: "right" }}>
            <Link to="/user/checkout" className="btn">
              Proceed to Checkout
            </Link>
          </div>
        </div>
      ) : (
        <div className="empty-state">
          Your cart is empty. <Link to="/user/products">Browse products</Link>
        </div>
      )}
    </div>
  );
}
