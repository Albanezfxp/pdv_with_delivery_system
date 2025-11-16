import { Table } from "./table.interface";

export interface ModalEditProps {
    currentTable: Table; // O objeto Table atual (propriedade 'table' no pai)
    setTable: (table: Table) => void; // Função para atualizar o objeto Table no pai
    handleCloseModal: () => void; // Função para fechar o modal
}