import { useEffect, useState } from "react";
import api, { errorMessage } from "../api/client";
import OrderCard from "../components/OrderCard";

export default function MyOrders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/user/orders")
      .then((r) => setOrders(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, []);

  return (
    <div className="container">
      <h1>My Orders</h1>
      {error && <div className="alert alert-error">{error}</div>}
      {orders.map((o) => (
        <OrderCard key={o.orderId} order={o} />
      ))}
      {orders.length === 0 && (
        <div className="empty-state">No orders placed yet.</div>
      )}
    </div>
  );
}
