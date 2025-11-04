import Header from "../components/Header";
import "../styles/Historic.css";
import { useState } from "react";

interface Venda {
  id: number;
  data: string;
  total: number;
  itens: number;
}

interface FormaPagamento {
  id: number;
  metodo: string;
  valor: number;
  percentual: number;
}

interface Movimentacao {
  id: number;
  tipo: "entrada" | "saida";
  descricao: string;
  valor: number;
  data: string;
}

export default function Historic() {
  const [activeTab, setActiveTab] = useState("vendas");

  // 🧾 MOCKS DE DADOS
  const vendas: Venda[] = [
    { id: 1, data: "18/10/2025", total: 450.5, itens: 23 },
    { id: 2, data: "17/10/2025", total: 320.0, itens: 19 },
    { id: 3, data: "16/10/2025", total: 612.8, itens: 31 },
  ];

  const formasPagamento: FormaPagamento[] = [
    { id: 1, metodo: "Dinheiro", valor: 680.0, percentual: 40 },
    { id: 2, metodo: "Cartão de Crédito", valor: 850.5, percentual: 50 },
    { id: 3, metodo: "Pix", valor: 170.3, percentual: 10 },
  ];

  const movimentacoes: Movimentacao[] = [
    { id: 1, tipo: "entrada", descricao: "Venda mesa 5", valor: 120.5, data: "18/10/2025" },
    { id: 2, tipo: "saida", descricao: "Compra de ingredientes", valor: 240.0, data: "18/10/2025" },
    { id: 3, tipo: "entrada", descricao: "Pedido delivery", valor: 89.9, data: "17/10/2025" },
  ];

  return (
    <>
        <div id="historic-page-container">
      <Header />
      <main className="historic-main">
        <div className="historic-title">
          <h1>Relatório de Vendas</h1>
          <hr />
        </div>

        <div className="historic-nav">
          <nav>
            <ul>
              <li
                className={`historic-nav-item ${activeTab === "vendas" ? "active" : ""}`}
                onClick={() => setActiveTab("vendas")}
              >
                Vendas
              </li>
              <li
                className={`historic-nav-item ${activeTab === "formas" ? "active" : ""}`}
                onClick={() => setActiveTab("formas")}
              >
                Formas de Pagamento
              </li>
              <li
                className={`historic-nav-item ${activeTab === "mov" ? "active" : ""}`}
                onClick={() => setActiveTab("mov")}
              >
                Movimentações
              </li>
            </ul>
          </nav>
        </div>

        <section className="historic-content">
          {activeTab === "vendas" && (
            <div className="historic-card">
                <div className="historic-card-title">
              <h2>Resumo de Vendas</h2>
              <p>Filtrar</p>
              </div>
              <table className="historic-table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Itens</th>
                    <th>Total (R$)</th>
                  </tr>
                </thead>
                <tbody>
                  {vendas.map((venda) => (
                    <tr key={venda.id}>
                      <td>{venda.data}</td>
                      <td>{venda.itens}</td>
                      <td>{venda.total.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {activeTab === "formas" && (
            <div className="historic-card">
              <h2>Formas de Pagamento</h2>
              <table className="historic-table">
                <thead>
                  <tr>
                    <th>Método</th>
                    <th>Valor (R$)</th>
                    <th>Percentual</th>
                  </tr>
                </thead>
                <tbody>
                  {formasPagamento.map((forma) => (
                    <tr key={forma.id}>
                      <td>{forma.metodo}</td>
                      <td>{forma.valor.toFixed(2)}</td>
                      <td>{forma.percentual}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {activeTab === "mov" && (
            <div className="historic-card">
              <h2>Movimentações</h2>
              <table className="historic-table">
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Descrição</th>
                    <th>Tipo</th>
                    <th>Valor (R$)</th>
                  </tr>
                </thead>
                <tbody>
                  {movimentacoes.map((mov) => (
                    <tr key={mov.id}>
                      <td>{mov.data}</td>
                      <td>{mov.descricao}</td>
                      <td className={mov.tipo === "entrada" ? "entrada" : "saida"}>
                        {mov.tipo.toUpperCase()}
                      </td>
                      <td>{mov.valor.toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
      </div>
    </>
  );
}
