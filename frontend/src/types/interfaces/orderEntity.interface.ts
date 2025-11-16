export interface OrderEntity {
  id: number;
  client: any | null;
  user: any | null;
  table: any | null;
  paymentMethods: PaymentMethod[]; // 🔥 ADICIONAR ESTA LINHA
  status: string;
  discount: number | null;
  addition: number | null;
  total: number | null;
  subtotal: number | null;
  createdAt: string;
  items: any[] | null;
  links: Link[];
}

export interface PaymentMethod {
  method: string;
  amount: number;
}

export interface Link {
  rel: string;
  href: string;
  type: string;
}