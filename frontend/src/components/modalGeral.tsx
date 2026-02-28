import React, { useEffect, useMemo, useState } from "react";
import "../styles/ModalAddProduct.css";
import "../styles/DeliveryModal.css";
import {
  fetchAddDelivery,
  fetchAddItemToOrder,
  fetchCategories,
  fetchCofirmPayment,
  fetchEditTableName,
  fetchFlavores,
} from "../api";
import toast from "react-hot-toast";
import { FiPlus, FiSearch, FiTrash2, FiX } from "react-icons/fi";
import { ProductVariation } from "../types/interfaces/productVariation.interface";
import { useNavigate } from "react-router-dom";
import { PaymentEntry } from "../pages/DetailsTable";
import { ModalAddProductProps } from "../types/interfaces/ModalAddProduct.interface";
import { ModalEditProps } from "../types/interfaces/ModalEditProps.interface";
import { modalFlavorProps } from "../types/interfaces/modalFlavorProps.inteface";
import { ModalPercentProps } from "../types/interfaces/ModalPercentProps.interface";
import { modalPayProps } from "../types/interfaces/modalPayProps.interface";
import { ModalAddCategoryProps } from "../types/interfaces/ModalAddCategory.interface";
import { OrderType } from "../types/enums/orderType.enum";
import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import TablePanel from "./Table-Panel";
import ProductSection from "./Product-section";
import { Order_Item_Entity } from "../types/interfaces/orderItem.interface";
import { createPortal } from "react-dom";
import { SelectedProductState } from "../types/interfaces/SelectedProductState.interface";
import cep from "cep-promise";
import { OrderDeliveryRequest } from "../types/interfaces/orderDeliveryRequest.interface";

type CEP = {
  cep: string;
  street: string;
  neighborhood: string;
  city: string;
  state: string;
};

export enum PaymentMethod {
  PIX = "PIX",
  CREDITO = "CREDITO",
  DEBITO = "DEBITO",
  DINHEIRO = "DINHEIRO",
}

interface ModalDeliveryOrderContainerProps {
  isOpen: boolean;
  onClose: () => void;
  handleAddDelivery: (order: OrderDeliveryRequest) => void;
  categories: Category[];
  selectedCategory: Category | null;
  setSelectedCategory: (category: Category) => void;

  /** ✅ VEM DO PAI (DeliveryManager) */
  orderItemsExternal: Order_Item_Entity[];
  /** ✅ SETTER DO PAI (fonte única de verdade) */
  setOrderItemsExternal: React.Dispatch<
    React.SetStateAction<Order_Item_Entity[]>
  >;

  handleOpenFlavorModal: (
    productName: string,
    variation: ProductVariation,
  ) => void;

  /** opcional: se você quiser resolver nomes de sabores por fora */
  getFlavorNames?: (flavorIds: number[]) => string;
}

const PAYMENT_METHODS = ["PIX", "CREDITO", "DEBITO", "DINHEIRO"] as const;
type PaymentMethodValue = (typeof PAYMENT_METHODS)[number];

interface ModalDeliveryOrderContainerProps {
  isOpen: boolean;
  onClose: () => void;

  categories: Category[];
  selectedCategory: Category | null;
  setSelectedCategory: (category: Category) => void;

  orderItemsExternal: Order_Item_Entity[];
  setOrderItemsExternal: React.Dispatch<
    React.SetStateAction<Order_Item_Entity[]>
  >;

  handleOpenFlavorModal: (
    productName: string,
    variation: ProductVariation,
  ) => void;

  getFlavorNames?: (flavorIds: number[]) => string;
  fetchDeliveryOrders: () => void
}

export const ModalDeliveryOrderContainer = ({
  isOpen,
  onClose,
  categories,
  selectedCategory,
  setSelectedCategory,
  orderItemsExternal,
  setOrderItemsExternal,
  handleOpenFlavorModal,
  getFlavorNames,
  fetchDeliveryOrders
}: ModalDeliveryOrderContainerProps) => {
  const [cepData, setCepData] = useState<CEP | null>(null);

  const [customer, setCustomer] = useState({
    name: "",
    phone: "",
    email: "",
    address: {
      street: "",
      cep: "",
      number: "",
      neighborhood: "",
      reference: "",
      city: "",
    },
  });

  // ✅ novo: forma de pagamento
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethodValue>("PIX");

  // ⚠️ IMPORTANTÍSSIMO: hooks sempre no topo, antes de qualquer return condicional

  useEffect(() => {
    if (!isOpen) {
      setCustomer({
        name: "",
        phone: "",
        email: "",
        address: {
          street: "",
          cep: "",
          number: "",
          neighborhood: "",
          reference: "",
          city: "",
        },
      });
      setCepData(null);
      setPaymentMethod("PIX");
    }
  }, [isOpen]);

  const frete = 1;

  const subtotal = useMemo(() => {
    return orderItemsExternal.reduce(
      (acc, item) => acc + (item.subtotal || 0),
      0,
    );
  }, [orderItemsExternal]);

  const total = subtotal + frete;

  const handleAddItemLocal = (
    variation: ProductVariation,
    productName: string,
  ) => {
    const newItem: Order_Item_Entity = {
      id: Date.now(),
      name: productName,
      quantity: 1,
      size: variation.size,
      subtotal: variation.price,
      notes: "",
      flavorIds: [],
      orderId: 0,
      productVariationId: variation.id,
      complementIds: variation.complements?.map((c) => c.id) || [],
    };

    setOrderItemsExternal((prev) => [...prev, newItem]);
  };

  const handleRemoveItemLocal = (item: Order_Item_Entity) => {
    setOrderItemsExternal((prev) => prev.filter((i) => i.id !== item.id));
  };

  const resolveFlavorNames = (ids: number[]) => {
    if (getFlavorNames) return getFlavorNames(ids);
    return ids.join(", ");
  };

  const validateDelivery = () => {
    if (!customer.name.trim()) return "Informe o nome do cliente.";
    if (!customer.phone.trim()) return "Informe o telefone do cliente.";
    if (!customer.address.cep.trim()) return "Informe o CEP.";
    if (!customer.address.street.trim()) return "Informe a rua/logradouro.";
    if (!customer.address.number.trim()) return "Informe o número.";
    if (!customer.address.neighborhood.trim()) return "Informe o bairro.";
    if (!customer.address.city.trim()) return "Informe a cidade.";
    if (orderItemsExternal.length === 0)
      return "Adicione pelo menos 1 item ao pedido.";
    return null;
  };

  const handleSaveDelivery = async () => {
    const error = validateDelivery();
    if (error) {
      toast.error(error);
      return;
    }

    const payload: OrderDeliveryRequest = {
  cliente_name: customer.name,
  cliente_phone: customer.phone,
  cliente_email: customer.email || undefined,
  cliente_endereco: {
    cep: customer.address.cep,
    street: customer.address.street,
    neighborhood: customer.address.neighborhood,
    city: customer.address.city,
    reference: customer.address.reference,
    number: customer.address.number,
  },
  items: orderItemsExternal,
  type: OrderType.DELIVERY,
  subtotal,
  total, // (recomendo mandar também)
  paymentEntries: [
    {
      method: paymentMethod, // <- usa o select
      amount: total,
    },
  ],
};

    try {

      await fetchAddDelivery(payload)

      toast.success("Delivery criado!");
      onClose();
      setOrderItemsExternal([]);
      fetchDeliveryOrders();
    } catch (err) {
      console.error(err);
      toast.error("Erro ao criar delivery.");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="dm-modal-overlay" onClick={onClose}>
      <div
        className="dm-modal-container dm-modal-large"
        onClick={(e) => e.stopPropagation()}
      >
        <header className="dm-modal-header">
          <h2>Novo Pedido – Delivery</h2>
          <button
            className="dm-close-btn"
            onClick={onClose}
            aria-label="Fechar"
            type="button"
          >
            <FiX />
          </button>
        </header>

        <div className="delivery-modal-content">
          {/* ESQUERDA */}
          <div className="dm-TablePanel-container">
            <div className="dm-tablePanel-fixed">
              <div className="dm-tablePanel-title">
                <h2>Delivery</h2>
              </div>

              <div className="itens-order-container">
                {orderItemsExternal.length === 0 ? (
                  <p style={{ textAlign: "center", color: "#888" }}>
                    Nenhum Item Adicionado
                  </p>
                ) : (
                  orderItemsExternal.map((item) => (
                    <div key={item.id} className="order-item-detail">
                      <button
                        className="remove-item-button"
                        onClick={() => handleRemoveItemLocal(item)}
                        type="button"
                      >
                        <FiX size={18} />
                      </button>

                      <div className="item-info">
                        <div className="title-product-in-card">
                          <span>{item.quantity}x</span>
                          <p>
                            {item.name} {item.size && `(${item.size})`}
                          </p>
                        </div>

                        {item.flavorIds?.length > 0 && (
                          <p className="item-flavors">
                            Sabores: {resolveFlavorNames(item.flavorIds)}
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

              <div style={{ padding: "10px 0" }}>
                <p style={{ margin: 0, color: "#475569", fontSize: 12 }}>
                  Subtotal
                </p>
                <p style={{ margin: "4px 0 10px", fontWeight: 700 }}>
                  R$ {subtotal.toFixed(2).replace(".", ",")}
                </p>
                <p style={{ margin: 0, color: "#475569", fontSize: 12 }}>
                  Frete
                </p>
                <p style={{ margin: "4px 0", fontWeight: 800, fontSize: 18 }}>
                  R$ {frete.toFixed(2).replace(".", ",")}
                </p>
                   <p style={{ margin: 0, color: "#475569", fontSize: 12 }}>
                  Total
                </p>
                <p style={{ margin: "4px 0", fontWeight: 800, fontSize: 18 }}>
                  R$ {total.toFixed(2).replace(".", ",")}
                </p>
              </div>
            </div>

            <div className="dm-clientInfo-container">
              <div className="dm-clientInfo-header">
                <h2>Dados para Entrega</h2>
              </div>

              <div className="dm-clientInfo-form">
                <div className="dm-input-group flex-3">
                  <label>Nome Completo</label>
                  <input
                    type="text"
                    placeholder="Nome do cliente"
                    value={customer.name}
                    onChange={(e) =>
                      setCustomer({ ...customer, name: e.target.value })
                    }
                  />
                </div>

                <div className="dm-input-row">
                  <div className="dm-input-group flex-2">
                    <label>Telefone</label>
                    <input
                      type="text"
                      placeholder="(00) 00000-0000"
                      value={customer.phone}
                      onChange={(e) =>
                        setCustomer({ ...customer, phone: e.target.value })
                      }
                    />
                  </div>

                </div>
                  <div className="dm-input-group flex-2">
                    <label>Email (opcional)</label>
                    <input
                      type="email"
                      placeholder="email@exemplo.com"
                      value={customer.email}
                      onChange={(e) =>
                        setCustomer({ ...customer, email: e.target.value })
                      }
                    />
                  </div>

                {/* ✅ novo: forma de pagamento */}
                <div className="dm-input-group">
                  <label>Forma de pagamento</label>
                  <select
                    value={paymentMethod}
                    onChange={(e) =>
                      setPaymentMethod(e.target.value as PaymentMethodValue)
                    }
                  >
                    {PAYMENT_METHODS.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="dm-input-row">
                  <div className="dm-input-group flex-2">
                    <label>CEP</label>

                    <div className="dm-campo-cep">
                      <input
                        type="text"
                        placeholder="Buscar endereço pelo CEP"
                        value={customer.address.cep}
                        onChange={(e) =>
                          setCustomer({
                            ...customer,
                            address: {
                              ...customer.address,
                              cep: e.target.value,
                            },
                          })
                        }
                      />

                      <button
                        className="dm-btn-cep"
                        type="button"
                        aria-label="Buscar CEP"
                        onClick={async () => {
                          try {
                            const addr = (await cep(
                              customer.address.cep,
                            )) as CEP;
                            setCepData(addr);

                            setCustomer((prev) => ({
                              ...prev,
                              address: {
                                ...prev.address,
                                street: addr.street ?? prev.address.street,
                                neighborhood:
                                  addr.neighborhood ??
                                  prev.address.neighborhood,
                                city: addr.city ?? prev.address.city,
                              },
                            }));
                          } catch (err) {
                            console.error(err);
                            toast.error("CEP inválido ou não encontrado.");
                          }
                        }}
                      >
                        <FiSearch />
                      </button>
                    </div>

                    {/* opcional: debug / feedback */}
                    {cepData?.state && (
                      <small style={{ color: "#64748b" }}>
                        {cepData.city} - {cepData.state}
                      </small>
                    )}
                  </div>
                </div>

                <div className="dm-input-row">
                  <div className="dm-input-group flex-3">
                    <label>Rua / Logradouro</label>
                    <input
                      type="text"
                      value={customer.address.street}
                      onChange={(e) =>
                        setCustomer({
                          ...customer,
                          address: {
                            ...customer.address,
                            street: e.target.value,
                          },
                        })
                      }
                    />
                  </div>

                  <div className="dm-input-group flex-1">
                    <label>Nº</label>
                    <input
                      type="text"
                      value={customer.address.number}
                      onChange={(e) =>
                        setCustomer({
                          ...customer,
                          address: {
                            ...customer.address,
                            number: e.target.value,
                          },
                        })
                      }
                    />
                  </div>
                </div>

                <div className="dm-input-row">
                  <div className="dm-input-group flex-2">
                    <label>Bairro</label>
                    <input
                      type="text"
                      placeholder="Ex: Centro"
                      value={customer.address.neighborhood}
                      onChange={(e) =>
                        setCustomer({
                          ...customer,
                          address: {
                            ...customer.address,
                            neighborhood: e.target.value,
                          },
                        })
                      }
                    />
                  </div>

                  <div className="dm-input-group flex-2">
                    <label>Cidade</label>
                    <input
                      type="text"
                      placeholder="Ex: João Pessoa"
                      value={customer.address.city}
                      onChange={(e) =>
                        setCustomer({
                          ...customer,
                          address: {
                            ...customer.address,
                            city: e.target.value,
                          },
                        })
                      }
                    />
                  </div>
                </div>

                <div className="dm-input-group">
                  <label>Referência</label>
                  <input
                    type="text"
                    placeholder="Ex: Casa verde, portão preto..."
                    value={customer.address.reference}
                    onChange={(e) =>
                      setCustomer({
                        ...customer,
                        address: {
                          ...customer.address,
                          reference: e.target.value,
                        },
                      })
                    }
                  />
                </div>
              </div>
            </div>

            <div className="dm_butto_save">
              <button
                className="add_pay"
                type="button"
                onClick={handleSaveDelivery}
              >
                Adicionar Delivery
              </button>
            </div>
          </div>

          {/* DIREITA */}
          <div className="dm-ProductSection-container">
            <ProductSection
              mode="DELIVERY"
              categorys={categories}
              selectedCategory={selectedCategory}
              setSelectedCategory={setSelectedCategory}
              handleAddItemOverride={handleAddItemLocal}
              handleOpenFlavorModal={handleOpenFlavorModal}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
export const ModalAddDeliveryOrder = ({ isOpen, onClose, onSave }: any) => {
  const [customer, setCustomer] = useState({
    name: "",
    phone: "",
    address: "",
  });
  const [items, setItems] = useState([
    { id: Date.now(), name: "", qty: 1, price: 0 },
  ]);

  if (!isOpen) return null;

  const addItem = () =>
    setItems([...items, { id: Date.now(), name: "", qty: 1, price: 0 }]);

  const removeItem = (id: number) => {
    if (items.length > 1) setItems(items.filter((i) => i.id !== id));
  };

  const total = items.reduce((acc, item) => acc + item.qty * item.price, 0);

  return (
    <div className="dm-modal-overlay">
      <div className="dm-modal-container">
        <header className="dm-modal-header">
          <div>
            <h2>Novo Pedido de Delivery</h2>
            <p>Preencha os dados para iniciar a entrega</p>
          </div>
          <button className="dm-close-btn" onClick={onClose}>
            <FiX />
          </button>
        </header>

        <form className="dm-modal-form" onSubmit={(e) => e.preventDefault()}>
          {/* SEÇÃO CLIENTE */}
          <section className="dm-form-section">
            <div className="dm-section-title">
              <span className="dm-step-number">1</span>
              <h3>Dados do Cliente</h3>
            </div>
            <div className="dm-input-row">
              <div className="dm-input-group flex-2">
                <label>Telefone</label>
                <div className="dm-input-with-icon">
                  <FiSearch />
                  <input type="text" placeholder="(00) 00000-0000" />
                </div>
              </div>
              <div className="dm-input-group flex-3">
                <label>Nome Completo</label>
                <input type="text" placeholder="Nome do cliente" />
              </div>
            </div>
            <div className="dm-input-group">
              <label>Endereço de Entrega</label>
              <input
                type="text"
                placeholder="Rua, número, bairro e complemento"
              />
            </div>
          </section>
          <footer className="dm-modal-footer">
            <div className="dm-total-display">
              <span>Total do Pedido</span>
              <strong>R$ {total.toFixed(2)}</strong>
            </div>
            <div className="dm-footer-btns">
              <button type="button" className="dm-btn-cancel" onClick={onClose}>
                Cancelar
              </button>
              <button type="submit" className="dm-btn-save">
                Criar Pedido
              </button>
            </div>
          </footer>
        </form>
      </div>
    </div>
  );
};
export const ModalAddProduct = ({
  isOpen,
  onClose,
  categories,
  onSave,
}: ModalAddProductProps) => {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    categoryId: "",
    imageUrl: "",
    active: true,
  });

  const [variations, setVariations] = useState([
    { size: "", price: 0, numberOfFlavor: 1, stock: null },
  ]);

  //Verifica se a categoria é bebidas
  const isBeverage = categories
    .find((cat) => cat.id === Number(formData.categoryId))
    ?.name.toLowerCase()
    .includes("bebida");

  //Se for, numero de sabores é igual a 0 que pode ser escolhido
  useEffect(() => {
    if (isBeverage) {
      setVariations((prev) => prev.map((v) => ({ ...v, numberOfFlavor: 0 })));
    }
  }, [formData.categoryId, isBeverage]);

  if (!isOpen) return null;

  // Função para formatar o número como Moeda Brasileira para exibição
  const formatToBRL = (value: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(value);
  };

  const handlePriceChange = (index: number, rawValue: string) => {
    // Remove tudo que não for dígito
    const digits = rawValue.replace(/\D/g, "");

    // Converte para decimal (ex: 1500 vira 15.00)
    const numericValue = Number(digits) / 100;

    const newVariations = [...variations];
    newVariations[index].price = numericValue;
    setVariations(newVariations);
  };

  const updateVariation = (index: number, field: string, value: any) => {
    const newVariations = [...variations];
    newVariations[index] = { ...newVariations[index], [field]: value };
    setVariations(newVariations);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave({
      ...formData,
      category: { id: Number(formData.categoryId) },
      variations: variations,
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container" onClick={(e) => e.stopPropagation()}>
        <header className="modal-header">
          <h2>Novo Produto</h2>
          <button className="btn-close" onClick={onClose}>
            &times;
          </button>
        </header>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-section">
            <h3>Informações Básicas</h3>
            <div className="input-group">
              <input
                type="text"
                placeholder="Nome do Produto"
                required
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
              />
              <select
                required
                value={formData.categoryId}
                onChange={(e) =>
                  setFormData({ ...formData, categoryId: e.target.value })
                }
              >
                <option value="">Selecione a Categoria</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <input
              placeholder="Descrição"
              className="description-item"
              onChange={(e) =>
                setFormData({ ...formData, description: e.target.value })
              }
            />
            <input
              type="text"
              placeholder="URL da Imagem"
              onChange={(e) =>
                setFormData({ ...formData, imageUrl: e.target.value })
              }
            />
          </div>

          <div className="form-section">
            <div className="section-header">
              <h3>Variações (Tamanhos/Preços)</h3>
              <button
                type="button"
                className="btn-add-var"
                onClick={() =>
                  setVariations([
                    ...variations,
                    {
                      size: "",
                      price: 0,
                      numberOfFlavor: isBeverage ? 0 : 1,
                      stock: null,
                    },
                  ])
                }
              >
                + Add Variação
              </button>
            </div>

            <div className="variations-list">
              {variations.map((v, index) => (
                <div key={index} className="variation-row">
                  <input
                    type="text"
                    placeholder="Ex: Grande"
                    required
                    value={v.size}
                    onChange={(e) =>
                      updateVariation(index, "size", e.target.value)
                    }
                  />

                  {/* INPUT DE PREÇO COM MÁSCARA */}
                  <input
                    type="text"
                    placeholder="R$ 0,00"
                    required
                    value={formatToBRL(v.price)}
                    onChange={(e) => handlePriceChange(index, e.target.value)}
                  />

                  <div className="input-with-label">
                    <input
                      type="number"
                      placeholder="Sabores"
                      value={v.numberOfFlavor}
                      className="inpt-flavor-number-item"
                      disabled={isBeverage}
                      style={
                        isBeverage
                          ? {
                              backgroundColor: "#e9ecef",
                              cursor: "not-allowed",
                            }
                          : {}
                      }
                      onChange={(e) =>
                        updateVariation(
                          index,
                          "numberOfFlavor",
                          parseInt(e.target.value),
                        )
                      }
                    />
                  </div>

                  {variations.length > 1 && (
                    <button
                      type="button"
                      className="btn-del-var"
                      onClick={() =>
                        setVariations(variations.filter((_, i) => i !== index))
                      }
                    >
                      🗑️
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <footer className="modal-footer">
            <button type="button" className="btn-cancel" onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="btn-submit">
              Salvar Produto
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
};

export const ModalAddCategory = ({
  isOpen,
  onClose,
  onSave,
}: ModalAddCategoryProps) => {
  const [formData, setFormData] = useState({
    name: "",
    imageUrl: "",
  });

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave({
      ...formData,
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-container" onClick={(e) => e.stopPropagation()}>
        <header className="modal-header">
          <h2>Nova categoria</h2>
          <button className="btn-close" onClick={onClose}>
            &times;
          </button>
        </header>

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-section">
            <h3>Informações Básicas</h3>
            <div className="input-group">
              <input
                type="text"
                placeholder="Nome da categoria"
                required
                onChange={(e) =>
                  setFormData({ ...formData, name: e.target.value })
                }
              />
            </div>
            <input
              type="text"
              placeholder="URL da Imagem"
              onChange={(e) =>
                setFormData({ ...formData, imageUrl: e.target.value })
              }
            />
          </div>

          <footer className="modal-footer">
            <button type="button" className="btn-cancel" onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="btn-submit">
              Salvar Produto
            </button>
          </footer>
        </form>
      </div>
    </div>
  );
};

export const ModalEditName = ({
  currentTable,
  setTable,
  handleCloseModal,
}: ModalEditProps) => {
  const handleSaveName = async () => {
    try {
      await fetchEditTableName(currentTable.name, currentTable.id);

      handleCloseModal();
      toast.success("Nome atualizado com sucesso!");
    } catch (error) {
      console.error("Erro ao atualizar o nome da mesa:", error);
      toast.error("Não foi possível atualizar o nome.");
    }
  };

  return (
    <div className="modal-Edit-Table-Name-Container">
      <div id="container-edit-modal">
        <button className="close-modal-button" onClick={handleCloseModal}>
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
            value={currentTable.name}
            onChange={(e) =>
              setTable({ ...currentTable, name: e.target.value })
            }
          />
          <button type="submit" className="save-flavor-button">
            Salvar
          </button>
        </form>
      </div>
    </div>
  );
};

export const ModalFlavor = ({
  handleCloseModal,
  table,
  fetchTableData,
  flavors,
  selectedProduct,
  mode,
  onConfirmDelivery,
}: modalFlavorProps) => {
  const [selectedFlavorIds, setSelectedFlavorIds] = useState<number[]>([]);
  const modalRoot = document.getElementById("modal-root");

  if (!modalRoot || !selectedProduct) return null;

  const requiredFlavors = selectedProduct.variation.numberOfFlavor;

  const normalFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "NORMAL",
  );
  const sweetFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "CANDY",
  );
  const specialFlavors = (flavors || []).filter(
    (f) => f.type?.toUpperCase() === "ESPECIAL",
  );

  const toggleFlavor = (id: number) => {
    if (selectedFlavorIds.includes(id)) {
      setSelectedFlavorIds((prev) => prev.filter((i) => i !== id));
      return;
    }

    if (selectedFlavorIds.length >= requiredFlavors) {
      toast.error(`Selecione exatamente ${requiredFlavors} sabor(es).`);
      return;
    }

    setSelectedFlavorIds((prev) => [...prev, id]);
  };

  const handleAddItemToOrderTable = async (
    variation: ProductVariation,
    productName: string,
    flavorIds: number[] = [],
  ) => {
    if (!table) return;

    try {
      const payload = {
        productVariationId: variation.id,
        quantity: 1,
        flavorIds,
        complementIds: variation.complements?.map((c) => c.id) || [],
        type: OrderType.TABLE,
      };

      await fetchAddItemToOrder(payload, table.id);
      if (fetchTableData) await fetchTableData();
      toast.success(`${productName} adicionado!`);
    } catch (error) {
      console.error("Erro ao adicionar item à mesa:", error);
      toast.error("Não foi possível adicionar o item.");
    }
  };

  const handleConfirm = async () => {
    if (selectedFlavorIds.length !== requiredFlavors) {
      toast.error(
        `Por favor, selecione exatamente ${requiredFlavors} sabor(es).`,
      );
      return;
    }

    if (mode === "TABLE") {
      await handleAddItemToOrderTable(
        selectedProduct.variation,
        selectedProduct.productName,
        selectedFlavorIds,
      );
      handleCloseModal();
      return;
    }

    // DELIVERY
    if (!onConfirmDelivery) {
      toast.error("Callback do delivery não foi configurado.");
      return;
    }

    if (mode == "DELIVERY") {
      onConfirmDelivery(
        selectedProduct.variation,
        selectedProduct.productName,
        selectedFlavorIds,
      );
      handleCloseModal();
    }
  };

  const renderFlavorGroup = (title: string, list: any[]) => {
    if (!list.length) return null;

    return (
      <>
        <p
          className="title-modal-type"
          style={{ fontSize: "20px", fontWeight: "bold" }}
        >
          {title}
        </p>

        <div className="flavors-grid">
          {list.map((flavor) => (
            <label key={flavor.id} className="flavor-card">
              <div>
                <input
                  type="checkbox"
                  value={flavor.id}
                  checked={selectedFlavorIds.includes(flavor.id)}
                  onChange={() => toggleFlavor(flavor.id)}
                />
                <span className="md-flavor-name">{flavor.name}</span>
              </div>
              <p className="md-flavor-description">{flavor.description}</p>
            </label>
          ))}
        </div>
      </>
    );
  };

  return createPortal(
    <div className="modal-flavor-overlay">
      <div className="modal-flavor-Container">
        <div id="container-flavor-modal">
          <button
            className="close-modal-button"
            onClick={handleCloseModal}
            type="button"
          >
            <FiX />
          </button>

          <h3>Escolha os sabores para {selectedProduct.productName}</h3>
          <p className="p-title-modal-flavor">
            Selecione exatamente {requiredFlavors} sabor(es)
          </p>

          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleConfirm();
            }}
          >
            {renderFlavorGroup("Tradicionais", normalFlavors)}
            {renderFlavorGroup("Doces", sweetFlavors)}
            {renderFlavorGroup("Especiais", specialFlavors)}

            <button
              type="submit"
              className="save-flavor-button"
              disabled={selectedFlavorIds.length !== requiredFlavors}
            >
              Confirmar
            </button>
          </form>
        </div>
      </div>
    </div>,
    modalRoot,
  );
};
export const ModalPay = ({
  table,
  total,
  totalPaid,
  subtotal,
  paymentMethods,
  acrescimo,
  handleCloseModal,
}: modalPayProps) => {
  const [paymentEntries, setPaymentEntries] = useState<PaymentEntry[]>([]);
  const navigator = useNavigate();

  useEffect(() => {
    if (paymentEntries.length === 0 && total > 0) {
      const initialAmount = Math.round(total * 100) / 100;
      setPaymentEntries([
        {
          id: Date.now(),
          method: paymentMethods[0],
          amount: initialAmount,
        },
      ]);
    }
  }, [total, paymentMethods]);

  const currentTotalPaid = paymentEntries.reduce(
    (acc, entry) => acc + entry.amount,
    0,
  );
  const currentChange = currentTotalPaid > total ? currentTotalPaid - total : 0;
  const roundedCurrentTotalPaid = Math.round(currentTotalPaid * 100) / 100;
  const roundedTotal = Math.round(total * 100) / 100;
  const isPaymentValid = roundedCurrentTotalPaid >= roundedTotal;

  const handleAddPaymentEntry = () => {
    setPaymentEntries([
      ...paymentEntries,
      {
        id: Date.now(),
        method: paymentMethods[0],
        amount: 0,
      },
    ]);
  };

  const handleRemovePaymentEntry = (idToRemove: number) => {
    setPaymentEntries(
      paymentEntries.filter((entry) => entry.id !== idToRemove),
    );
  };

  const handleChangePaymentEntry = (
    idToChange: number,
    field: "method" | "amount",
    // O valor para 'amount' será sempre 'number', e para 'method' será 'string'
    value: string | number,
  ) => {
    setPaymentEntries(
      paymentEntries.map((entry) =>
        entry.id === idToChange ? { ...entry, [field]: value } : entry,
      ),
    );
  };

  const handleConfirmPayment = async () => {
    if (!table?.id) {
      toast.error("Erro: ID da mesa não encontrado.");
      return;
    }

    const paymentsPayload = paymentEntries.map((entry) => ({
      method: entry.method,
      amount: entry.amount,
    }));

    const requestBody = {
      paymentEntries: paymentsPayload,
      total: total,
      addition: acrescimo,
      subtotal: subtotal,
      discount: 0,
      type: OrderType.TABLE,
    };

    if (totalPaid < total) {
      toast.error(
        "O valor pago é menor que o total da conta. Adicione mais pagamentos.",
      );
      return;
    }

    await fetchCofirmPayment(requestBody, table.id)
      .then((res) => {
        toast.success(`${table.name} fechada e pagamento confirmado!`);
        handleCloseModal();
        navigator("/");
      })
      .catch((err) => {
        const errorMessage =
          err.response?.data?.message ||
          "Erro desconhecido. Verifique se o pedido está ativo no backend.";
        console.error("Erro ao finalizar o pagamento:", err);
        toast.error(`Falha ao fechar mesa: ${errorMessage}`);
      });
  };

  return (
    <div className="modal-Pay-Table-Container">
      <div id="container-pay-modal">
        <button className="close-modal-button" onClick={handleCloseModal}>
          <FiX />
        </button>
        <h3>Finalizar Pedido - {table.name}</h3>

        <div className="payment-summary">
          <p>
            Total da Conta:{" "}
            <span className="highlight-price">
              R$ {total.toFixed(2).replace(".", ",")}
            </span>
          </p>
          <hr />
          <p>
            Total Pago:{" "}
            <span style={{ fontWeight: "bold", color: "#42a5f5" }}>
              R$ {currentTotalPaid.toFixed(2).replace(".", ",")}{" "}
            </span>
          </p>
          <p>
            Restante a Pagar:{" "}
            <span
              style={{
                fontWeight: "bold",
                color: currentTotalPaid < total ? "#e53935" : "#66bb6a",
              }}
            >
              R${" "}
              {Math.max(0, total - currentTotalPaid)
                .toFixed(2)
                .replace(".", ",")}{" "}
            </span>
          </p>
          {currentChange > 0 && (
            <p>
              Troco:{" "}
              <span style={{ fontWeight: "bold", color: "#1b5e20" }}>
                R$ {currentChange.toFixed(2).replace(".", ",")}{" "}
              </span>
            </p>
          )}
        </div>

        <div>
          <h4>Formas de Pagamento</h4>
          <form onSubmit={(e) => e.preventDefault()}>
            <div className="payment-entries-list">
              {paymentEntries.map((entry) => (
                <div key={entry.id} className="payment-entry-row">
                  <select
                    value={entry.method}
                    onChange={(e) =>
                      handleChangePaymentEntry(
                        entry.id,
                        "method",
                        e.target.value,
                      )
                    }
                  >
                    {paymentMethods.map((method) => (
                      <option key={method} value={method}>
                        {method.replace("_", " ")}
                      </option>
                    ))}
                  </select>
                  <input
                    type="number"
                    placeholder="Valor Pago"
                    step="0.01"
                    min="0"
                    value={entry.amount === 0 ? "" : entry.amount}
                    onChange={(e) => {
                      const inputValue =
                        e.target.value === "" ? "0" : e.target.value;
                      const roundedValue =
                        Math.round(Number(inputValue) * 100) / 100;

                      handleChangePaymentEntry(
                        entry.id,
                        "amount",
                        roundedValue,
                      );
                    }}
                  />
                  <button
                    type="button"
                    className="remove-payment-entry-btn"
                    onClick={() => handleRemovePaymentEntry(entry.id)}
                    disabled={paymentEntries.length === 1}
                  >
                    <FiX />
                  </button>
                </div>
              ))}
            </div>

            <button
              type="button"
              className="add-payment-entry-btn"
              onClick={handleAddPaymentEntry}
            >
              <FiPlus /> Adicionar Pagamento
            </button>

            <button
              type="button"
              className="save-flavor-button confirm-pay-button"
              onClick={handleConfirmPayment}
              disabled={!isPaymentValid}
            >
              Confirmar Pagamento e Fechar Mesa
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export const ModalPercent = ({
  handleClosePercentModal,
  onSavePercent,
  currentPercent,
}: ModalPercentProps) => {
  const handleFormSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const input = (
      e.currentTarget.elements.namedItem("percent") as HTMLInputElement
    ).value;
    const newPercent = Number(input);
    onSavePercent(newPercent);
  };

  return (
    <div className="modal-percent-Container">
      <div id="container-percent-modal">
        <button
          className="close-modal-button"
          onClick={handleClosePercentModal}
        >
          <FiX />
        </button>
        <h3>Alterar acréscimo (%)</h3>
        <form onSubmit={handleFormSubmit}>
          <input
            name="percent"
            type="number"
            defaultValue={currentPercent}
            min={0}
            step={1}
            className="inpt-modal-acresscimento"
          />
          <button type="submit" className="save-flavor-button">
            Salvar
          </button>
        </form>
      </div>
    </div>
  );
};
