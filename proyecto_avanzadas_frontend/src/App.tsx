import {Routes,Route} from 'react-router-dom'
import {Login,Home, Reportes,ReportesUsuario,EditarUsuario,RegistroUsuario} from './pages/Index'
function App() {
 
  return (
    <>
      <Routes>
        <Route path='/' element={<Login/>}></Route>
        <Route path='Home' element={<Home/>}> </Route>
        <Route path='Reportes' element={<Reportes/>}></Route>
        <Route path='ReportesUsuario' element={<ReportesUsuario/>}></Route>
        <Route path='usuarios/registrar' element={<RegistroUsuario/>}></Route>
        <Route path='usuarios/editar/:id' element={<EditarUsuario/>}></Route>
    </Routes>
    </>
  )
}

export default App
