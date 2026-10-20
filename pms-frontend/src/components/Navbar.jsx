import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  if (!user) return null;

  const onLogout = async () => {
    await logout();
    navigate("/login");
  };

  const isAdmin = user.role === "ADMIN" || user.role === "SUPER_ADMIN";

  return (
    <div className="navbar">
      <div className="brand">
        Product Management &amp; Order Processing System By Jignesh
      </div>
      <div className="links">
        {user.role === "USER" && (
          <span>
            <Link to="/user/products">Products</Link>
            <Link to="/user/cart">Cart</Link>
            <Link to="/user/address">Address</Link>
            <Link to="/user/orders">My Orders</Link>
          </span>
        )}
        {isAdmin && (
          <span>
            <Link to="/admin/products">Manage Products</Link>
          </span>
        )}
        {user.role === "SUPER_ADMIN" && (
          <span>
            <Link to="/superadmin/categories">Categories</Link>
            <Link to="/superadmin/users">Users</Link>
            <Link to="/superadmin/orders">All Orders</Link>
          </span>
        )}
        <span>({user.email})</span>
        <button
          className="btn btn-sm btn-danger"
          style={{ marginLeft: 12 }}
          onClick={onLogout}
        >
          Logout
        </button>
      </div>
    </div>
  );
}
