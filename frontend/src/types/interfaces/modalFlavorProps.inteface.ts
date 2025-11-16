import { Flavor } from "./flavor.interface";
import { SelectedProductState } from "./SelectedProductState.interface";
import { Table } from "./table.interface";

    
export interface modalFlavorProps {
        handleCloseModal: () => void;
        table?: Table;
        fetchTableData?: () => void;
        flavors: Flavor[];
        selectedProduct: SelectedProductState | null;
    }