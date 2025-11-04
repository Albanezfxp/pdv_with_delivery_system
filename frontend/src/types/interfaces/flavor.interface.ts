import { FlavorType } from "../enums/flavorType.enum";

export interface Flavor {
  id: number;
  name: string;
  description: string;
  productId?: number;
  type: FlavorType;
}