
import { FlavorType } from "../enums/flavorType.enum";
import { OrderStatus } from "../enums/orderStatus.enum";
import { OrderType } from "../enums/orderType.enum";
import { PaymentMethod } from "./orderEntity.interface";



export interface OrderDeliveryResponse {
  id: number;
  client: Client | null;
  user: User | null;
  table: Table | null;

  payments: Payment[];

  status: OrderStatus;
  discount: number;
  addition: number;
  total: number;
  subtotal: number;

  createdAt: string; // ISO datetime
  items: OrderItem[];

  type: OrderType;
}

 interface Payment {
  method: string;
  amount: number;
}

interface Client {
  id: number;
  name: string;
  phone: string;
  email: string;
  birthday: string | null;
  endereco: {
        cep: string,
        neighborhood: string,
        street: string,
        number: string,
        complement?: string,
        reference: string,
        city: string
  };
}

 interface User {
  [key: string]: unknown;
}

 interface Table {
  [key: string]: unknown;
}

 interface OrderItem {
  id: number;
  productVariation: ProductVariation;

  flavors: Flavor[];
  quantity: number;
  subtotal: number;

  notes: string;
  complements: Complement[];
}

 interface ProductVariation {
  id: number;
  size: string; // ex: "Média" | "Grande" (pode ampliar depois)
  price: number;
  numberOfFlavor: number;
  product: {
    name: string
  }
  flavors: Flavor[]; // no JSON veio []
  complements: Complement[]; // no JSON veio []
  stock: number | null; // no JSON veio null
}

 interface Flavor {
  id: number;
  name: string;
  description: string;
  product: Product;
  type: FlavorType;
}

 interface Product {
  id: number;
  name: string;
  description: string;
  active: boolean;
  imageUrl: string;
  variations: ProductVariation[];
}

 interface Complement {
  [key: string]: unknown;
}