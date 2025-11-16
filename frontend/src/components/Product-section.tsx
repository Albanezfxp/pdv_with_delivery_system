import { fetchAddItemToOrder } from "../api";
import { Category } from "../types/interfaces/category.interface";
import { ProductVariation } from "../types/interfaces/productVariation.interface";
import { Table } from "../types/interfaces/table.interface";
import notFoundImgProduct from "../assets/not_found/images.png";
import toast from "react-hot-toast";
import { OrderType } from "../types/enums/orderType.enum";
import { FiArrowUp } from "react-icons/fi"; // Opcional: ícone de seta

interface ModalProps {
  mode: "TABLE" | "DELIVERY";
  categorys: Category[];
  selectedCategory: Category | null;
  setSelectedCategory: (category: Category) => void;
  handleOpenFlavorModal: (productName: string, variation: ProductVariation) => void;
  fetchTableData?: () => void;
  table?: Table;
  handleAddItemOverride?: (variation: ProductVariation, productName: string) => void;
}

export default function ProductSection({ 
  mode, 
  categorys, 
  selectedCategory, 
  setSelectedCategory, 
  handleOpenFlavorModal, 
  fetchTableData, 
  table,
  handleAddItemOverride 
}: ModalProps) {
  
  const isTable = mode === "TABLE";
  const order_type = isTable ? OrderType.TABLE : OrderType.DELIVERY;

  const handleAddItemToOrder = async (variation: ProductVariation, productName: string, flavorIds: number[] = []) => {
    if (!table) return;
    try {
      const payload = {
        productVariationId: variation.id,
        quantity: 1,
        flavorIds: flavorIds,
        complementIds: variation.complements?.map((c) => c.id) || [],
        type: order_type            
      };
      await fetchAddItemToOrder(payload, table.id);
      if (fetchTableData) await fetchTableData();
      toast.success(`${productName} adicionado!`);
    } catch (error) {
      toast.error("Erro ao adicionar item.");
    }
  };

  return (
    <section className="products-section">
      {/* Lista de Categorias */}
      <div className="categories">
        {categorys.map((category) => (
          <div
            key={category.id}
            className={`category-item ${selectedCategory?.id === category.id ? "active" : ""}`}
            onClick={() => setSelectedCategory(category)}
          >
            <img src={category.imageUrl} alt={category.name} />
            <span>{category.name}</span>
          </div>
        ))}
      </div>

      {/* Grid de Produtos ou Estado Vazio */}
      <div className="products-grid-container">
        {selectedCategory ? (
          <div className="products-grid">
            {selectedCategory.products.flatMap((product) =>
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
                  <img src={product.imageUrl || notFoundImgProduct} alt={product.name} />
                  <div className="product-card-info">
                    <h3>{`${product.name} ${variation.size}`}</h3>
                    {variation.numberOfFlavor > 0 && <p>Sabores: {variation.numberOfFlavor}</p>}
                    <span className="price">R$ {variation.price.toFixed(2).replace(".", ",")}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        ) : (
          /* ESTADO VAZIO ATUALIZADO */
          <div className="empty-category-state">
            <div className="empty-state-content">
              <div className="icon-pulse">
                <FiArrowUp size={40} />
              </div>
              <h3>Cardápio disponível</h3>
              <p>Selecione uma categoria acima para visualizar os itens.</p>
            </div>
          </div>
        )}
      </div>
    </section>
  );
}