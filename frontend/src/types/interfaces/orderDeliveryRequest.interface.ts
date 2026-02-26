import { OrderType } from "../enums/orderType.enum";
import { PaymentMethod } from "../enums/paymentMethods";
import { Order_Item_Entity } from "./orderItem.interface";

export interface OrderDeliveryRequest {
    cliente_name: string,
    cliente_phone: string,
    cliente_email?: string,
    cliente_endereco: {
        cep: string,
        neighborhood: string,
        street: string,
        number: string,
        complement?: string,
        reference: string,
        city: string
    },
    payment_methods: string,
    subtotal: number,
    items: Order_Item_Entity[],
    type: OrderType;
 }