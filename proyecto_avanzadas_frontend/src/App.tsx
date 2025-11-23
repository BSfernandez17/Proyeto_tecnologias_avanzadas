import {Routes,Route} from 'react-router-dom'
import {Login,Home, Reportes,ReportesUsuario,EditarUsuario,RegistroUsuario} from './pages/Index'
import InformeCamaraMasArchivos from './pages/admin/InformeCamaraMasArchivos'
import ArchivosFiltrados from './pages/admin/ArchivosFiltrados'
import ArchivosPorCamara from './pages/ArchivosPorCamara'
import AdminRoute from './components/AdminRoute'
function App() {
 
  return (
    <>
      <Routes>
        <Route path='/' element={<Login/>}></Route>
        <Route path='usuarios/registrar' element={<RegistroUsuario/>}></Route>
        {/* Rutas protegidas solo para admin */}
        <Route path='Home' element={<AdminRoute><Home/></AdminRoute>}></Route>
        <Route path='Reportes' element={<AdminRoute><Reportes/></AdminRoute>}></Route>
        <Route path='ReportesUsuario' element={<AdminRoute><ReportesUsuario/></AdminRoute>}></Route>
        <Route path='usuarios/editar/:id' element={<AdminRoute><EditarUsuario/></AdminRoute>}></Route>
        <Route path='usuarios/VerReporte/:id' element={<AdminRoute><ReportesUsuario/></AdminRoute>}></Route>
        <Route path='admin/archivos-camara/:camaraId' element={<AdminRoute><ArchivosPorCamara/></AdminRoute>} />
        <Route path='admin/informe-camara-mas-archivos' element={<AdminRoute><InformeCamaraMasArchivos/></AdminRoute>} />
        <Route path='admin/archivos-filtrados' element={<AdminRoute><ArchivosFiltrados/></AdminRoute>} />
      </Routes>
    </>
  )
}

export default App
