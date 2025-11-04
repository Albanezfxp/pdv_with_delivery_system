import { ProductVariation } from "./productVariation.interface";

export interface Product {
  id: number;
  name: string;
  description: string;
  active: boolean;
  imageUrl: string;
  categoryId?: number;
  variations: ProductVariation[];
}