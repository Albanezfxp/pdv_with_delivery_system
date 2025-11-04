export interface OrderItem {
  id: number;
  name: string;
  notes: string;
  quantity: number;
  size: string;
  complementIds: number[];
  productVariationId: number;
  flavorIds: number[];
  subtotal: number;
}

export interface Order {
  id: number;
  status: string; // Ou o enum de status
  subtotal: number;
  // O campo que contém a lista de itens
  items: OrderItem[];
  // Outros campos como addition, discount, total, etc.
  discount: number;
  addition: number;
  
}

export interface Order_Item_Entity {
  id: number;
  orderId: number;
  name: string;
  notes: string;
  size: string;
  quantity: number;
  productVariationId: number;
  complementIds: number[];
  flavorIds: number[];
  subtotal: number;
  links: [];
}
