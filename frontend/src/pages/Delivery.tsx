import React, { useEffect, useState } from "react";
import "../styles/Delivery.css";

import Header from "../components/Header";

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
  fetchFlavores,
  fetchUpdateStatusOrderDelivery,
} from "../api";

import { OrderDeliveryRequest } from "../types/interfaces/orderDeliveryRequest.interface";
import { OrderDeliveryResponse } from "../types/interfaces/Delivery.interface";
import toast from "react-hot-toast";

export default function DeliveryManager() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deliveryState, setDeliveryState] = useState<OrderStatus>(OrderStatus.PENDING);
  const [isModalFlavorOpen, setIsModalFlavorOpen] = useState(false);

  const [selectedProduct, setSelectedProduct] = useState<SelectedProductState | null>(null);
  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>();

  const [deliverys, setDeliverys] = useState<OrderDeliveryResponse[]>([]);

  const [categories, setCategories] = useState<Category[]>([]);
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);

  const [orderItemsExternal, setOrderItemsExternal] = useState<Order_Item_Entity[]>([]);

  const tabs = [
    { label: "Pendente", status: OrderStatus.PENDING },
    { label: "Preparando", status: OrderStatus.PREPARING },
    { label: "Em Rota", status: OrderStatus.ON_ROUTE },
    { label: "Concluídos", status: OrderStatus.PAYED },
  ];

  const fetchDeliveryOrders = async () => {
    try {
      const response = await fetchAllDeliverys();
      setDeliverys(response);
      return response;
    } catch (error) {
      toast.error("Erro em buscar mesas");
    }
  };

  useEffect(() => {
    const loadStaticData = async () => {
      try {
        const categoriesData = await fetchCategories();
        setCategories(categoriesData);

        const flavorsData = await fetchFlavores();
        setFlavors(flavorsData);

        const deliveryOrdersData = await fetchAllDeliverys();
        setDeliverys(deliveryOrdersData);
      } catch (err) {
        console.error("Erro ao buscar dados estáticos", err);
      }
    };

    loadStaticData();
  }, []);

  const getFlavorNames = (flavorIds: number[]) => {
    if (!flavorIds || flavorIds.length === 0) return null;

    return flavorIds
      .map((id) => flavors.find((f) => f.id === id)?.name)
      .filter((n) => n)
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

  const handleConfirmDelivery = (
    variation: ProductVariation,
    productName: string,
    flavorIds: number[],
  ) => {
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
      fetchDeliveryOrders();
    } catch {
      toast.error("Erro em adicionar delivery");
    }
  };

  const handleButtonStatusOrderDelivery = async (status: OrderStatus, id: string) => {
    try {
      await fetchUpdateStatusOrderDelivery(status, id);
      fetchDeliveryOrders();
    } catch (error) {
      toast.error("Erro em atualizar status");
    }
  };

  const deliveryOrdersToday = deliverys.filter((order) => {
  const created = new Date(order.createdAt);
  const now = new Date();

  const diffMs = now.getTime() - created.getTime();
  if (Number.isNaN(created.getTime()) || diffMs < 0) return false;

  const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  return days < 1;
});

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

  const order_render = deliverys.filter((o) => o.status === deliveryState);
  const delivery_peding = deliverys.filter(order => order.status === OrderStatus.PENDING);
  const delivery_preparing = deliverys.filter(order => order.status === OrderStatus.PREPARING);
  const delivery_in_rounter = deliverys.filter(order => order.status === OrderStatus.ON_ROUTE);
  const delivery_finish = deliverys.filter(order => order.status === OrderStatus.PAYED);
  
  return (
    <div className="dm-main-wrapper">
      <Header />

      <div className="dm-dashboard-container">
        <aside className="dm-sidebar-panel">
          <h2 className="dm-sidebar-title">Delivery Control</h2>

          <div className="dm-stats-summary">
            <p className="dm-stat-line">
              Pedidos Hoje: <span className="dm-stat-bold">{deliveryOrdersToday.length}</span>
            </p>
            <p className="dm-stat-line">
              Aguardando: <span className="dm-stat-warning">{delivery_peding.length}</span>
            </p>
            <p className="dm-stat-line">
              Preparando: <span className="dm-stat-preparing">{delivery_preparing.length}</span>
            </p>
            <p className="dm-stat-line">
              Em Rota: <span className="dm-stat-in_rounter">{delivery_in_rounter.length}</span>
            </p>
            <p className="dm-stat-line">
              Concluídos: <span className="dm-stat-info">{delivery_finish.length}</span>
            </p>
          </div>

          <div className="dm-sidebar-actions">
            <button className="dm-btn-history">Histórico de Hoje</button>
            <button className="dm-btn-report">Relatório de Entregas</button>
          </div>
        </aside>

        <main className="dm-content-section">
          <div className="dm-filter-tabs">
            <div className="dm-new-order-wrapper" onClick={() => setIsModalOpen(true)}>
              <div className="dm-tab-item new-order">
                <button className="dm-btn-primary">Novo Pedido</button>
              </div>
            </div>

            {tabs.map((tab) => (
              <div
                key={tab.status}
                className={`dm-tab-item ${deliveryState === tab.status ? "dm-active" : ""}`}
                onClick={() => setDeliveryState(tab.status)}
              >
                {tab.label}
              </div>
            ))}
          </div>

          <div className="dm-orders-grid">
            {order_render.length === 0 ? (
              <div className="dm-empty-state">
                <h3 className="dm-empty-title">Nenhum pedido por aqui 😶</h3>
                <p className="dm-empty-subtitle">
                  Não existem pedidos na sessão <strong>{tabs.find((t) => t.status === deliveryState)?.label}</strong>.
                </p>
                <button className="dm-btn-primary" onClick={() => setIsModalOpen(true)}>
                  Criar novo pedido
                </button>
              </div>
            ) : (
              order_render.map((order) => (
                <div key={order.id} className={`dm-order-card dm-status-${order.status}`}>
                  <div className="dm-card-header">
                    <span className="dm-order-id">#{order.id}</span>
                    <span className="dm-order-timer">{formatOrderTimer(order.createdAt)}</span>
                  </div>

                  <h3 className="dm-customer-name">{order.client?.name ?? "Sem nome"}</h3>

                  <p className="dm-customer-address">
                    {order.client?.endereco?.street ? order.client.endereco.street : "N/A"},{" "}
                    {order.client?.endereco?.number ?? "s/n"}
                  </p>

                  <div className="dm-items-pill-container">
                    {order.items?.length ? order.items.map((i) => i.notes).join(", ") : "Sem itens"}
                  </div>

                  <div className="dm-card-footer">
                    <span className="dm-order-total">R$ {Number(order.total).toFixed(2)}</span>

                    <div className="dm-action-group">
                      {order.status === OrderStatus.PENDING && (
                        <button
                          className="dm-btn-accept"
                          onClick={() =>
                            handleButtonStatusOrderDelivery(OrderStatus.PREPARING, order.id.toString())
                          }
                        >
                          Aceitar
                        </button>
                      )}

                      {order.status === OrderStatus.PREPARING && (
                        <button
                          className="dm-btn-dispatch"
                          onClick={() =>
                            handleButtonStatusOrderDelivery(OrderStatus.ON_ROUTE, order.id.toString())
                          }
                        >
                          Despachar
                        </button>
                      )}

                      {order.status === OrderStatus.ON_ROUTE && (
                        <button
                          className="dm-btn-on_route"
                          onClick={() =>
                            handleButtonStatusOrderDelivery(OrderStatus.PAYED, order.id.toString())
                          }
                        >
                          Finalizar
                        </button>
                      )}

                      {order.status === OrderStatus.PAYED && (
                        <button disabled className="dm-btn-finish">
                          Pago
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </main>

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