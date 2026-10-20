import { money, formatDate } from "../utils";

export default function OrderCard({ order, showEmail }) {
  return (
    <div className="card">
      <h2>
        {`Order #${order.orderId}`}
        {showEmail && ` — ${order.userEmail}`}
      </h2>
      <p className="text-muted">{formatDate(order.orderDate)}</p>
      <table>
        <thead>
          <tr>
            <th>Product</th>
            <th>Quantity</th>
            <th>Price</th>
          </tr>
        </thead>
        <tbody>
          {order.items.map((i, idx) => (
            <tr key={idx}>
              <td>{i.productName}</td>
              <td>{i.quantity}</td>
              <td>{money(i.price)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3 style={{ textAlign: "right" }}>Total: {money(order.totalAmount)}</h3>
    </div>
  );
}
