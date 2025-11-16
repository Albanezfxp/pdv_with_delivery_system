import Header from "../components/Header";
import "../styles/Historic.css";
import { useEffect, useState, useMemo } from "react";
import { OrderEntity } from "../types/interfaces/orderEntity.interface";
import { fetchAllOrders, fetchAllPaymentOrders } from "../api";
import Loading from "../components/Loading";
import GeralTitle from "../components/GeralTitle";
import MainContent from "../components/MainContent";

export interface FormaPagamento {
  method: string;
  amount: number;
  percentual: number;
  quantity: number;
}

interface Movimentacao {
  id: number;
  tipo: "entrada" | "saida";
  descricao: string;
  valor: number;
  data: string;
}

interface VendaAgrupada {
  data: string;
  dataOriginal: Date;
  totalVendas: number;
  quantidadeVendas: number;
  pedidos: OrderEntity[];
}

export default function Historic() {
  const [activeTab, setActiveTab] = useState("vendas");
  const [orders, setOrders] = useState<OrderEntity[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroData, setFiltroData] = useState<string>("todos");
  const [paymentsOrder, setPaymentsOrder] = useState<OrderEntity[]>([]);

  const loadFetchs = async () => {
    try {
      setLoading(true);
      const ordersData = await fetchAllOrders();
      setOrders(Array.isArray(ordersData) ? ordersData : []);
      
      const paymentsOrders = await fetchAllPaymentOrders();
      setPaymentsOrder(Array.isArray(paymentsOrders) ? paymentsOrders : []);
    } catch (error) {
      console.error('Error loading orders:', error);
      setError('Erro ao carregar pedidos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFetchs();
  }, []);

  // Proteção: Garante que "pedidos" seja sempre um array antes de filtrar
  const filtrarPorPeriodo = (pedidos: OrderEntity[]) => {
    if (!pedidos || !Array.isArray(pedidos)) return [];

    const hoje = new Date();
    const ontem = new Date(hoje);
    ontem.setDate(hoje.getDate() - 1);
    const inicioSemana = new Date(hoje);
    inicioSemana.setDate(hoje.getDate() - hoje.getDay());
    const inicioMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);

    // Ignora mesas abertas (apenas status PAYED entra no relatório)
    const pedidosClosed = pedidos.filter(p => p && p.status === "PAYED");

    return pedidosClosed.filter(pedido => {
      if (!pedido.createdAt) return false;
      const dataPedido = new Date(pedido.createdAt);
      
      switch (filtroData) {
        case "hoje": return dataPedido.toDateString() === hoje.toDateString();
        case "ontem": return dataPedido.toDateString() === ontem.toDateString();
        case "semana": return dataPedido >= inicioSemana;
        case "mes": return dataPedido >= inicioMes;
        default: return true;
      }
    });
  };

  const calcularFormasPagamento = (): FormaPagamento[] => {
    const pedidosFiltrados = filtrarPorPeriodo(paymentsOrder);
    
    const pagamentosAgrupados = pedidosFiltrados.reduce((acc, pedido) => {
      // Verifica se existem métodos de pagamento (mesas abertas podem vir sem)
      if (pedido?.paymentMethods && Array.isArray(pedido.paymentMethods)) {
        pedido.paymentMethods.forEach(pagamento => {
          const { method, amount } = pagamento;
          if (!acc[method]) acc[method] = { method, amount: 0, percentual: 0, quantity: 0 };
          acc[method].amount += amount;
          acc[method].quantity += 1;
        });
      }
      return acc;
    }, {} as Record<string, FormaPagamento>);

    const total = Object.values(pagamentosAgrupados).reduce((sum, f) => sum + f.amount, 0);
    
    return Object.values(pagamentosAgrupados).map(f => ({
      ...f,
      percentual: total > 0 ? (f.amount / total) * 100 : 0
    })).sort((a, b) => b.amount - a.amount);
  };

  const estatisticasPagamento = useMemo(() => {
    const formas = calcularFormasPagamento();
    return {
      formas,
      totalPeriodo: formas.reduce((sum, f) => sum + f.amount, 0),
      metodoMaisUsado: formas[0]?.method || 'Nenhum',
      totalTransacoes: formas.reduce((sum, f) => sum + f.quantity, 0)
    };
  }, [paymentsOrder, filtroData]);

  const pedidosFiltradosVendas = useMemo(() => {
    const orderCompleted = orders.filter(o => o && o.status === "PAYED");
    return filtrarPorPeriodo(orderCompleted);
  }, [orders, filtroData]);

  const vendasAgrupadasPorData = (): VendaAgrupada[] => {
    const agrupados = pedidosFiltradosVendas.reduce((acc, pedido) => {
      const dataFormatada = new Date(pedido.createdAt).toLocaleDateString('pt-BR');
      if (!acc[dataFormatada]) {
        acc[dataFormatada] = { data: dataFormatada, dataOriginal: new Date(pedido.createdAt), totalVendas: 0, quantidadeVendas: 0, pedidos: [] };
      }
      acc[dataFormatada].totalVendas += pedido.total || 0;
      acc[dataFormatada].quantidadeVendas += 1;
      acc[dataFormatada].pedidos.push(pedido);
      return acc;
    }, {} as Record<string, VendaAgrupada>);
    return Object.values(agrupados).sort((a, b) => b.dataOriginal.getTime() - a.dataOriginal.getTime());
  };

  const formatCurrency = (v: number) => v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

  const traduzirMetodoPagamento = (m: string) => {
    const t: Record<string, string> = { 'DINHEIRO': 'Dinheiro', 'DEBITO': 'Débito', 'CREDITO': 'Crédito', 'PIX': 'Pix' };
    return t[m] || m;
  };

  const movimentacoes: Movimentacao[] = [
    { id: 1, tipo: "entrada", descricao: "Venda mesa 5", valor: 120.5, data: "18/10/2025" },
    { id: 2, tipo: "saida", descricao: "Compra de ingredientes", valor: 240.0, data: "18/10/2025" },
  ];

  if (loading) {
    return (
      <div id="historic-page-container">
        <Header />
        <MainContent>
          <div className="loading-wrapper">
            <Loading text="Carregando..." />
          </div>
        </MainContent>
      </div>
    );
  }

  return (
    <div id="historic-page-container">
      <Header />
      <main className="historic-main">
        <GeralTitle title="Relatórios e Histórico" />

        <div className="historic-nav">
          <button className={`historic-nav-item ${activeTab === "vendas" ? "active" : ""}`} onClick={() => setActiveTab("vendas")}>Vendas</button>
          <button className={`historic-nav-item ${activeTab === "formas" ? "active" : ""}`} onClick={() => setActiveTab("formas")}>Pagamentos</button>
          <button className={`historic-nav-item ${activeTab === "mov" ? "active" : ""}`} onClick={() => setActiveTab("mov")}>Movimentações</button>
        </div>

        <section className="historic-content">
          {activeTab === "vendas" && (
            <>
              <div className="estatisticas-rapidas">
                <div className="kpi-card">
                  <span>Receita no Período</span>
                  <strong>{formatCurrency(pedidosFiltradosVendas.reduce((s, p) => s + (p.total || 0), 0))}</strong>
                </div>
                <div className="kpi-card">
                  <span>Total de Vendas</span>
                  <strong>{pedidosFiltradosVendas.length}</strong>
                </div>
                <div className="kpi-card">
                  <span>Ticket Médio</span>
                  <strong>{formatCurrency(pedidosFiltradosVendas.length > 0 ? (pedidosFiltradosVendas.reduce((s, p) => s + (p.total || 0), 0) / pedidosFiltradosVendas.length) : 0)}</strong>
                </div>
              </div>

              <div className="historic-card">
                <div className="historic-card-title">
                  <h2>Resumo de Vendas Diárias</h2>
                  <select value={filtroData} onChange={(e) => setFiltroData(e.target.value)} className="filtro-select">
                    <option value="todos">Todos os períodos</option>
                    <option value="hoje">Hoje</option>
                    <option value="ontem">Ontem</option>
                    <option value="semana">Esta semana</option>
                    <option value="mes">Este mês</option>
                  </select>
                </div>
                <table className="historic-table">
                  <thead>
                    <tr><th>Data</th><th>Vendas</th><th>Total</th><th>Média</th></tr>
                  </thead>
                  <tbody>
                    {vendasAgrupadasPorData().map((v) => (
                      <tr key={v.data}>
                        <td>{v.data}</td>
                        <td>{v.quantidadeVendas}</td>
                        <td>{formatCurrency(v.totalVendas)}</td>
                        <td>{formatCurrency(v.quantidadeVendas > 0 ? v.totalVendas / v.quantidadeVendas : 0)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </>
          )}

          {activeTab === "formas" && (
            <div className="historic-card">
              <div className="historic-card-title">
                <h2>Distribuição de Pagamentos</h2>
                <select value={filtroData} onChange={(e) => setFiltroData(e.target.value)} className="filtro-select">
                  <option value="todos">Todos os períodos</option>
                  <option value="hoje">Hoje</option>
                  <option value="ontem">Ontem</option>
                </select>
              </div>
              <table className="historic-table">
                <thead>
                  <tr><th>Método</th><th>Valor</th><th>Qtd</th><th>%</th></tr>
                </thead>
                <tbody>
                  {estatisticasPagamento.formas.map((f) => (
                    <tr key={f.method}>
                      <td>{traduzirMetodoPagamento(f.method)}</td>
                      <td>{formatCurrency(f.amount)}</td>
                      <td>{f.quantity}</td>
                      <td>{f.percentual.toFixed(1)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className="grafico-pagamentos">
                {estatisticasPagamento.formas.map((f) => (
                  <div key={f.method} className="barra-container">
                    <div className="barra-info">
                      <span>{traduzirMetodoPagamento(f.method)}</span>
                      <span>{f.percentual.toFixed(1)}%</span>
                    </div>
                    <div className="barra">
                      <div className="barra-preenchimento" style={{ width: `${f.percentual}%` }}></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === "mov" && (
            <div className="historic-card">
              <h2>Fluxo de Caixa</h2>
              <table className="historic-table">
                <thead>
                  <tr><th>Data</th><th>Descrição</th><th>Tipo</th><th>Valor</th></tr>
                </thead>
                <tbody>
                  {movimentacoes.map((m) => (
                    <tr key={m.id}>
                      <td>{m.data}</td>
                      <td>{m.descricao}</td>
                      <td><span className={m.tipo}>{m.tipo.toUpperCase()}</span></td>
                      <td>{formatCurrency(m.valor)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}