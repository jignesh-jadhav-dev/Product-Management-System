import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/Login";
import Register from "./pages/Register";
import UserProducts from "./pages/UserProducts";
import Cart from "./pages/Cart";
import Address from "./pages/Address";
import Checkout from "./pages/Checkout";
import MyOrders from "./pages/MyOrders";
import AdminProducts from "./pages/AdminProducts";
import ProductForm from "./pages/ProductForm";
import SuperCategories from "./pages/SuperCategories";
import SuperUsers from "./pages/SuperUsers";
import SuperOrders from "./pages/SuperOrders";

function Home() {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/register" replace />;
  const to =
    user.role === "SUPER_ADMIN"
      ? "/superadmin/categories"
      : user.role === "ADMIN"
        ? "/admin/products"
        : "/user/products";
  return <Navigate to={to} replace />;
}

const ADMIN = ["ADMIN", "SUPER_ADMIN"];
const SUPER = ["SUPER_ADMIN"];

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/user/products"
          element={
            <ProtectedRoute>
              <UserProducts />
            </ProtectedRoute>
          }
        />
        <Route
          path="/user/cart"
          element={
            <ProtectedRoute>
              <Cart />
            </ProtectedRoute>
          }
        />
        <Route
          path="/user/address"
          element={
            <ProtectedRoute>
              <Address />
            </ProtectedRoute>
          }
        />
        <Route
          path="/user/checkout"
          element={
            <ProtectedRoute>
              <Checkout />
            </ProtectedRoute>
          }
        />
        <Route
          path="/user/orders"
          element={
            <ProtectedRoute>
              <MyOrders />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/products"
          element={
            <ProtectedRoute roles={ADMIN}>
              <AdminProducts />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/products/add"
          element={
            <ProtectedRoute roles={ADMIN}>
              <ProductForm />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/products/:id/edit"
          element={
            <ProtectedRoute roles={ADMIN}>
              <ProductForm />
            </ProtectedRoute>
          }
        />

        <Route
          path="/superadmin/categories"
          element={
            <ProtectedRoute roles={SUPER}>
              <SuperCategories />
            </ProtectedRoute>
          }
        />
        <Route
          path="/superadmin/users"
          element={
            <ProtectedRoute roles={SUPER}>
              <SuperUsers />
            </ProtectedRoute>
          }
        />
        <Route
          path="/superadmin/orders"
          element={
            <ProtectedRoute roles={SUPER}>
              <SuperOrders />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  );
}
