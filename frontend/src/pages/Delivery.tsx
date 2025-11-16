import React, { useCallback, useEffect, useState } from 'react';
import '../styles/Delivery.css';

import Header from '../components/Header';

import { OrderStatus } from '../types/enums/orderStatus.enum';
import {
  ModalAddDeliveryOrder,
  ModalDeliveryOrderContainer,
  ModalFlavor
} from '../components/modalGeral';

import { Category } from '../types/interfaces/category.interface';
import { Flavor } from '../types/interfaces/flavor.interface';
import { Order_Item_Entity } from '../types/interfaces/orderItem.interface';
import { ProductVariation } from '../types/interfaces/productVariation.interface';
import { SelectedProductState } from '../types/interfaces/SelectedProductState.interface';

import { fetchCategories, fetchFlavores, fetchTable } from '../api';

export default function DeliveryManager() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deliveryState, setDeliveryState] = useState<OrderStatus>(
    OrderStatus.PENDING
  );
  const [isModalFlavorOpen, setIsModalFlavorOpen] = useState(false);

  const [selectedProduct, setSelectedProduct] =
    useState<SelectedProductState | null>(null);

  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>();

  const [orders, setOrders] = useState([
    {
      id: '1050',
      customer: 'João Silva',
      address: 'Rua das Flores, 123',
      items: ['Pizza Calabresa', 'Coca-Cola 2L'],
      total: 57.9,
      status: OrderStatus.PENDING,
      time: '10 min atrás'
    },
    {
      id: '1051',
      customer: 'Maria Souza',
      address: 'Av. Principal, 450 - Ap 12',
      items: ['X-Burger', 'Batata Frita'],
      total: 42.0,
      status: OrderStatus.PREPARING,
      time: '25 min atrás'
    },
    {
      id: '1052',
      customer: 'Maria Eduarda',
      address: 'Av. Principal, 450 - Ap 12',
      items: ['X-Burger', 'Batata Frita'],
      total: 42.0,
      status: OrderStatus.ON_ROUTE,
      time: '25 min atrás'
    },
    {
      id: '1053',
      customer: 'Marieta',
      address: 'Av. Principal, 450 - Ap 12',
      items: ['X-Burger', 'Batata Frita'],
      total: 42.0,
      status: OrderStatus.PAYED_IN_DELIVERY,
      time: '25 min atrás'
    }
  ]);

  const [categories, setCategories] = useState<Category[]>([]);
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [selectedCategory, setSelectedCategory] =
    useState<Category | null>(null);

  const [orderItemsExternal, setOrderItemsExternal] = useState<
    Order_Item_Entity[]
  >([]);

  const handleOpenFlavorModal = (
    productName: string,
    variation: ProductVariation
  ) => {
    setSelectedProduct({ productName, variation });
    setSelectedFlavorIds([]);
    setIsModalFlavorOpen(true);
  };

  const handleCloseFlavorModal = () => {
    setIsModalFlavorOpen(false);
    setSelectedProduct(null);
  };

  const tabs = [
    { label: 'Pendente', status: OrderStatus.PENDING },
    { label: 'Preparando', status: OrderStatus.PREPARING },
    { label: 'Em Rota', status: OrderStatus.ON_ROUTE },
    { label: 'Concluídos', status: OrderStatus.PAYED_IN_DELIVERY }
  ];

  const order_render = orders.filter(
    (o) => o.status === deliveryState
  );

  useEffect(() => {
    const loadStaticData = async () => {
      try {
        const categories = await fetchCategories();
        setCategories(categories);

        const flavorsData = await fetchFlavores();
        setFlavors(flavorsData);
      } catch (err) {
        console.error('Erro ao buscar dados estáticos', err);
      }
    };

    loadStaticData();
  }, []);

  return (
    <div className="dm-main-wrapper">
      <Header />

      <div className="dm-dashboard-container">
        <aside className="dm-sidebar-panel">
          <h2 className="dm-sidebar-title">Delivery Control</h2>

          <div className="dm-stats-summary">
            <p className="dm-stat-line">
              Pedidos Hoje: <span className="dm-stat-bold">12</span>
            </p>
            <p className="dm-stat-line">
              Aguardando: <span className="dm-stat-warning">5</span>
            </p>
            <p className="dm-stat-line">
              Em Rota: <span className="dm-stat-info">3</span>
            </p>
          </div>

          <div className="dm-sidebar-actions">
            <button className="dm-btn-history">
              Histórico de Hoje
            </button>
            <button className="dm-btn-report">
              Relatório de Entregas
            </button>
          </div>
        </aside>

        <main className="dm-content-section">
          <div className="dm-filter-tabs">
            <div
              className="dm-new-order-wrapper"
              onClick={() => setIsModalOpen(true)}
            >
              <div className="dm-tab-item new-order">
                <button className="dm-btn-primary">
                  Novo Pedido
                </button>
              </div>
            </div>

            {tabs.map((tab) => (
              <div
                key={tab.status}
                className={`dm-tab-item ${
                  deliveryState === tab.status
                    ? 'dm-active'
                    : ''
                }`}
                onClick={() => setDeliveryState(tab.status)}
              >
                {tab.label}
              </div>
            ))}
          </div>

          <div className="dm-orders-grid">
            {order_render.map((order) => (
              <div
                key={order.id}
                className={`dm-order-card dm-status-${order.status}`}
              >
                <div className="dm-card-header">
                  <span className="dm-order-id">
                    #{order.id}
                  </span>
                  <span className="dm-order-timer">
                    {order.time}
                  </span>
                </div>

                <h3 className="dm-customer-name">
                  {order.customer}
                </h3>
                <p className="dm-customer-address">
                  {order.address}
                </p>

                <div className="dm-items-pill-container">
                  {order.items.join(', ')}
                </div>

                <div className="dm-card-footer">
                  <span className="dm-order-total">
                    R$ {order.total.toFixed(2)}
                  </span>

                  <div className="dm-action-group">
                    {order.status === 'PENDING' && (
                      <button className="dm-btn-accept">
                        Aceitar
                      </button>
                    )}

                    {order.status === 'PREPARING' && (
                      <button className="dm-btn-dispatch">
                        Despachar
                      </button>
                    )}

                    {order.status === 'ON_ROUTE' && (
                      <button className="dm-btn-on_route">
                        Finalizar
                      </button>
                    )}

                    {order.status === OrderStatus.PAYED && (
                      <button
                        disabled
                        className="dm-btn-finish"
                      >
                        Pago
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </main>

        {isModalFlavorOpen && selectedProduct && (
          <ModalFlavor
            flavors={flavors}
            handleCloseModal={handleCloseFlavorModal}
            selectedProduct={selectedProduct}
          />
        )}

        <ModalDeliveryOrderContainer
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          categories={categories}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
          orderItemsExternal={orderItemsExternal}
          handleOpenFlavorModal={handleOpenFlavorModal}
        />
      </div>
    </div>
  );
}
