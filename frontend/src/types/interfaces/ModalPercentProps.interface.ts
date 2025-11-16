export interface ModalPercentProps {
    handleClosePercentModal: () => void;
    onSavePercent: (newPercent: number) => void; // Função para enviar o valor para o pai
    currentPercent: number; // O valor inicial da porcentagem (ex: 10)
}