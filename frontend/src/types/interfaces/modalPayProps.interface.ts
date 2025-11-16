import { Table } from "./table.interface";

export interface modalPayProps {
  table: {
    id: number;
    name: string;
  };
  total: number;
  totalPaid: number;
  subtotal: number;
  paymentMethods: string[];
  acrescimo: number;
  handleCloseModal: () => void;
}