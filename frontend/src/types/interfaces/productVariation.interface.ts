import { Complement } from "./complements.interface";
import { Flavor } from "./flavor.interface";

export interface ProductVariation {
  id: number;
  productId?: number;
  size: string;
  price: number;
  stock?: number;
  numberOfFlavor?: number;
  flavors: Flavor[];
  complements: Complement[];
}
