import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api/client";
import { money } from "../utils";

export default function Checkout() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [addressId, setAddressId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([api.get("/user/cart"), api.get("/user/addresses")])
      .then(([c, a]) => {
        setCart(c.data);
        setAddresses(a.data);
      })
      .catch((e) => setError(errorMessage(e)));
  }, []);

  const placeOrder = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api.post("/user/orders", { addressId });
      navigate("/user/orders");
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  if (!cart)
    return (
      <div className="container">
        <h1>Checkout</h1>
        {error && <div className="alert alert-error">{error}</div>}
      </div>
    );

  return (
    <div className="container">
      <h1>Checkout</h1>
      {error && <div className="alert alert-error">{error}</div>}

      {cart.items.length === 0 ? (
        <div className="card">
          <p>
            Your cart is empty.{" "}
            <Link to="/user/products">Add products first</Link>.
          </p>
        </div>
      ) : (
        <>
          <div className="card">
            <h2>Order Summary</h2>
            <table>
              <thead>
                <tr>
                  <th>Product Name</th>
                  <th>Quantity</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                {cart.items.map((i) => (
                  <tr key={i.cartItemId}>
                    <td>{i.productName}</td>
                    <td>x{i.quantity}</td>
                    <td>{money(i.price * i.quantity)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <h3 style={{ textAlign: "right" }}>
              Total: {money(cart.totalAmount)}
            </h3>
          </div>

          {addresses.length === 0 ? (
            <div className="card">
              <p>
                No saved address found.{" "}
                <Link to="/user/address">Add an address</Link> before checkout.
              </p>
            </div>
          ) : (
            <div className="card">
              <h2>Select Delivery Address</h2>
              <form onSubmit={placeOrder}>
                {addresses.map((a) => (
                  <div className="form-group" key={a.id}>
                    <label>
                      <input
                        type="radio"
                        name="addressId"
                        required
                        checked={addressId === a.id}
                        onChange={() => setAddressId(a.id)}
                      />{" "}
                      {`${a.street}, ${a.city}, ${a.state} - ${a.pincode}`}
                    </label>
                  </div>
                ))}
                <button type="submit" className="btn">
                  Place Order
                </button>
              </form>
            </div>
          )}
        </>
      )}
    </div>
  );
}
