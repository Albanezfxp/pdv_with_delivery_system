import { Category } from "./category.interface";

export interface ModalAddProductProps {
  isOpen: boolean;
  onClose: () => void;
  categories: Category[];
  onSave: (product: any) => void;
}