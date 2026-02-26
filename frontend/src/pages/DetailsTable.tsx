import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";

import "../styles/DetailsTable.css";

import { Table } from "../types/interfaces/table.interface";
import { Order_Item_Entity } from "../types/interfaces/orderItem.interface";
import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import { ProductVariation } from "../types/interfaces/productVariation.interface";
import { SelectedProductState } from "../types/interfaces/SelectedProductState.interface";

import {
  fetchAddItemToOrder,
  fetchCategories,
  fetchCofirmPayment,
  fetchDeleteTable,
  fetchEditTableName,
  fetchFlavores,
  fetchOrderItemsExternal,
  fetchRemoveItemToOrder,
  fetchTable
} from "../api";

import ProductSection from "../components/Product-section";
import TablePanel from "../components/Table-Panel";
import Loading from "../components/Loading";
import MainContent from "../components/MainContent";
import Header from "../components/Header";


import { useAccrescimo } from "../context/AccrescimoProvider";
import { ModalEditName, ModalFlavor, ModalPay, ModalPercent } from "../components/modalGeral";

const PAYMENT_METHODS = ["DINHEIRO", "DEBITO", "CREDITO", "PIX"];

export interface PaymentEntry {
  id: number;
  method: string;
  amount: number;
}

export default function DetailsTable() {
  const { id } = useParams<{ id: string }>();

  const [table, setTable] = useState<Table | null>(null);
  const [categorys, setCategorys] = useState<Category[]>([]);
  const [selectedCategory, setSelectedCategory] =
    useState<Category | null>(null);

  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [selectedProduct, setSelectedProduct] =
    useState<SelectedProductState | null>(null);
  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>([]);

  const [isModalEditNameOpen, setIsModalEditNameOpen] = useState(false);
  const [isModalFlavorOpen, setIsModalFlavorOpen] = useState(false);
  const [isModalPayOpen, setIsModalPayOpen] = useState(false);
  const [isModalPercentOpen, setIsModalPercentOpen] = useState(false);

  const [paymentEntries, setPaymentEntries] = useState<PaymentEntry[]>([]);

  const [orderItemsExternal, setOrderItemsExternal] = useState<
    Order_Item_Entity[]
  >([]);

  const { accrescimo, setAccrescimo } = useAccrescimo();

  const fetchOrderItems = useCallback(
    async (currentTable: Table | null, currentId: string) => {
      const orderId = currentTable?.order?.id || currentId;
      if (!orderId) return;

      try {
        const data = await fetchOrderItemsExternal(orderId);
        setOrderItemsExternal(data);
      } catch (err) {
        console.error("Erro ao buscar itens (API)", err);
        toast.error("ERRO AO BUSCAR ITENS (API)");
      }
    },
    []
  );

  const fetchTableData = useCallback(async () => {
    if (!id) return;

    try {
      const data = await fetchTable(id);
      setTable(data);
      await fetchOrderItems(data, id);
    } catch (err) {
      console.error("Erro ao buscar a mesa", err);
    }
  }, [id, fetchOrderItems]);

  useEffect(() => {
    const loadStaticData = async () => {
      try {
        const categories = await fetchCategories();
        setCategorys(categories);

        const flavorsData = await fetchFlavores();
        setFlavors(flavorsData);
      } catch (err) {
        console.error("Erro ao buscar dados estáticos", err);
      }
    };

    loadStaticData();
  }, []);

  useEffect(() => {
    fetchTableData();
  }, [fetchTableData]);

  if (!table) {
    return (
      <div id="details-table-container">
        <div className="loading-details-table-container">
          <MainContent>
            <div className="loading-wrapper">
              <Loading text="Carregando..." />
            </div>
          </MainContent>
        </div>
      </div>
    );
  }

  const handleOpenEditName = () => setIsModalEditNameOpen(true);
  const handleCloseEditName = () => setIsModalEditNameOpen(false);

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

  const handleOpenPayModal = () => {
    setIsModalPayOpen(true);
    setPaymentEntries([
      {
        id: Date.now(),
        method: PAYMENT_METHODS[0],
        amount: total
      }
    ]);
  };

  const handleClosePayModal = () => setIsModalPayOpen(false);

  const handleOpenPercentModal = () => setIsModalPercentOpen(true);
  const handleClosePercentModal = () => setIsModalPercentOpen(false);

  const orderItemsToRender: Order_Item_Entity[] = orderItemsExternal;

  const subtotal = orderItemsToRender.reduce(
    (acc, item) => acc + (item.subtotal || 0),
    0
  );

  const acrescimo = subtotal * accrescimo;
  const total = subtotal + acrescimo;
  const restante = total;

  const totalPaid = paymentEntries.reduce(
    (acc, entry) => acc + entry.amount,
    0
  );

  const change = totalPaid > total ? totalPaid - total : 0;

  const handleChangeAccrescimo = (newPercent: number) => {
    setAccrescimo(newPercent / 100);
    setIsModalPercentOpen(false);
  };

  return (
    <>
      <div id="details-table-container">
   <TablePanel
  mode="TABLE"
  fetchTableData={fetchTableData}
  id={id}
  accrescimo_percent={accrescimo}
  handleOpenEditName={handleOpenEditName}
  handleOpenPayModal={handleOpenPayModal}
  handleOpenPercentModal={handleOpenPercentModal}
  orderItemsToRender={orderItemsToRender}
  allFlavors={flavors} // <--- ADICIONE ESTA LINHA AQUI
  restante={restante}
  subtotal={subtotal}
  table={table}
  total={total}
/>

        <ProductSection
          mode="TABLE"
          categorys={categorys}
          fetchTableData={fetchTableData}
          handleOpenFlavorModal={handleOpenFlavorModal}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
          table={table}
        />
      </div>

      {isModalEditNameOpen && (
        <ModalEditName
          currentTable={table}
          setTable={setTable}
          handleCloseModal={handleCloseEditName}
        />
      )}

      {isModalPercentOpen && (
        <ModalPercent
          handleClosePercentModal={handleClosePercentModal}
          onSavePercent={handleChangeAccrescimo}
          currentPercent={accrescimo * 100}
        />
      )}

      {isModalFlavorOpen && selectedProduct && (
        <ModalFlavor
          fetchTableData={fetchTableData}
          flavors={flavors}
          handleCloseModal={handleCloseFlavorModal}
          table={table}
          key={table.id}
          selectedProduct={selectedProduct}
          mode="TABLE"
        />
      )}

      {isModalPayOpen && (
        <ModalPay
          acrescimo={acrescimo}
          subtotal={subtotal}
          handleCloseModal={handleClosePayModal}
          paymentMethods={PAYMENT_METHODS}
          table={table}
          total={total}
          totalPaid={totalPaid}
          key={table.id}
        />
      )}
    </>
  );
}
