import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar/Navbar'
import Home from './pages/Home/Home'
import Clientes from './pages/Clientes/Clientes'
import Planes from './pages/Planes/Planes'
import Reservas from './pages/Reservas/Reservas'
import Comprobantes from './pages/Comprobantes/Comprobantes'
import Karts from './pages/Karts/Karts'
import Calendario from './pages/Calendario/Calendario'
import Reportes from './pages/Reportes/Reportes'
import './App.css'

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/clientes" element={<Clientes />} />
          <Route path="/planes" element={<Planes />} />
          <Route path="/reservas" element={<Reservas />} />
          <Route path="/comprobantes" element={<Comprobantes />} />
          <Route path="/karts" element={<Karts />} />
          <Route path="/calendario" element={<Calendario />} />
          <Route path="/reportes" element={<Reportes />} />
        </Routes>
      </main>
    </div>
  )
}