import React, { useEffect, useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import Loading from "../components/Loading";

import { ProductInterface } from "../types/interfaces/product.interface";
import { Category } from "../types/interfaces/category.interface";
import { Flavor } from "../types/interfaces/flavor.interface";
import {
  fetchAllProducts,
  fetchCategories,
  fetchCreateProduct,
  fetchCreateVariation,
  fetchDeleteProduct,
  fetchDeleteVariation,
  fetchFlavores,
} from "../api";

import "../styles/Products.css";
import { ModalAddProduct } from "../components/modalGeral";

export default function Produtos() {
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<ProductInterface[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [flavors, setFlavors] = useState<Flavor[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isExcludeModalOpen, setIsExcludeModalOpen] = useState(false);

  const [selectedProductForAction, setSelectedProductForAction] = useState<{
    productId: number;
    variationId?: number;
  } | null>(null);

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

      if (payload.variations?.length > 0) {
        await Promise.all(
          payload.variations.map((variation: any) =>
            fetchCreateVariation(variation, productId)
          )
        );
      }

      setIsModalOpen(false);
      await loadData();
    } catch (err) {
      console.error("Erro ao salvar produto:", err);
    } finally {
      setLoading(false);
    }
  };

  const openExcludeModal = (productId: number, variationId?: number) => {
    setSelectedProductForAction({ productId, variationId });
    setIsExcludeModalOpen(true);
  };

  const handleDeleteProduct = async () => {
    if (!selectedProductForAction) return;

    if (window.confirm("Excluir produto e todas as variações?")) {
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

    if (window.confirm("Excluir apenas esta variação?")) {
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
    const category = categories.find(
      (cat) => cat.id === Number(categoryId)
    );
    return category ? category.name : "Não categorizado";
  };

  const formatCurrency = (value: number) =>
    value.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });

  return (
    <div className="products-page-container">
      <Header />

      <main className="products-main-content">
        <div className="products-header-actions">
          <GeralTitle title="Gestão de Produtos" />
          <button
            className="btn-add-product"
            onClick={() => setIsModalOpen(true)}
          >
            + Novo Produto
          </button>
        </div>

        {loading ? (
          <div className="products-loading-wrapper">
            <Loading text={"Saindo do forno..."} />
          </div>
        ) : (
          <div className="products-cards-container">
            {products.length > 0 ? (
              products.map((product) => (
                <div key={product.id} className="product-card">
                  
                  {/* HEADER */}
                  <div className="product-card-header">
                    <div>
                      <h2 className="product-name">{product.name}</h2>
                      <span className="product-category">
                        {product.category?.name ||
                          getCategoryName(product.category?.id || "")}
                      </span>
                    </div>

                    <button
                      className="btn-delete-product"
                      onClick={() => openExcludeModal(product.id)}
                    >
                      Excluir
                    </button>
                  </div>

                  {/* VARIAÇÕES */}
                  <div className="product-variations">
                    {product.variations?.map((variation) => (
                      <div
                        key={variation.id}
                        className="variation-card"
                      >
                        <div className="variation-info">
                          <span className="variation-size">
                            {variation.size}
                          </span>

                          <span className="variation-price">
                            {formatCurrency(variation.price)}
                          </span>

                          <span className="variation-flavor">
                            {variation.numberOfFlavor > 0
                              ? `${variation.numberOfFlavor} sabores`
                              : "1 sabor"}
                          </span>
                        </div>

                        <button
                          className="btn-delete-variation"
                          onClick={() =>
                            openExcludeModal(
                              product.id,
                              variation.id
                            )
                          }
                        >
                          Remover
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            ) : (
              <div className="empty-state">
                Nenhum produto encontrado.
              </div>
            )}
          </div>
        )}

        <ModalAddProduct
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          categories={categories}
          onSave={handleSaveNewProduct}
        />

        {isExcludeModalOpen && (
          <div
            className="modal-overlay"
            onClick={() => setIsExcludeModalOpen(false)}
          >
            <div
              className="modal-exclude-item-container"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="title-container">
                <h1>Excluir Item</h1>
                <button
                  className="btn-close"
                  onClick={() => setIsExcludeModalOpen(false)}
                >
                  &times;
                </button>
              </div>

              <p>O que deseja remover?</p>

              <div className="modal-exclude-main-container">
                <button
                  className="button-item-exclude product"
                  onClick={handleDeleteProduct}
                >
                  Excluir Produto
                </button>

                <button
                  className="button-item-exclude variation"
                  onClick={handleDeleteVariation}
                >
                  Excluir Variação
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}