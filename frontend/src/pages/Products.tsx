import React, { useEffect, useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import Loading from "../components/Loading";

import { Product, ProductInterface } from "../types/interfaces/product.interface";
import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import { fetchAllProducts, fetchCategories, fetchCreateProduct, fetchCreateVariation, fetchDeleteProduct, fetchDeleteVariation, fetchFlavores } from "../api";

import "../styles/Products.css"; 
import { ModalAddProduct } from "../components/modalGeral";

export default function Produtos() {
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<ProductInterface[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isExcludeModalOpen, setIsExcludeModalOpen] = useState(false);
const [selectedProductForAction, setSelectedProductForAction] = useState<{productId: number, variationId?: number} | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      const [productsRes, categoriesRes, flavorsRes] = await Promise.all([
        fetchAllProducts(),
        fetchCategories(),
        fetchFlavores(),
      ]);
      setProducts(productsRes);
      setCategories(categoriesRes);
      setFlavors(flavorsRes);
    } catch (err) {
      console.error("Erro ao carregar dados:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

const handleSaveNewProduct = async (payload: any) => {
  try {
    setLoading(true);
    
    const savedProduct = await fetchCreateProduct(payload);
    const productId = savedProduct.id;

    if (payload.variations && payload.variations.length > 0) {
      const variationPromises = payload.variations.map((variation: any) => 
        fetchCreateVariation(variation, productId)
      );
      await Promise.all(variationPromises);
    }
    console.log("Chegou aqui")
    setIsModalOpen(false);
    await loadData();
    console.log("Chegou ali")
  } catch (err) {
    console.error("Erro ao salvar produto e variações:", err);
  } finally {
    setLoading(false);
  }
};

const openExcludeModal = (productId: number, variationId: number) => {
  setSelectedProductForAction({ productId, variationId });
  setIsExcludeModalOpen(true);
};
const handleDeleteProduct = async () => {
  if (!selectedProductForAction) return;
  if (window.confirm("Isso excluirá o produto e TODAS as suas variações. Confirmar?")) {
    try {
      setLoading(true);
      await fetchDeleteProduct(selectedProductForAction.productId);
      setIsExcludeModalOpen(false);
      await loadData();
    } catch (err) {
      console.error("Erro ao excluir produto", err);
    } finally {
      setLoading(false);
    }
  }
};

const handleDeleteVariation = async () => {
  if (!selectedProductForAction?.variationId) return;
  if (window.confirm("Deseja excluir apenas este tamanho/variação?")) {
    try {
      setLoading(true);
      await fetchDeleteVariation(selectedProductForAction.variationId);
      setIsExcludeModalOpen(false);
      await loadData();
    } catch (err) {
      console.error("Erro ao excluir variação", err);
    } finally {
      setLoading(false);
    }
  }
};

  const getCategoryName = (categoryId: number | string) => {
    const category = categories.find(cat => cat.id === Number(categoryId));
    return category ? category.name : "Não categorizado";
  };

  const formatCurrency = (value: number) =>
    value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

  return (
    <div className="products-page-container">
      <Header />

      <main className="products-main-content">
        <div className="products-header-actions">
          <GeralTitle title="Gestão de Cardápio" />
          <button className="btn-add-product" onClick={() => setIsModalOpen(true)}>
            + Novo Produto
          </button>
        </div>

        {loading ? (
          <div className="products-loading-wrapper">
            <Loading text={"Saindo do forno..."} />
          </div>
        ) : (
          <div className="products-table-container">
            <table className="products-table">
              <thead>
                <tr>
                  <th>Produto</th>
                  <th>Categoria</th>
                  <th>Tamanho/Variação</th>
                  <th>Preço</th>
                  <th>Qtd Sabores</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {products.length > 0 ? (
                  products.map((product) => (
                    <React.Fragment key={product.id}>
                      {product.variations?.map((variation, index) => (
                        <tr key={`${product.id}-${variation.id}`} className="product-row">
                          {index === 0 && (
                            <>
                              <td rowSpan={product.variations.length} className="product-info-cell name">
                                {product.name}
                              </td>
                              <td rowSpan={product.variations.length} className="product-info-cell category">
                                {product.category?.name || getCategoryName(product.category?.id || "")}
                              </td>
                            </>
                          )}

                          <td className="variation-cell">{variation.size}</td>
                          <td className="price-cell">{formatCurrency(variation.price)}</td>
                          <td className="flavor-cell">
                            {variation.numberOfFlavor !== null && variation.numberOfFlavor !== undefined 
                              ? variation.numberOfFlavor > 0 
                                ? `${variation.numberOfFlavor} sab.` 
                                : "1 sab." 
                              : "1 sab."}
                          </td>
                          <td className="actions-cell">
<button 
    className="btn-delete-trigger" 
    onClick={() => openExcludeModal(product.id, variation.id)}
  >
    🗑️
  </button>                          </td>
                        </tr>
                      ))}
                    </React.Fragment>
                  ))
                ) : (
                  <tr className="empty-table-row">
                    <td colSpan={6}>Nenhum produto encontrado.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
        
        <ModalAddProduct 
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          categories={categories}
          onSave={handleSaveNewProduct}
        />
      {isExcludeModalOpen && (
  <div className="modal-overlay" onClick={() => setIsExcludeModalOpen(false)}>
    <div className="modal-exclude-item-container" onClick={e => e.stopPropagation()}>
      <div className="title-container">
        <h1>Excluir Item</h1>
        <button className="btn-close" onClick={() => setIsExcludeModalOpen(false)}>&times;</button>
      </div>
      <p>O que você deseja remover?</p>
      <div className="modal-exclude-main-container">
        <button 
          className="button-item-exclude product" 
          onClick={handleDeleteProduct}
        >
          Excluir Produto Inteiro
        </button>
        <button 
          className="button-item-exclude variation" 
          onClick={handleDeleteVariation}
        >
          Excluir Apenas este Tamanho
        </button>
      </div>
    </div>
  </div>
)}
      </main>
    </div>
  );
}