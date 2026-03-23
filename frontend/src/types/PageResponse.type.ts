export type PageResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // page atual (0-based)
  size: number;
};