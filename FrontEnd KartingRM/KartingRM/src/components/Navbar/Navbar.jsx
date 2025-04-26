import { Link } from 'react-router-dom'
import './Navbar.css'

const menuItems = [
  { path: "/clientes", name: "Clientes" },
  { path: "/planes", name: "Planes" },
  { path: "/reservas", name: "Reservas" },
  { path: "/comprobantes", name: "Comprobantes" },
  { path: "/karts", name: "Karts" },
  { path: "/calendario", name: "Calendario" },
  { path: "/reportes", name: "Reportes" },
]

export default function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-left">
          <Link to="/" className="navbar-link">Karting RM</Link>
        </div>
        <ul className="navbar-right">
          {menuItems.map((item) => (
            <li key={item.path} className="navbar-item">
              <Link to={item.path} className="navbar-link">{item.name}</Link>
            </li>
          ))}
        </ul>
      </div>
    </nav>
  )
}