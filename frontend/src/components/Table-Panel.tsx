import { FiEdit2, FiX } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import toast from 'react-hot-toast';

import { Table } from "../types/interfaces/table.interface";
import { Order_Item_Entity } from "../types/interfaces/orderItem.interface";
import { Flavor } from "../types/interfaces/flavor.interface"; // Certifique-se de importar a interface
import { fetchDeleteTable, fetchRemoveItemToOrder } from "../api";
import impressoraIcon from "../assets/icons/imprimir.png";
import trashIcon from "../assets/icons/lixo.png";
import porcentagemIcon from "../assets/icons/porcentagem-de-emblema.png";

interface TablePanelProps {
    mode: "TABLE" | "DELIVERY";
    id?: string;
    table?: Table;
    orderItemsToRender?: Order_Item_Entity[];
    allFlavors?: Flavor[]; // Adicionamos esta prop para traduzir IDs em Nomes
    handleOpenPayModal?: () => void;
    handleOpenPercentModal?: () => void;
    restante?: number;
    accrescimo_percent?: number;
    total: number;
    subtotal?: number;
    handleOpenEditName?: () => void;
    fetchTableData?: () => Promise<void>;
    handleRemoveItemOverride?: (item: Order_Item_Entity) => void;
}

export default function TablePanel({mode, fetchTableData, table,  orderItemsToRender , allFlavors = [], handleOpenPayModal, handleOpenPercentModal, restante, accrescimo_percent, total, subtotal, handleOpenEditName, handleRemoveItemOverride }: TablePanelProps) {
  const isTable = mode === "TABLE";
  const navigator = useNavigate();

  const getFlavorNames = (flavorIds: number[]) => {
    if (!flavorIds || flavorIds.length === 0) return null;
    return flavorIds
      .map(id => allFlavors.find(f => f.id === id)?.name)
      .filter(n => n)
      .join(", ");
  };

  const handleRemoveItemFromOrder = async (orderItem: Order_Item_Entity) => {
    if (handleRemoveItemOverride) {
      handleRemoveItemOverride(orderItem);
      return;
    }
    if (!table || !table.order) return;
    try {
      await fetchRemoveItemToOrder(orderItem.id); 
      if (mode === "TABLE") {                
        await fetchTableData(); 
      }
      toast.success("Item removido com sucesso!");
    } catch (error) {
      toast.error("Erro ao remover item.");
    }
  };

  const handleDeleteTable = async () => {
    if (!isTable) return;
    try {
      await fetchDeleteTable(table.id);
      navigator("/");
      toast.success("Mesa excluida")
    } catch (error) {
      toast.error("Erro ao deletar a mesa.");
    }
  };

  return (
    <>
      <section className="table-panel">
        <button className={`close-header-button ${isTable ? "on":""}`} onClick={() => navigator("/")}>
          <FiX />
        </button>
        <h2>
          {isTable ? table.name : "Delivery"}
          <span className={`edit-name-table-pen ${isTable ? "open" : ""}`} onClick={handleOpenEditName}>
            <FiEdit2 />
          </span>
        </h2>

        <div className="itens-order-container">
          {orderItemsToRender.length === 0 ? (
            <p style={{ textAlign: "center", color: "#888" }}>Nenhum Item Adicionado</p>
          ) : (
            orderItemsToRender.map((item: Order_Item_Entity) => (
              <div key={item.id} className="order-item-detail">
                <button className="remove-item-button" onClick={() => handleRemoveItemFromOrder(item)}>
                  <FiX size={18} />
                </button>

                <div className="item-info">
                  <div className="title-product-in-card">
                    <span>{item.quantity}x</span>
                    <p>{item.name} {item.size && `(${item.size})`}</p>
                  </div>

                  {/* EXIBIÇÃO DOS SABORES AQUI */}
            {item.flavorIds && item.flavorIds.length > 0 && (
  <p className="item-flavors" style={{ fontSize: "0.8rem", color: "#666", margin: "2px 0" }}>
    Sabores: {getFlavorNames(item.flavorIds)}
  </p>
)}

                  <span className="item-price">
                    R$ {(item.subtotal || 0).toFixed(2).replace(".", ",")}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>

        <div className="summary" >
          <p>Subtotal: <span style={{ float: "right" }}>R$ {subtotal.toFixed(2).replace(".", ",")}</span></p>
          <p>Acréscimo: <span className="highlight" style={{ float: "right" }}>{Math.round(accrescimo_percent * 100)}%</span></p>
          <p>Total: <span style={{ float: "right", fontWeight: "bold" }}>R$ {total.toFixed(2).replace(".", ",")}</span></p>
          <p>Restante: <span style={{ float: "right", fontWeight: "bold" }}>R$ {restante.toFixed(2).replace(".", ",")}</span></p>
        </div>

        <div className={`action-buttons ${isTable ? "open" : ""}`}>
          <button className="print"><img src={impressoraIcon} className="iconsAdctionButtons" /></button>
          <button className="delete" onClick={handleDeleteTable}><img src={trashIcon} className="iconsAdctionButtons" /></button>
          <button className="discount" onClick={handleOpenPercentModal}><img src={porcentagemIcon} className="iconsAdctionButtons" /></button>
        </div>

        <button className="add_pay" onClick={handleOpenPayModal}>
          {isTable ? "Adicionar pagamento" : "Adicionar pedido"}
        </button>
      </section>
    </>
  );
}