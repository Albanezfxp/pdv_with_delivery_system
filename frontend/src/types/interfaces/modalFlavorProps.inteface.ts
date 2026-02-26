import { Flavor } from "./flavor.interface";
import { SelectedProductState } from "./SelectedProductState.interface";
import { Table } from "./table.interface";
import { ProductVariation } from "./productVariation.interface";

export interface modalFlavorProps {
  handleCloseModal: () => void;

  // TABLE (opcional, como já está)
  table?: Table;
  fetchTableData?: () => void;

  flavors: Flavor[];
  selectedProduct: SelectedProductState | null;

  // ✅ NOVO: diz quem está usando o modal
  mode: "TABLE" | "DELIVERY";

  // ✅ NOVO: callback pro delivery receber os sabores confirmados
  onConfirmDelivery?: (
    variation: ProductVariation,
    productName: string,
    flavorIds: number[]
  ) => void;
}