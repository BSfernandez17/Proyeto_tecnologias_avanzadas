import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContex';
import { EditarUsuario, Home, Login, RegistroUsuario, Reportes, ReportesUsuario } from './pages/Index';

function App() {
  const { rol } = useAuth();

  return (
    <>
      <Routes>
        <Route path='/' element={<Login/>}></Route>
        <Route path='/home' element={rol === 'ADMIN' ? <Home/> : <Navigate to="/" replace />} />
        <Route path='/reportes' element={rol === 'ADMIN' ? <Reportes/> : <Navigate to="/" replace />} />
        <Route path='/reportesUsuario/:id' element={rol === 'ADMIN' ? <ReportesUsuario/> : <Navigate to="/" replace />} />
        <Route path='/usuarios/registrar' element={rol === 'ADMIN' ? <RegistroUsuario/> : <Navigate to="/" replace />} />
        <Route path='/usuarios/editar/:id' element={rol === 'ADMIN' ? <EditarUsuario/> : <Navigate to="/" replace />} />
        <Route path='/usuarios/VerReporte/:id' element={rol === 'ADMIN' ? <ReportesUsuario/> : <Navigate to="/" replace />} />
        {/* Ruta por defecto para usuarios no ADMIN */}
        <Route path='*' element={<Navigate to="/" replace />} />
    </Routes>
    </>
  )
}

export default App
