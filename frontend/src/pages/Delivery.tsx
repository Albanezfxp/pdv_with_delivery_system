import React, { useEffect, useMemo, useState } from "react";
import "../styles/Delivery.css";

import Header from "../components/Header";
import toast from "react-hot-toast";
import { FiTrash2, FiX, FiFilter } from "react-icons/fi";

import { OrderStatus } from "../types/enums/orderStatus.enum";
import { ModalDeliveryOrderContainer, ModalFlavor } from "../components/modalGeral";

import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import { Order_Item_Entity } from "../types/interfaces/orderItem.interface";
import { ProductVariation } from "../types/interfaces/productVariation.interface";
import { SelectedProductState } from "../types/interfaces/SelectedProductState.interface";

import {
  fetchAddDelivery,
  fetchAllDeliverys,
  fetchCategories,
  fetchDeleteDeliveryOrder,
  fetchFlavores,
  fetchUpdateStatusOrderDelivery,
} from "../api";

import { OrderDeliveryRequest } from "../types/interfaces/orderDeliveryRequest.interface";
import { OrderDeliveryResponse } from "../types/interfaces/Delivery.interface";
import { DeliveryQuery } from "../types/interfaces/DeliveryQuery.interface";
import Loading from "../components/Loading";
import { PageResponse } from "../types/PageResponse.type";

type SortKey = "NEWEST" | "OLDEST" | "TOTAL_DESC" | "TOTAL_ASC";

// ✅ tipo da resposta paginada (Spring-like)


export default function DeliveryManager() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deliveryState, setDeliveryState] = useState<OrderStatus>(OrderStatus.PENDING);
  const [isModalFlavorOpen, setIsModalFlavorOpen] = useState(false);

  const [selectedProduct, setSelectedProduct] = useState<SelectedProductState | null>(null);
  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>([]);

  const [deliverys, setDeliverys] = useState<OrderDeliveryResponse[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);

  const [orderItemsExternal, setOrderItemsExternal] = useState<Order_Item_Entity[]>([]);

  // ✅ Detalhe do pedido
  const [selectedOrderId, setSelectedOrderId] = useState<number | null>(null);

  // ✅ FILTRO (drawer) — AGORA 100% LOCAL
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filterQuery, setFilterQuery] = useState("");
  const [filterTodayOnly, setFilterTodayOnly] = useState(false);
  const [filterPaymentMethod, setFilterPaymentMethod] = useState<string>("ALL");
  const [filterMinTotal, setFilterMinTotal] = useState<string>("");
  const [filterMaxTotal, setFilterMaxTotal] = useState<string>("");
  const [sortKey, setSortKey] = useState<SortKey>("NEWEST");

  // ✅ paginação/consulta server-side (mantida: paginação + ordenação)
  const [formPagination, setFormPagination] = useState<DeliveryQuery>({
    page: 0,
    size: 7,
    direction: "desc",
    directionParam: "createdAt",
    status: OrderStatus.PENDING,
    q: "",
    todayOnly: false,
    paymentMethod: "ALL",
    minTotal: undefined,
    maxTotal: undefined,
  });

  const [pageInfo, setPageInfo] = useState({
    totalPages: 0,
    totalElements: 0,
    page: 0,
    size: 10,
  });

  const [loading, setLoading] = useState(false);

  const selectedOrder = useMemo(() => {
    if (!selectedOrderId) return null;
    return deliverys.find((d) => d.id === selectedOrderId) ?? null;
  }, [deliverys, selectedOrderId]);

  const tabs = [
    { label: "Pendente", status: OrderStatus.PENDING },
    { label: "Preparando", status: OrderStatus.PREPARING },
    { label: "Em Rota", status: OrderStatus.ON_ROUTE },
    { label: "Concluídos", status: OrderStatus.PAYED },
  ];

  const parseMoney = (value: string) => {
    if (!value) return undefined;
    const cleaned = value.replace(",", ".").replace(/[^\d.]/g, "");
    const num = Number(cleaned);
    return Number.isFinite(num) ? num : undefined;
  };

  const applySortToQuery = (key: SortKey, prev: DeliveryQuery): DeliveryQuery => {
    switch (key) {
      case "NEWEST":
        return { ...prev, direction: "desc", directionParam: "createdAt", page: 0 };
      case "OLDEST":
        return { ...prev, direction: "asc", directionParam: "createdAt", page: 0 };
      case "TOTAL_DESC":
        return { ...prev, direction: "desc", directionParam: "total", page: 0 };
      case "TOTAL_ASC":
        return { ...prev, direction: "asc", directionParam: "total", page: 0 };
      default:
        return prev;
    }
  };

  // ✅ fetch server-side (mantido)
  const fetchDeliveryOrders = async () => {
    try {
      setLoading(true);

      const response = (await fetchAllDeliverys(formPagination)) as PageResponse<OrderDeliveryResponse>;

      setDeliverys(response.content ?? []);
      setPageInfo({
        totalPages: response.totalPages ?? 0,
        totalElements: response.totalElements ?? 0,
        page: response.number ?? formPagination.page,
        size: response.size ?? formPagination.size,
      });

      return response.content ?? [];
    } catch {
      toast.error("Erro ao buscar pedidos de delivery");
      setDeliverys([]);
      setPageInfo({ totalPages: 0, totalElements: 0, page: 0, size: formPagination.size });
      return [];
    } finally {
      setLoading(false);
    }
  };

  // ✅ carrega dados estáticos
  useEffect(() => {
    const loadStaticData = async () => {
      try {
        const [categoriesData, flavorsData] = await Promise.all([fetchCategories(), fetchFlavores()]);
        setCategories(categoriesData);
        setFlavors(flavorsData);
      } catch (err) {
        console.error("Erro ao buscar dados estáticos", err);
        toast.error("Erro ao carregar dados do delivery");
      }
    };

    loadStaticData();
  }, []);

  // ✅ sempre que a query (formPagination) mudar -> refetch
  useEffect(() => {
    fetchDeliveryOrders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formPagination]);

  // ✅ quando troca a aba, se o pedido selecionado não estiver nela, limpa detalhe
  useEffect(() => {
    if (!selectedOrder) return;
    if (selectedOrder.status !== deliveryState) setSelectedOrderId(null);
  }, [deliveryState]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleSelectOrderDetail = (id: number) => setSelectedOrderId(id);
  const handleCloseOrderDetail = () => setSelectedOrderId(null);

  const getFlavorNames = (flavorIds: number[]) => {
    if (!flavorIds || flavorIds.length === 0) return null;

    return flavorIds
      .map((id) => flavors.find((f) => f.id === id)?.name)
      .filter(Boolean)
      .join(", ");
  };

  const handleOpenFlavorModal = (productName: string, variation: ProductVariation) => {
    setSelectedProduct({ productName, variation });
    setSelectedFlavorIds([]);
    setIsModalFlavorOpen(true);
  };

  const handleCloseFlavorModal = () => {
    setIsModalFlavorOpen(false);
    setSelectedProduct(null);
    fetchDeliveryOrders();
  };

  function getProductNameFromNotes(notes: string, size?: string) {
    if (!notes) return "";
    let base = notes.split("(")[0].split("+")[0].trim();
    if (size) {
      const escapedSize = size.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
      base = base.replace(new RegExp(`\\s*${escapedSize}\\s*$`, "i"), "").trim();
    }
    return base;
  }

  const handleConfirmDelivery = (variation: ProductVariation, productName: string, flavorIds: number[]) => {
    const newItem: Order_Item_Entity = {
      id: Date.now(),
      name: productName,
      quantity: 1,
      size: variation.size,
      subtotal: variation.price,
      notes: "",
      flavorIds,
      orderId: 0,
      productVariationId: variation.id,
      complementIds: variation.complements?.map((c) => c.id) || [],
    };

    setOrderItemsExternal((prev) => [...prev, newItem]);
  };

  const handleAddDelivery = async (order: OrderDeliveryRequest) => {
    try {
      await fetchAddDelivery(order);
      setIsModalOpen(false);
      await fetchDeliveryOrders();
      toast.success("Pedido criado!");
    } catch {
      toast.error("Erro ao adicionar delivery");
    }
  };

  const handleDeleteDelivery = async (id?: number) => {
    if (!id) return;
    try {
      await fetchDeleteDeliveryOrder(id);
      await fetchDeliveryOrders();
      toast.success("Pedido deletado!");
      if (selectedOrderId === id) setSelectedOrderId(null);
    } catch {
      toast.error("Erro em deletar delivery");
    }
  };

  const handleUpdateStatus = async (status: OrderStatus, id: number) => {
    try {
      await fetchUpdateStatusOrderDelivery(status, String(id));
      await fetchDeliveryOrders();
    } catch {
      toast.error("Erro ao atualizar status");
    }
  };

  // ✅ stats (página atual)
  const deliveryOrdersToday = useMemo(() => {
    const now = new Date();
    return deliverys.filter((order) => {
      const created = new Date(order.createdAt);
      const diffMs = now.getTime() - created.getTime();
      if (Number.isNaN(created.getTime()) || diffMs < 0) return false;
      const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
      return days < 1;
    });
  }, [deliverys]);

  const formatOrderTimer = (createdAt: string) => {
    const created = new Date(createdAt);
    const now = new Date();

    const diffMs = now.getTime() - created.getTime();
    if (Number.isNaN(created.getTime()) || diffMs < 0) return "agora";

    const totalSeconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `há ${days}d`;
    if (hours > 0) return `há ${hours}h`;
    if (minutes > 0) return `há ${minutes}min`;
    return `há ${totalSeconds}s`;
  };

  const delivery_pending = useMemo(() => deliverys.filter((order) => order.status === OrderStatus.PENDING), [deliverys]);
  const delivery_preparing = useMemo(() => deliverys.filter((order) => order.status === OrderStatus.PREPARING), [deliverys]);
  const delivery_on_route = useMemo(() => deliverys.filter((order) => order.status === OrderStatus.ON_ROUTE), [deliverys]);
  const delivery_finish = useMemo(() => deliverys.filter((order) => order.status === OrderStatus.PAYED), [deliverys]);

  const paymentsLabel = (order: OrderDeliveryResponse) => {
    if (order.payments.length === 0) return `Não informado`;
    const methods = order.payments.map((p) => p.method).filter(Boolean);
    return methods.length ? methods.join(", ") : "Não informado";
  };

  const copyToClipboard = async (text?: string) => {
    if (!text) return;
    try {
      await navigator.clipboard.writeText(text);
      toast.success("Copiado!");
    } catch {
      toast.error("Não foi possível copiar");
    }
  };

  const openWhatsApp = (phone?: string) => {
    if (!phone) return;
    const digits = phone.replace(/\D/g, "");
    window.open(`https://wa.me/55${digits}`, "_blank", "noopener,noreferrer");
  };

  // ============================
  // ✅ FILTER + SORT
  // ✅ Ordenação segue server-side (mantida)
  // ✅ Filtros agora são LOCAIS (na página atual)
  // ============================

  const paymentMethods = useMemo(() => {
    const set = new Set<string>();
    deliverys.forEach((o) => o.payments?.forEach((p) => p.method && set.add(String(p.method))));
    return Array.from(set).sort((a, b) => a.localeCompare(b));
  }, [deliverys]);

  const activeFilterCount = useMemo(() => {
    let c = 0;
    if (filterQuery.trim()) c++;
    if (filterTodayOnly) c++;
    if (filterPaymentMethod !== "ALL") c++;
    if (filterMinTotal.trim()) c++;
    if (filterMaxTotal.trim()) c++;
    return c;
  }, [filterQuery, filterTodayOnly, filterPaymentMethod, filterMinTotal, filterMaxTotal]);

  const clearFilters = () => {
    setFilterQuery("");
    setFilterTodayOnly(false);
    setFilterPaymentMethod("ALL");
    setFilterMinTotal("");
    setFilterMaxTotal("");
    // ✅ não mexe em paginação nem ordenação
  };

  const applyFilters = () => {
    // ✅ filtros são locais, então só fecha o drawer
    // ✅ ordenação permanece como está (você ainda pode trocar sortKey e aplicar ordenação server-side abaixo se quiser)
    setIsFilterOpen(false);

    // ✅ Se você quiser manter o "Aplicar" também aplicando SOMENTE a ordenação (server-side),
    // sem mexer em filtros no backend:
    setFormPagination((prev) => applySortToQuery(sortKey, prev));
  };

  // ✅ paginação UI (mantida)
  const canPrev = pageInfo.page > 0;
  const canNext = pageInfo.page + 1 < pageInfo.totalPages;

  const prevPage = () => setFormPagination((p) => ({ ...p, page: Math.max(0, (p.page ?? 0) - 1) }));
  const nextPage = () => setFormPagination((p) => ({ ...p, page: (p.page ?? 0) + 1 }));

  // ✅ FILTRO LOCAL (aplicado na lista da página atual)
  const ordersToRender = useMemo(() => {
    const q = filterQuery.trim().toLowerCase();
    const min = parseMoney(filterMinTotal);
    const max = parseMoney(filterMaxTotal);

    const isToday = (createdAt: string) => {
      const created = new Date(createdAt);
      const now = new Date();
      const diffMs = now.getTime() - created.getTime();
      if (Number.isNaN(created.getTime()) || diffMs < 0) return false;
      const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
      return days < 1;
    };

    const textIncludes = (value?: string) => (value ?? "").toLowerCase().includes(q);

    return deliverys
      .filter((o) => o.status === deliveryState)
      .filter((o) => {
        // ✅ somente hoje
        if (filterTodayOnly && !isToday(o.createdAt)) return false;

        // ✅ pagamento
        if (filterPaymentMethod !== "ALL") {
          const has = (o.payments ?? []).some((p) => String(p.method) === String(filterPaymentMethod));
          if (!has) return false;
        }

        // ✅ total min/max
        const total = Number(o.total ?? 0);
        if (typeof min === "number" && total < min) return false;
        if (typeof max === "number" && total > max) return false;

        // ✅ busca textual
        if (!q) return true;

        const client = o.client;
        const endereco = client?.endereco;

        const inClient =
          textIncludes(client?.name) ||
          textIncludes(client?.phone) ||
          textIncludes(endereco?.street) ||
          textIncludes(endereco?.neighborhood) ||
          textIncludes(endereco?.city) ||
          textIncludes(endereco?.reference) ||
          textIncludes(String(endereco?.number ?? ""));

        const inItems = (o.items ?? []).some((it) => textIncludes(it.notes) || textIncludes(it.productVariation?.size));

        return inClient || inItems || textIncludes(String(o.id));
      });
  }, [
    deliverys,
    deliveryState,
    filterQuery,
    filterTodayOnly,
    filterPaymentMethod,
    filterMinTotal,
    filterMaxTotal,
  ]);

  return (
    <div className="dm-main-wrapper">
      <Header />

      <div className="dm-dashboard-container">
        {/* ✅ SIDEBAR: DETALHE */}
        <aside className="dm-sidebar-panel dd-sidebar">
          <div className="dd-sidebar-header">
            <div className="dd-sidebar-titlewrap">
              <span className="dd-sidebar-eyebrow">Delivery</span>
              {selectedOrder ? <h2 className="dd-sidebar-title">Detalhe do pedido</h2> : <h2 className="dd-sidebar-title">Controle do Delivery</h2>}

              {selectedOrder ? (
                <span
                  className={[
                    "dd-status-badge",
                    selectedOrder.status === OrderStatus.PENDING && "dd-status-pending",
                    selectedOrder.status === OrderStatus.PREPARING && "dd-status-preparing",
                    selectedOrder.status === OrderStatus.ON_ROUTE && "dd-status-onroute",
                    selectedOrder.status === OrderStatus.PAYED && "dd-status-finish",
                  ]
                    .filter(Boolean)
                    .join(" ")}
                >
                  {tabs.find((t) => t.status === selectedOrder.status)?.label ?? selectedOrder.status}
                </span>
              ) : null}
            </div>

            <div className="dd-sidebar-btn-header">
              <button
                className={`dd-sidebar-trash-btn ${selectedOrder ? "open" : ""}`}
                onClick={() => handleDeleteDelivery(selectedOrder?.id)}
                aria-label="Deletar"
                type="button"
                title="Deletar pedido"
              >
                <FiTrash2 />
              </button>

              <button
                className={`dd-sidebar-close-btn ${selectedOrder ? "open" : ""}`}
                onClick={handleCloseOrderDetail}
                aria-label="Fechar"
                type="button"
                title="Fechar detalhe"
              >
                <FiX />
              </button>
            </div>
          </div>

          {!selectedOrder ? (
            <div className="dd-empty">
              <div className="dd-empty-icon">📦</div>
              <h3>Selecione um pedido</h3>
              <p>Clique em um card para ver os detalhes aqui.</p>

              <div style={{ marginTop: 14, textAlign: "left" }}>
                <div className="dm-stats-summary" style={{ borderTop: "none", paddingTop: 0 }}>
                  <p className="dm-stat-line">
                    Pedidos Hoje (página): <span className="dm-stat-bold">{deliveryOrdersToday.length}</span>
                  </p>
                  <p className="dm-stat-line">
                    Aguardando (página): <span className="dm-stat-warning">{delivery_pending.length}</span>
                  </p>
                  <p className="dm-stat-line">
                    Preparando (página): <span className="dm-stat-preparing">{delivery_preparing.length}</span>
                  </p>
                  <p className="dm-stat-line">
                    Em Rota (página): <span className="dm-stat-in_rounter">{delivery_on_route.length}</span>
                  </p>
                  <p className="dm-stat-line">
                    Concluídos (página): <span className="dm-stat-info">{delivery_finish.length}</span>
                  </p>

                  <p className="dm-stat-line" style={{ marginTop: 10, opacity: 0.8 }}>
                    Página {pageInfo.page + 1} de {Math.max(1, pageInfo.totalPages)} • {pageInfo.totalElements} pedidos
                  </p>
                </div>
              </div>
            </div>
          ) : (
            <div className="dd-content">
              <div className="dd-top">
                <div className="dd-idblock">
                  <span className="dd-chip">#{selectedOrder.id}</span>
                  <span className="dd-time">{formatOrderTimer(selectedOrder.createdAt)}</span>
                </div>

                <div className="dd-kpis">
                  <div className="dd-kpi">
                    <span className="dd-kpi-label">Total</span>
                    <strong className="dd-kpi-value">R$ {Number(selectedOrder.total ?? 0).toFixed(2)}</strong>
                  </div>
                  <div className="dd-kpi">
                    <span className="dd-kpi-label">Pagamento</span>
                    <strong className="dd-kpi-value">{paymentsLabel(selectedOrder)}</strong>
                  </div>
                </div>
              </div>

              <div className="dd-section">
                <h4 className="dd-section-title">Cliente</h4>
                <div className="dd-card">
                  <div className="dd-row">
                    <span className="dd-label">Nome</span>
                    <span className="dd-value">{selectedOrder.client?.name ?? "N/A"}</span>
                  </div>
                  <div className="dd-row">
                    <span className="dd-label">Telefone</span>
                    <span className="dd-value">{selectedOrder.client?.phone ?? "N/A"}</span>
                  </div>

                  <div className="dd-actions-inline">
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={() => copyToClipboard(selectedOrder.client?.phone)}>
                      Copiar telefone
                    </button>
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={() => openWhatsApp(selectedOrder.client?.phone)}>
                      WhatsApp
                    </button>
                  </div>
                </div>
              </div>

              <div className="dd-section">
                <h4 className="dd-section-title">Endereço</h4>
                <div className="dd-card">
                  <div className="dd-row">
                    <span className="dd-label">Rua</span>
                    <span className="dd-value">
                      {selectedOrder.client?.endereco?.street ?? "N/A"}, {selectedOrder.client?.endereco?.number ?? "s/n"}
                    </span>
                  </div>
                  <div className="dd-row">
                    <span className="dd-label">Bairro</span>
                    <span className="dd-value">{selectedOrder.client?.endereco?.neighborhood ?? "N/A"}</span>
                  </div>
                  <div className="dd-row">
                    <span className="dd-label">Cidade</span>
                    <span className="dd-value">{selectedOrder.client?.endereco?.city ?? "N/A"}</span>
                  </div>
                  <div className="dd-row">
                    <span className="dd-label">Referência</span>
                    <span className="dd-value">{selectedOrder.client?.endereco?.reference ?? "N/A"}</span>
                  </div>

                  <button className="dd-btn dd-btn-primary" type="button" onClick={() => toast("Mapa: implementar")}>
                    Ver no mapa
                  </button>
                </div>
              </div>

              <div className="dd-section">
                <h4 className="dd-section-title">Itens</h4>

                <div className="dd-items">
                  {selectedOrder.items?.length ? (
                    selectedOrder.items.map((item) => {
                      const itemName = getProductNameFromNotes(item.notes, item.productVariation.size);
                      const itemSize = item.productVariation?.size ?? "";
                      const flavorsText = item.flavors?.length ? item.flavors.map((f) => f.name).join(", ") : "—";

                      return (
                        <div className="dd-item" key={item.id}>
                          <div className="dd-item-left">
                            <span className="dd-item-qty">{item.quantity}x</span>
                            <div className="dd-item-info">
                              <p className="dd-item-name">
                                {itemName} {itemSize}
                              </p>
                              <p className="dd-item-sub">Sabores: {flavorsText}</p>
                            </div>
                          </div>
                          <span className="dd-item-price">R$ {Number(item.subtotal ?? 0).toFixed(2)}</span>
                        </div>
                      );
                    })
                  ) : (
                    <div className="dd-card">
                      <span className="dd-label">Sem itens</span>
                    </div>
                  )}
                </div>

                <div className="dd-totals">
                  <div className="dd-row">
                    <span className="dd-label">Subtotal</span>
                    <span className="dd-value">R$ {Number(selectedOrder.subtotal ?? 0).toFixed(2)}</span>
                  </div>
                  <div className="dd-row">
                    <span className="dd-label">Frete</span>
                    <span className="dd-value">R$ {Number((selectedOrder.total ?? 0) - (selectedOrder.subtotal ?? 0)).toFixed(2)}</span>
                  </div>
                  <div className="dd-row dd-row-strong">
                    <span className="dd-label">Total</span>
                    <span className="dd-value">R$ {Number(selectedOrder.total ?? 0).toFixed(2)}</span>
                  </div>
                </div>
              </div>

              <div className="dd-footer">
                {selectedOrder.status === OrderStatus.PENDING && (
                  <>
                    <button className="dd-btn dd-btn-secondary" type="button" onClick={() => handleUpdateStatus(OrderStatus.PREPARING, selectedOrder.id)}>
                      Aceitar
                    </button>
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={handleCloseOrderDetail}>
                      Fechar
                    </button>
                  </>
                )}

                {selectedOrder.status === OrderStatus.PREPARING && (
                  <>
                    <button className="dd-btn dd-btn-secondary" type="button" onClick={() => handleUpdateStatus(OrderStatus.ON_ROUTE, selectedOrder.id)}>
                      Despachar
                    </button>
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={handleCloseOrderDetail}>
                      Fechar
                    </button>
                  </>
                )}

                {selectedOrder.status === OrderStatus.ON_ROUTE && (
                  <>
                    <button className="dd-btn dd-btn-secondary" type="button" onClick={() => handleUpdateStatus(OrderStatus.PAYED, selectedOrder.id)}>
                      Finalizar
                    </button>
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={handleCloseOrderDetail}>
                      Fechar
                    </button>
                  </>
                )}

                {selectedOrder.status === OrderStatus.PAYED && (
                  <>
                    <button className="dd-btn dd-btn-primary" type="button" onClick={handleCloseOrderDetail}>
                      Ok
                    </button>
                    <button className="dd-btn dd-btn-ghost" type="button" onClick={() => toast("Imprimir: implementar")}>
                      Imprimir
                    </button>
                  </>
                )}
              </div>
            </div>
          )}
        </aside>

        {/* ✅ LISTA/GRID */}
        <main className="dm-content-section">
          <div className="dm-filter-tabs">
            <div className="dm-new-order-wrapper" onClick={() => setIsModalOpen(true)}>
              <div className="dm-tab-item new-order">
                <button className="dm-btn-primary" type="button">
                  Novo Pedido
                </button>
              </div>
            </div>

            {tabs.map((tab) => (
              <div
                key={tab.status}
                className={`dm-tab-item ${deliveryState === tab.status ? "dm-active" : ""}`}
                onClick={() => {
                  setSelectedOrderId(null);
                  setDeliveryState(tab.status);

                  setFormPagination((prev) => ({
                    ...prev,
                    status: tab.status,
                    page: 0,
                  }));
                }}
                role="button"
                tabIndex={0}
              >
                {tab.label}
              </div>
            ))}
          </div>

          <div className="dm-orders-flex">
            {/* ✅ Botão do filtro */}
            <button
              className={`dm-filter-btn ${isFilterOpen ? "open" : ""}`}
              type="button"
              onClick={() => setIsFilterOpen((v) => !v)}
              aria-label="Abrir filtros"
            >
              <FiFilter />
              <span>Filtro</span>
              {activeFilterCount > 0 && <span className="dm-filter-badge">{activeFilterCount}</span>}
            </button>

            {/* ✅ Drawer sempre montado (para animar abrir/fechar) */}
            <div className={`dm-filter-backdrop ${isFilterOpen ? "open" : ""}`} onClick={() => setIsFilterOpen(false)} />

            <div className={`dm-filter-drawer ${isFilterOpen ? "open" : ""}`} role="dialog" aria-label="Filtros" aria-hidden={!isFilterOpen}>
              <div className="dm-filter-drawer-header">
                <div>
                  <h3>Filtros</h3>
                  <p>Refina os pedidos desta aba (local)</p>
                </div>
                <button className="dm-filter-close" type="button" onClick={() => setIsFilterOpen(false)}>
                  <FiX />
                </button>
              </div>

              <div className="dm-filter-grid">
                <div className="dm-filter-field">
                  <label>Buscar</label>
                  <input value={filterQuery} onChange={(e) => setFilterQuery(e.target.value)} placeholder="Nome, telefone, rua, itens..." />
                </div>

                <div className="dm-filter-field">
                  <label>Pagamento</label>
                  <select value={filterPaymentMethod} onChange={(e) => setFilterPaymentMethod(e.target.value)}>
                    <option value="ALL">Todos</option>
                    {paymentMethods.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="dm-filter-field">
                  <label>Total mínimo</label>
                  <input value={filterMinTotal} onChange={(e) => setFilterMinTotal(e.target.value)} placeholder="Ex: 25.00" inputMode="decimal" />
                </div>

                <div className="dm-filter-field">
                  <label>Total máximo</label>
                  <input value={filterMaxTotal} onChange={(e) => setFilterMaxTotal(e.target.value)} placeholder="Ex: 120.00" inputMode="decimal" />
                </div>

                <div className="dm-filter-field dm-filter-inline">
                  <label className="dm-check">
                    <input type="checkbox" checked={filterTodayOnly} onChange={(e) => setFilterTodayOnly(e.target.checked)} />
                    <span>Somente hoje</span>
                  </label>
                </div>

                <div className="dm-filter-field">
                  <label>Ordenar por</label>
                  <select value={sortKey} onChange={(e) => setSortKey(e.target.value as SortKey)}>
                    <option value="NEWEST">Mais recentes</option>
                    <option value="OLDEST">Mais antigos</option>
                    <option value="TOTAL_DESC">Maior total</option>
                    <option value="TOTAL_ASC">Menor total</option>
                  </select>
                </div>
              </div>

              <div className="dm-filter-actions">
                <button className="dm-filter-secondary" type="button" onClick={clearFilters}>
                  Limpar
                </button>
                <button className="dm-filter-primary" type="button" onClick={applyFilters}>
                  Aplicar
                </button>
              </div>
            </div>

            {/* ✅ Cards */}
            {!loading && ordersToRender.length === 0 ? (
              <div className="dm-empty-state">
                <h3 className="dm-empty-title">Nenhum pedido por aqui 😶</h3>
                <p className="dm-empty-subtitle">
                  Não existem pedidos na sessão <strong>{tabs.find((t) => t.status === deliveryState)?.label}</strong>
                  {activeFilterCount > 0 ? " com os filtros atuais." : "."}
                </p>

                <div className="dm-empty-actions">
                  <button className="dm-btn-primary-empty" onClick={() => setIsModalOpen(true)} type="button">
                    Criar novo pedido
                  </button>

                  {activeFilterCount > 0 && (
                    <button className="dm-filter-secondary" onClick={clearFilters} type="button">
                      Limpar filtros
                    </button>
                  )}
                </div>
              </div>
            ) : (
              ordersToRender.map((order) => (
                <div
                  key={order.id}
                  className={`dm-order-card dm-status-${order.status}`}
                  onClick={() => handleSelectOrderDetail(order.id)}
                  role="button"
                  tabIndex={0}
                  title="Ver detalhes"
                  style={{
                    outline: selectedOrderId === order.id ? "3px solid var(--dd-ring)" : "transparent",
                  }}
                >
                  <div className="dm-card-header">
                    <span className="dm-order-id">#{order.id}</span>
                    <span className="dm-order-timer">{formatOrderTimer(order.createdAt)}</span>
                  </div>

                  <h3 className="dm-customer-name">{order.client?.name ?? "Sem nome"}</h3>

                  <p className="dm-customer-address">
                    {order.client?.endereco?.street ? order.client.endereco.street : "N/A"}, {order.client?.endereco?.number ?? "s/n"}
                  </p>

                  <div className="dm-items-pill-container">{order.items?.length ? order.items.map((i) => i.notes).join(", ") : "Sem itens"}</div>

                  <div className="dm-card-footer">
                    <span className="dm-order-total">R$ {Number(order.total ?? 0).toFixed(2)}</span>

                    <div className="dm-action-group">
                      {order.status === OrderStatus.PENDING && (
                        <button
                          className="dm-btn-accept"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleUpdateStatus(OrderStatus.PREPARING, order.id);
                          }}
                          type="button"
                        >
                          Aceitar
                        </button>
                      )}

                      {order.status === OrderStatus.PREPARING && (
                        <button
                          className="dm-btn-dispatch"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleUpdateStatus(OrderStatus.ON_ROUTE, order.id);
                          }}
                          type="button"
                        >
                          Despachar
                        </button>
                      )}

                      {order.status === OrderStatus.ON_ROUTE && (
                        <button
                          className="dm-btn-on_route"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleUpdateStatus(OrderStatus.PAYED, order.id);
                          }}
                          type="button"
                        >
                          Finalizar
                        </button>
                      )}

                      {order.status === OrderStatus.PAYED && (
                        <button disabled className="dm-btn-finish" type="button">
                          Pago
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
            <div
              className={`btn-pagination open`}
              style={{
                width: "100%",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginTop: 12,
              }}
            >
              <div style={{ display: "flex", gap: 8 }}>
                <button className="dm-filter-secondary" type="button" onClick={prevPage} disabled={!canPrev || loading}>
                  Anterior
                </button>
                <button className="dm-filter-primary" type="button" onClick={nextPage} disabled={!canNext || loading}>
                  Próxima
                </button>
              </div>
            </div>{" "}
          </div>
        </main>

        {/* ✅ MODAIS */}
        {isModalFlavorOpen && selectedProduct && (
          <ModalFlavor
            flavors={flavors}
            handleCloseModal={handleCloseFlavorModal}
            selectedProduct={selectedProduct}
            mode="DELIVERY"
            onConfirmDelivery={handleConfirmDelivery}
          />
        )}

        <ModalDeliveryOrderContainer
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          categories={categories}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
          orderItemsExternal={orderItemsExternal}
          setOrderItemsExternal={setOrderItemsExternal}
          handleOpenFlavorModal={handleOpenFlavorModal}
          getFlavorNames={getFlavorNames}
          handleAddDelivery={handleAddDelivery}
          fetchDeliveryOrders={fetchDeliveryOrders}
        />
      </div>
    </div>
  );
}