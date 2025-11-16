import { useLocation } from "react-router-dom";
import logo from "../assets/logo/logoSamdLyn.jpg";
import "../styles/Header.css";
import { Link } from "react-router-dom";

export default function Header() {
  const location = useLocation();

  // função auxiliar para verificar se a rota está ativa
  const isActive = (path: string) => location.pathname === path;

  return (
    <header>
      <div className="header-container">
        <aside className="sidebar">
          <div className="sidebar-logo">
            <img src={logo} alt="Logo" />
          </div>

          <ul className="sidebar-nav">
            <Link
              to="/"
              className={`nav-item ${isActive("/") ? "active" : ""}`}
            >
              <li>Mesas</li>
            </Link>

            <Link
              to="/historic"
              className={`nav-item ${isActive("/historic") ? "active" : ""}`}
            >
              <li>Histórico</li>
            </Link>

           <Link
              to="/delivery"
              className={`nav-item ${isActive("/delivery") ? "active" : ""}`}
            >
              <li>Delivery</li>
            </Link>
     <Link
              to="/settings"
              className={`nav-item ${
                isActive("/settings") || 
                isActive("/clientes") || 
                isActive("/profile") || 
                isActive("/products") ||
                isActive("/categories") ||
                isActive("/acrescimos") ||
                isActive("/formatPay") ||
                isActive("/cardQr") ||
                isActive("/bairros") ||
                isActive("/cupons") ||
                isActive("/delivery-cardapio")
                ?  "active" : ""}`}
            >
              <li>Ajustes</li>
            </Link>            <li className="nav-item" id="login-button">
              Login
            </li>
          </ul>
        </aside>
      </div>
    </header>
  );
}
