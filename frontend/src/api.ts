import axios from "axios";
import { ItemOrderRequest, Order_Item_Entity, PayloadDto } from "./types/interfaces/orderItem.interface";
import { Product, ProductInterface } from "./types/interfaces/product.interface";
import { categoryAdd } from "./types/interfaces/category.interface";
import { OrderDeliveryRequest } from "./types/interfaces/orderDeliveryRequest.interface";
import { OrderStatus } from "./types/enums/orderStatus.enum";
import { DeliveryQuery, PageResponse } from "./types/interfaces/DeliveryQuery.interface";
import { OrderDeliveryResponse } from "./types/interfaces/Delivery.interface";
import { ClientQuery } from "./types/ClienteQuery.type";

const api = axios.create({
baseURL: "http://localhost:8080", // <-- Use a URL base fixada aqui
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para tratar erros globais
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido/expirou - redirecionar para login
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export const fetchTable = async (id: string) => {
  const response = await api.get(`/table/${id}`);
  return response.data;
};

export const fetchAllTables = async () => {
  const reponse = await api.get("/table");
  return reponse.data;
};

export const fetchOrderItemsExternal = async (
  orderId: number | string
): Promise<Order_Item_Entity[]> => {
  const response = await api.get(`/order/itens-table/${orderId}`);
  return response.data;
};

export const fetchCategories = async () => {
  const response = await api.get("/categories")
  return response.data 
}

export const fetchNewCategory = async (payload: categoryAdd) => {
  const response = await api.post("/categories", payload)
  return response.data
}

export const fetchDeleteCategory = async (id: number) => {
  const response = await api.delete(`/categories/${id}`)
}

export const fetchFlavores = async () => {
  const response = await api.get("/flavor")
  return response.data;
}

export const fetchSaveName = async (tableId: number, tableName: string) => {
  const response = await api.put(`/table/update-table/${tableId}`, {
        name: tableName,
      });
  return response.data
}

export const fetchDeleteTable = async (tableId: number) => {
  const response = await api.delete(`/table/${tableId}`)
  return response.data;
}

export const fetchDeleteDeliveryOrder = async (id: number) => {
  const response = await api.delete(`/order/delete-order-delivery/${id}`)
}

export const fetchAddItemToOrder = async (itemOrderRequest: ItemOrderRequest, tableId: number) => {
  const response = await api.post(`/order/add-item/${tableId}`, itemOrderRequest)
  return response.data;
}

export const fetchAddDelivery = async (order: OrderDeliveryRequest) => {
  const response = await api.post(`/order/create_order_delivery`, order)
  return response.data;
}

//OBS: DEVO PASSAR NO PARAMETRO O PAGE EO SIZE E BUSCAR  O CONTENT, PARA RETORNAR APENAS O ARRAY
export async function fetchAllDeliverys(query: DeliveryQuery): Promise<PageResponse<OrderDeliveryResponse>> {
  const params: Record<string, string | number | boolean> = {
    page: query.page,
    size: query.size,
    direction: query.direction,
    directionParam: query.directionParam,
    status: query.status,
  };

  if (query.q?.trim()) params.q = query.q.trim();
  if (query.todayOnly) params.todayOnly = true;
  if (query.paymentMethod && query.paymentMethod !== "ALL") params.paymentMethod = query.paymentMethod;
  if (typeof query.minTotal === "number") params.minTotal = query.minTotal;
  if (typeof query.maxTotal === "number") params.maxTotal = query.maxTotal;

  const { data } = await api.get<PageResponse<OrderDeliveryResponse>>("/order/orders-delivery", { params });
  return data;
}

export const fetchUpdateStatusOrderDelivery = async (status: OrderStatus, id: string ) => {
  const realId = parseFloat(id);
  const response = await api.patch(`/order/updatedStatus/${realId}`, status)
  return response.data;
}

export const fetchDeliveryOrderById = async (id: number) => {
  const response = await api.get(`/delivery_detail/${id}`)
  return response.data;
}

export const fecthDeleteItemToOrder = async (ordertItemId: number) => {
  const response = await api.delete(
        `/order/itens-table/${ordertItemId}`
      );
return response.data;
}

export const fetchCofirmPayment = async (payload: PayloadDto, tableId: number) => {
  const response = await api.post(`/order/close/${tableId}`, payload)
  return response.data;
}

export const fetchEditTableName = async (payload: string, tableId: number) => {
  const response = await api.put(`/table/update-table/${tableId}`, {
    name: payload
  });
  return response.data
}

export const fetchRemoveTable = async (tableId: number) => {
  const response = await api.delete(`/table/${tableId}`)
  return response.data;
}

export const fetchRemoveItemToOrder = async (orderItemId: number) => {
  const response = await api.delete(`/order/itens-table/${orderItemId}`)
  return response.data;
}

export const fetchAllOrders = async () => {
  const response = await api.get("/order_entity")
  return response.data;
}

export const fetchAllPaymentOrders = async () => {
  const response = await api.get("/order/payments")
  return response.data
}
export const fetchAllClients = async (
  query: ClientQuery
): Promise<PageResponse<any>> => {
  const { data } = await api.get("/clientes", {
    params: {
      page: query.page,
      size: query.size,
    },
  });

  return data;
};

export const fetchAllProducts = async () => {
  const response = await api.get("/products")
  return response.data
}
export const fetchCreateProduct = async (payload : Product) => {
const newProduct = {
    name: payload.name,
    description: payload.description,
    active: payload.active,
    imageUrl: payload.imageUrl,
    category: { id: Number(payload.categoryId) } 
  };

  const response = await api.post("/products", newProduct)
  return response.data;
}

export const fetchCreateVariation = async (variation: any, productId: number) => {
  const payload = {
    productId: Number(productId), 
    size: variation.size,
    price: variation.price,
    numberOfFlavor: variation.numberOfFlavor,
    stock: variation.stock || 0
  };
  
  const response = await api.post("/product_variation", payload);
  return response.data;
}

export const fetchDeleteProduct = async (id: number) => {
  try {
    const response = await api.patch(`/products/disabled-product/${id}`)
  return  response.data;
    } catch (err) {
  if (axios.isAxiosError(err)) {
    console.log("STATUS:", err.response?.status);
    console.log("DATA:", err.response?.data);
    console.log("URL:", err.config?.url);
    console.log("METHOD:", err.config?.method);
  } else {
    console.log("ERRO:", err);
  }
}
}
export const fetchDeleteVariation = async (id: number) => {
  const response = await api.delete(`/product_variation/${id}`)
  return response.data;
}
export default api;
