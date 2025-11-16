import { useEffect, useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import { fetchAllClients } from "../api";
import Loading from "../components/Loading";
import MainContent from "../components/MainContent"; // Importação essencial
import "../styles/Clientes.css";

interface clientesInterface {
  id: number;
  name: string;
  phone: string;
  email: string;
  birthday: Date;
  endereco: {
    nickname: string;
    cep: string;
    street: string;
    number: string;
    complement: string;
    reference: string;
    city: string;
    state: string;
  };
}

export default function Clientes() {
  const [loading, setLoading] = useState(true);
  const [clients, setClients] = useState<clientesInterface[]>([]);
  const totalClientes = clients.length;

  const featchClientes = async () => {
    try {
      setLoading(true);
      const clientData = await fetchAllClients();
      setClients(clientData);
      setLoading(false);
    } catch (err) {
      console.error("Erro ao buscar clientes:", err);
      setLoading(false);
    }
  };

  useEffect(() => {
    featchClientes();
  }, []);

  // Padronização do Loading para evitar desalinhamento lateral e scroll
  if (loading) {
    return (
      <div className="client-page-container">
        <Header />
        <MainContent>
          <div className="loading-wrapper">
            <Loading text="Carregando clientes..." />
          </div>
        </MainContent>
      </div>
    );
  }

  return (
    <div className="client-page-container">
      <Header />
      
      <MainContent>
        <GeralTitle title="Clientes" />
        
        <div className="table-clients container">
          <table className="historic-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>E-mail</th>
                <th>Telefone</th>
                <th>Endereço</th>
                <th>Última Compra</th>
              </tr>
            </thead>
            <tbody>
              {clients.map((cliente) => (
                <tr key={cliente.id}>
                  <td>{cliente.name}</td>
                  <td>{cliente.email}</td>
                  <td>{cliente.phone}</td>
                  <td>{cliente.endereco?.street || "Não informado"}</td>
                  <td>01/12/2025</td>
                </tr>
              ))}

              {clients.length === 0 && (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: '2rem' }}>
                    Nenhum cliente cadastrado encontrado.
                  </td>
                </tr>
              )}
            </tbody>

            {clients.length > 0 && (
              <tfoot>
                <tr className="total-geral">
                  <td colSpan={5}>
                    <strong>Total de Clientes: {totalClientes}</strong>
                  </td>
                </tr>
              </tfoot>
            )}
          </table>
        </div>
      </MainContent>
    </div>
  );
}