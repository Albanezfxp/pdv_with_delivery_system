import { OrderStatus } from "../enums/orderStatus.enum";


export type DeliveryQuery = {
  page: number;
  size: number;
  direction: "asc" | "desc";
  directionParam: string;
  status: OrderStatus;

  q?: string;
  todayOnly?: boolean;
  paymentMethod?: string;
  minTotal?: number;
  maxTotal?: number;
};

export type PageResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
};