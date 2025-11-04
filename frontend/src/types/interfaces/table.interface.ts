import { TableStatus } from "../enums/TableStatus.enum";
import { Order } from "./orderItem.interface";

export interface Table {
  id: number;
  name: string;
  status: TableStatus;
  // CORREÇÃO CRÍTICA: 'order' é um OBJETO (Order), que pode ser nulo se a mesa estiver vazia.
  order: Order | null; 
}