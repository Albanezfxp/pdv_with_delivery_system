import { useEffect, useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import { fetchAllClients } from "../api";
import Loading from "../components/Loading";
import MainContent from "../components/MainContent";
import "../styles/Clientes.css";

type PageResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
};

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

  // ✅ paginação
  const [pageQuery, setPageQuery] = useState({
    page: 0,
    size: 10,
  });

  const [pageInfo, setPageInfo] = useState({
    totalPages: 0,
    totalElements: 0,
    page: 0,
    size: 10,
  });

  // ✅ fetch com paginação
  const fetchClientes = async () => {
    try {
      setLoading(true);

      const response = (await fetchAllClients(
        pageQuery
      )) as PageResponse<clientesInterface>;

      setClients(response.content ?? []);

      setPageInfo({
        totalPages: response.totalPages ?? 0,
        totalElements: response.totalElements ?? 0,
        page: response.number ?? pageQuery.page,
        size: response.size ?? pageQuery.size,
      });
    } catch (err) {
      console.error("Erro ao buscar clientes:", err);
    } finally {
      setLoading(false);
    }
  };

  // ✅ dispara ao mudar página
  useEffect(() => {
    fetchClientes();
  }, [pageQuery]);

  // ✅ controles
  const canPrev = pageInfo.page > 0;
  const canNext = pageInfo.page + 1 < pageInfo.totalPages;

  const prevPage = () => {
    setPageQuery((p) => ({
      ...p,
      page: Math.max(0, p.page - 1),
    }));
  };

  const nextPage = () => {
    setPageQuery((p) => ({
      ...p,
      page: p.page + 1,
    }));
  };

  // ✅ loading padronizado
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
                <th>Aniversário</th>
              </tr>
            </thead>

            <tbody>
              {clients.map((cliente) => (
                <tr key={cliente.id}>
                  <td>{cliente.name}</td>
                  <td>{cliente.email}</td>
                  <td>{cliente.phone}</td>
                  <td>
                    {cliente.endereco?.street || "Não informado"}
                  </td>
                  <td>
                    {cliente.birthday
                      ? new Date(cliente.birthday).toLocaleDateString(
                          "pt-BR"
                        )
                      : "Não informado"}
                  </td>
                </tr>
              ))}

              {clients.length === 0 && (
                <tr>
                  <td
                    colSpan={5}
                    style={{ textAlign: "center", padding: "2rem" }}
                  >
                    Nenhum cliente encontrado.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* ✅ PAGINAÇÃO */}
        <div
          style={{
            width: "100%",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginTop: 16,
          }}
        >
          <div style={{ display: "flex", gap: 8 }}>
            <button
              className="dm-filter-secondary"
              onClick={prevPage}
              disabled={!canPrev || loading}
            >
              Anterior
            </button>

            <button
              className="dm-filter-primary"
              onClick={nextPage}
              disabled={!canNext || loading}
            >
              Próxima
            </button>
          </div>

          <span>
            Página {pageInfo.page + 1} de{" "}
            {Math.max(1, pageInfo.totalPages)} •{" "}
            {pageInfo.totalElements} clientes
          </span>
        </div>
      </MainContent>
    </div>
  );
}