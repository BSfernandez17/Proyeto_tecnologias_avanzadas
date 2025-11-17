import { useParams } from 'react-router-dom';
import { CamarasPorUsuario } from '../../components/CamarasPorUsuario';

export const ReportesUsuario = () => {
    const { id } = useParams<{ id?: string }>();

  return (
    <CamarasPorUsuario id={id} />
  )
}

export default ReportesUsuario
