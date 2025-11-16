import React, { useEffect, useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import Loading from "../components/Loading";
import "../styles/categories.css";
import { Category, categoryAdd } from "../types/interfaces/category.interface";
import { fetchCategories, fetchDeleteCategory, fetchNewCategory } from "../api";
import { ModalAddCategory } from "../components/modalGeral";
import toast from "react-hot-toast";

export default function Categories() {
    const [loading, setLoading] = useState(true);
    const [categories, setCategories] = useState<Category[]>([]);
    const [isOpen, setIsOpen] = useState(false)
    
    const [isExcludeModalOpen, setIsExcludeModalOpen] = useState(false);
    const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);

    const loadData = async () => {
        try {
            setLoading(true);
            const categoryData = await fetchCategories();
            setCategories(categoryData);
        } catch (error) {
            console.error("Erro ao carregar categorias:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    const openExcludeModal = (id: number) => {
        setSelectedCategoryId(id);
        setIsExcludeModalOpen(true);
    };

    const handleSaveNewCategory = async (paylaod: categoryAdd) => {
        try {
            setLoading(true)
            await fetchNewCategory(paylaod)
            toast.success("Sucesso em adicionar categoria.")
            setIsOpen(false)
            await loadData();
        } catch (error) {
            alert("Deu erro: " + error)
            toast.error("Erro ao adicionar categoria.")
        }
    }

    const handleDeleteCategory = async () => {
        if (!selectedCategoryId) return;
        try {
            await fetchDeleteCategory(selectedCategoryId); 
            toast.success("Categoria excluída com sucesso!");
            setIsExcludeModalOpen(false);
            loadData();
        } catch (error) {
            alert("Erro ao excluir: " + error);
        }
    };

    return (
        <div className="categories-page-container">
            <Header />

            <main className="categories-main-content">
                <div className="categories-header-actions">
                    <GeralTitle title="Gestão de Categorias" />
                    <button className="btn-add-product" onClick={() => {setIsOpen(true)}}>
                        + Nova Categoria
                    </button>
                </div>

                {loading ? (
    <div className="loading-wrapper">
      <Loading text="Carregando..." />
    </div>
                ) : (
                    <div className="categories-table-container">
                        <table className="categories-table">
                            <thead>
                                <tr>
                                    <th>Nome da Categoria</th>
                                    <th>Qtd Produtos</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                {categories.length > 0 ? (
                                    categories.map((cat) => (
                                        <tr key={cat.id} className="categories-row">
                                            <td className="categories-info-cell name">
                                                {cat.name}
                                            </td>
                                            <td>
                                                {cat.products?.length || 0} produtos
                                            </td>
                                            <td className="actions-cell">
                                                <button 
                                                    className="btn-delete-trigger" 
                                                    onClick={() => openExcludeModal(cat.id)}
                                                >
                                                    🗑️
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr className="empty-table-row">
                                        <td colSpan={3}>Nenhuma categoria encontrada.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}

                {isExcludeModalOpen && (
                    <div className="modal-overlay" onClick={() => setIsExcludeModalOpen(false)}>
                        <div className="modal-exclude-item-container" onClick={e => e.stopPropagation()}>
                            <div className="title-container">
                                <h1>Excluir Categoria</h1>
                                <button className="btn-close" onClick={() => setIsExcludeModalOpen(false)}>&times;</button>
                            </div>
                            <p>Tem certeza que deseja excluir esta categoria? Isso pode afetar os produtos vinculados a ela.</p>
                            <div className="modal-exclude-main-container">
                                <button 
                                    className="button-item-exclude product" 
                                    onClick={handleDeleteCategory}
                                >
                                    Confirmar Exclusão
                                </button>
                                <button 
                                    className="button-item-exclude variation" 
                                    onClick={() => setIsExcludeModalOpen(false)}
                                >
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                <ModalAddCategory isOpen={isOpen} onClose={() => setIsOpen(false)} onSave={handleSaveNewCategory} />
            </main>
        </div>
    );
}