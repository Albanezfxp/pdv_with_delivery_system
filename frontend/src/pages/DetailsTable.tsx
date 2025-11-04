import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { FiEdit2, FiX } from "react-icons/fi";
import "../styles/DetailsTable.css";

// Importações de Tipos (assumindo que estão corretas)
import { Table } from "../types/interfaces/table.interface";
import {
  Order,
  Order_Item_Entity,
  OrderItem,
} from "../types/interfaces/orderItem.interface";
import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import { ProductVariation } from "../types/interfaces/productVariation.interface";

import impressoraIcon from "../assets/icons/imprimir.png";
import trashIcon from "../assets/icons/lixo.png";
import dolarIcon from "../assets/icons/dolar (1).png";
import porcentagemIcon from "../assets/icons/porcentagem-de-emblema.png";
import notFoundImgProduct from "../assets/not_found/images.png";

const AUTOMATIC_ACCRESCIMO_PERCENT = 0.1;

interface SelectedProductState {
  productName: string;
  variation: ProductVariation;
}

export default function DetailsTable() {
  const { id } = useParams<{ id: string }>();
  const navigator = useNavigate();

  const [table, setTable] = useState<Table | null>(null);
  const [categorys, setCategorys] = useState<Category[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(
    null
  );
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [selectedProduct, setSelectedProduct] =
    useState<SelectedProductState | null>(null);

  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>([]);
  const [isModalEditNameOpen, setIsModalEditNameOpen] = useState(false);
  const [isModalFlavorOpen, setIsModalFlavorOpen] = useState(false);
  
  // orderItemsExternal é o state que armazena os itens para renderização
  const [orderItemsExternal, setOrderItemsExternal] = useState<
    Order_Item_Entity[]
  >([]);

  const fetchTable = () => {
    if (!id) return;
    axios
      .get(`http://localhost:8080/table/${id}`)
      .then((res) => setTable(res.data))
      .catch((err) => console.error("Erro ao buscar a mesa", err));
  };

  // ⭐️ CORREÇÃO CRÍTICA: Adicionar o processamento da resposta para atualizar o estado
  const fetchOrderItemsExternal = () => {
    if (!id) return;
    axios
      .get(`http://localhost:8080/order/itens-table/${id}`)
      .then((res) => {
        // Assume que res.data é a lista de Order_Item_Entity[]
        setOrderItemsExternal(res.data);
      })
      .catch((err) =>
        console.error("Erro ao buscar itens (rota externa)", err)
      );
  };

  // Carrega dados estáticos e necessários ao montar o componente
  useEffect(() => {
    axios
      .get(`http://localhost:8080/categories`)
      .then((res) => setCategorys(res.data))
      .catch((err) => console.error("Erro ao buscar as categorias", err));
    axios
      .get(`http://localhost:8080/flavor`)
      .then((res) => setFlavors(res.data))
      .catch((err) => console.error("Erro ao buscar os sabores", err));
  }, []);

  // Recarrega dados da mesa e itens sempre que o ID mudar (ou na montagem inicial)
  useEffect(() => {
    fetchTable();
    fetchOrderItemsExternal(); 
  }, [id]); // Dependência em 'id'

  if (!table) return <p>Carregando...</p>;


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

  const handleSaveName = async () => {
    try {
      await axios.put(`http://localhost:8080/table/update-table/${table.id}`, {
        name: table.name,
      });
      alert("Nome atualizado com sucesso!");
      setIsModalEditNameOpen(false);
    } catch (error) {
      console.error("Erro ao atualizar o nome da mesa:", error);
      alert("Não foi possível atualizar o nome.");
    }
  };

  const handleDeleteTable = async () => {
    try {
      await axios.delete(`http://localhost:8080/table/${table.id}`);
      navigator("/");
    } catch (error) {
      console.error("Erro ao deletar a mesa:", error);
      alert("Não foi possível deletar a mesa.");
    }
  };

  const handleAddItemToOrder = async (
    variation: ProductVariation,
    productName: string,
    flavorIds: number[] = []
  ) => {
    if (!table) return;

    try {
      const payload = {
        productVariationId: variation.id,
        quantity: 1,
        flavorIds: flavorIds,
        // Mantive a lógica original, mas o backend é quem deve gerenciar complementos.
        complementIds: variation.complements?.map((c) => c.id) || [],
      };

      const res = await axios.post(
        `http://localhost:8080/order/add-item/${table.id}`,
        payload
      );

      // ⭐️ Simplificação: Após o POST, forçamos o refresh do painel de itens
      // Isso elimina a lógica de manipulação local do `table.order.items`.
      // Se a lógica do backend garante a consistência, esta é a forma mais segura.
      fetchTable();
      fetchOrderItemsExternal();
      
      // Feedback opcional para o usuário
      // alert("Item adicionado com sucesso!"); 

    } catch (error) {
      console.error("Erro ao adicionar item à mesa:", error);
      alert("Não foi possível adicionar o item.");
    }
  };

  const handleRemoveItemFromOrder = async (orderItem: Order_Item_Entity) => {
    if (!table || !table.order) return;

    // Remove o item localmente para dar feedback imediato (otimista)
    const originalItems = orderItemsExternal;
    const updatedItems = (originalItems || []).filter(
      (item) => item.id !== orderItem.id
    );
    setOrderItemsExternal(updatedItems);
    
    try {
      // A rota DELETE usa o ID do OrderItem, que é o correto
      await axios.delete(
        `http://localhost:8080/order/itens-table/${orderItem.id}`
      );

      // Após sucesso, sincroniza novamente (redundância segura)
      fetchTable();
      // fetchOrderItemsExternal() já foi atualizado localmente, mas podemos chamar
      // para garantir que o subtotal/total se recalcule com os dados mais recentes do DB.
      // Vou manter a chamada para sincronização completa.
      fetchOrderItemsExternal(); 

    } catch (error) {
      console.error("Erro ao remover item da mesa:", error);
      alert("Não foi possível remover o item. Revertendo a exclusão.");
      // Reverte o estado local em caso de falha na API
      setOrderItemsExternal(originalItems); 
    }
  };

  // Usamos orderItemsExternal para renderizar e calcular
  const orderItemsToRender: Order_Item_Entity[] = orderItemsExternal;

  const subtotal = orderItemsToRender.reduce(
    (acc, item) => acc + (item.subtotal || 0),
    0
  );
  
  // ... (cálculos de acrescimo e filtros de sabores mantidos)
  const acrescimo = subtotal * AUTOMATIC_ACCRESCIMO_PERCENT;
  const total = subtotal + acrescimo;
  const restante = total;

  const normalFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "NORMAL"
  );
  const sweetFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "CANDY"
  );
  const specialFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "ESPECIAL"
  );


  return (
    <>
      <div id="details-table-container">
        <section className="table-panel">
          <button className="close-modal-button" onClick={() => navigator("/")}>
            <FiX />
          </button>
          <h2>
            {table.name}
            <span onClick={handleOpenEditName} style={{ color: "#007bff" }}>
              <FiEdit2 />
            </span>
          </h2>

          {/* Renderização dos Itens do Pedido */}
          <div className="itens-order-container">
            {orderItemsToRender.length === 0 ? (
              <p style={{ textAlign: "center", color: "#888" }}>
                Nenhum Item Adicionado
              </p>
            ) : (
              orderItemsToRender.map((item: Order_Item_Entity) => (
                <div key={item.id} className="order-item-detail">
                  <button
                    className="remove-item-button"
                    onClick={() => handleRemoveItemFromOrder(item)}
                    title="Remover Item"
                  >
                    <FiX size={18} />
                  </button>

                  <div className="item-info">
                    <div className="title-product-in-card">
                      <span>{item.quantity}x</span>
                      {/* item.name e item.size devem vir preenchidos pelo backend (DTO) */}
                      <p>
                        {item.name} {item.size && `(${item.size})`}
                      </p>
                    </div>

                    {/* item.notes contém a lista de sabores e complementos, formatada pelo backend */}
                    {item.flavorIds?.length > 0 && (
                      <p className="item-flavors">
                        Sabores:{" "}
                        {item.notes.match(/\((.*?)\)/)?.[1] || "Não informado"}
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
          
          {/* ... Resumo e Botões de Ação mantidos ... */}
          <div className="summary">
            <p>
              Subtotal:{" "}
              <span style={{ float: "right" }}>
                R$ {subtotal.toFixed(2).replace(".", ",")}
              </span>
            </p>
            <p>
              Acréscimo automático:{" "}
              <span className="highlight" style={{ float: "right" }}>
                {Math.round(AUTOMATIC_ACCRESCIMO_PERCENT * 100)}%
              </span>
            </p>
            <p>
              Total:{" "}
              <span style={{ float: "right", fontWeight: "bold" }}>
                R$ {total.toFixed(2).replace(".", ",")}
              </span>
            </p>
            <p>
              Restante:{" "}
              <span style={{ float: "right", fontWeight: "bold" }}>
                R$ {restante.toFixed(2).replace(".", ",")}
              </span>
            </p>
          </div>

          <div className="action-buttons">
            <button className="print" onClick={() => alert("Imprimir conta")}>
              <img
                src={impressoraIcon}
                alt="impressora icon"
                className="iconsAdctionButtons"
              />
            </button>
            <button className="delete" onClick={handleDeleteTable}>
              <img
                src={trashIcon}
                alt="trash icon"
                className="iconsAdctionButtons"
              />
            </button>
            <button
              className="cash"
              onClick={() => alert("Pagamento em dinheiro")}
            >
              <img
                src={dolarIcon}
                alt="dolar icon"
                className="iconsAdctionButtons"
              />
            </button>
            <button
              className="discount"
              onClick={() => alert("Pagamento com desconto")}
            >
              <img
                src={porcentagemIcon}
                alt=""
                className="iconsAdctionButtons"
              />
            </button>
          </div>

          <button
            className="add_pay"
            onClick={() => alert("Adicionar Pagamento")}
          >
            Adicionar Pagamento
          </button>
        </section>

        {/* ... Seção de Produtos e Modals mantidas ... */}
        <section className="products-section">
          <div className="categories">
            {categorys.map((category) => (
              <div
                key={category.id}
                className="category-item"
                onClick={() => setSelectedCategory(category)}
                style={{ cursor: "pointer" }}
              >
                <img
                  src={category.imageUrl}
                  style={{
                    border:
                      selectedCategory?.id === category.id
                        ? "2px solid #e6b800"
                        : "none",
                  }}
                  alt={category.name}
                />
                <span>{category.name}</span>
              </div>
            ))}
          </div>

          <div className="products-grid">
            {selectedCategory ? (
              selectedCategory.products.flatMap((product) =>
                product.variations.map((variation) => (
                  <div
                    key={variation.id}
                    className="product-card"
                    onClick={() => {
                      if (variation.numberOfFlavor > 0) {
                        handleOpenFlavorModal(product.name, variation);
                      } else {
                        handleAddItemToOrder(variation, product.name);
                      }
                    }}
                  >
                    <img
                      src={product.imageUrl || notFoundImgProduct}
                      alt={product.name}
                    />
                    <h3>{`${product.name} ${variation.size}`}</h3>
                    {variation.numberOfFlavor > 0 && (
                      <p>Sabores: {variation.numberOfFlavor}</p>
                    )}
                    <span>
                      R$ {variation.price.toFixed(2).replace(".", ",")}
                    </span>
                  </div>
                ))
              )
            ) : (
              <p>Selecione uma categoria para ver os produtos</p>
            )}
          </div>
        </section>
      </div>
      {isModalEditNameOpen && (
        <div className="modal-Edit-Table-Name-Container">
          <div id="container-edit-modal">
            <button
              className="close-modal-button"
              onClick={handleCloseEditName}
            >
              <FiX />
            </button>
            <h3>Editar nome da mesa:</h3>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleSaveName();
              }}
            >
              <input
                type="text"
                value={table.name}
                onChange={(e) => setTable({ ...table, name: e.target.value })}
              />
              <button type="submit">Salvar</button>
            </form>
          </div>
        </div>
      )}

      {/* Modal de Sabores */}
      {isModalFlavorOpen && selectedProduct && (
        <div className="modal-flavor-Container">
          <div id="container-flavor-modal">
            <button
              className="close-modal-button"
              onClick={handleCloseFlavorModal}
            >
              <FiX />
            </button>
            <h3>Escolha os sabores para {selectedProduct.productName}</h3>
            <p className="p-title-modal-flavor">
              Selecione exatamente {selectedProduct.variation.numberOfFlavor}{" "}
              sabor(es)
            </p>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                const requiredFlavors =
                  selectedProduct.variation.numberOfFlavor;

                if (selectedFlavorIds.length === requiredFlavors) {
                  handleAddItemToOrder(
                    selectedProduct.variation,
                    selectedProduct.productName,
                    selectedFlavorIds
                  );
                  handleCloseFlavorModal();
                } else {
                  alert(
                    `Por favor, selecione exatamente ${requiredFlavors} sabor(es).`
                  );
                }
              }}
            >
              {/* Tradicionais */}
              {normalFlavors.length > 0 && (
                <>
                  <p
                    className="title-modal-type"
                    style={{ fontSize: "20px", fontWeight: "bold" }}
                  >
                    Tradicionais
                  </p>
                  <div className="flavors-grid">
                    {normalFlavors.map((flavor) => (
                      <label key={flavor.id} className="flavor-card">
                        <div>
                          <input
                            type="checkbox"
                            value={flavor.id}
                            checked={selectedFlavorIds.includes(flavor.id)}
                            onChange={() => {
                              const id = flavor.id;
                              const requiredFlavors =
                                selectedProduct.variation.numberOfFlavor;

                              if (selectedFlavorIds.includes(id)) {
                                setSelectedFlavorIds(
                                  selectedFlavorIds.filter((i) => i !== id)
                                );
                              } else if (
                                selectedFlavorIds.length < requiredFlavors
                              ) {
                                setSelectedFlavorIds([
                                  ...selectedFlavorIds,
                                  id,
                                ]);
                              }
                            }}
                          />
                          <span>{flavor.name}</span>
                        </div>
                        <p>{flavor.description}</p>
                      </label>
                    ))}
                  </div>
                </>
              )}

              {/* Doces */}
              {sweetFlavors.length > 0 && (
                <>
                  <p
                    className="title-modal-type"
                    style={{ fontSize: "20px", fontWeight: "bold" }}
                  >
                    Doces
                  </p>
                  <div className="flavors-grid">
                    {sweetFlavors.map((flavor) => (
                      <label key={flavor.id} className="flavor-card">
                        <div>
                          <input
                            type="checkbox"
                            value={flavor.id}
                            checked={selectedFlavorIds.includes(flavor.id)}
                            onChange={() => {
                              const id = flavor.id;
                              const requiredFlavors =
                                selectedProduct.variation.numberOfFlavor;

                              if (selectedFlavorIds.includes(id)) {
                                setSelectedFlavorIds(
                                  selectedFlavorIds.filter((i) => i !== id)
                                );
                              } else if (
                                selectedFlavorIds.length < requiredFlavors
                              ) {
                                setSelectedFlavorIds([
                                  ...selectedFlavorIds,
                                  id,
                                ]);
                              }
                            }}
                          />
                          <span>{flavor.name}</span>
                        </div>
                        <p>{flavor.description}</p>
                      </label>
                    ))}
                  </div>
                </>
              )}

              {/* Especiais */}
              {specialFlavors.length > 0 && (
                <>
                  <p
                    className="title-modal-type"
                    style={{ fontSize: "20px", fontWeight: "bold" }}
                  >
                    Especiais
                  </p>
                  <div className="flavors-grid">
                    {specialFlavors.map((flavor) => (
                      <label key={flavor.id} className="flavor-card">
                        <div>
                          <input
                            type="checkbox"
                            value={flavor.id}
                            checked={selectedFlavorIds.includes(flavor.id)}
                            onChange={() => {
                              const id = flavor.id;
                              const requiredFlavors =
                                selectedProduct.variation.numberOfFlavor;

                              if (selectedFlavorIds.includes(id)) {
                                setSelectedFlavorIds(
                                  selectedFlavorIds.filter((i) => i !== id)
                                );
                              } else if (
                                selectedFlavorIds.length < requiredFlavors
                              ) {
                                setSelectedFlavorIds([
                                  ...selectedFlavorIds,
                                  id,
                                ]);
                              }
                            }}
                          />
                          <span>{flavor.name}</span>
                        </div>
                        <p>{flavor.description}</p>
                      </label>
                    ))}
                  </div>
                </>
              )}

              <button
                type="submit"
                className="save-flavor-button"
                disabled={
                  selectedFlavorIds.length !==
                  selectedProduct.variation.numberOfFlavor
                }
              >
                Confirmar
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
}