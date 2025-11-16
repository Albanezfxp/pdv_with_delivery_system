import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import "../styles/GeralSettings.css"
import clientIcon from "../assets/icons/cliente.png"
import editPerfilIcon from "../assets/icons/editar.png"
import productsIcon from "../assets/icons/produtos.png"
import categoriesIcon from "../assets/icons/caracteristicas.png"
import paysIcon from "../assets/icons/forma-de-pagamento.png"
import taxaIcon from "../assets/icons/taxa-de-cambio.png"
import bairrosIcon from "../assets/icons/entrega-rapida.png"
import cuponsIcon from "../assets/icons/cupom.png"
import qrCodeIcon from "../assets/icons/image-removebg-preview.png"
import cardapioIcon from '../assets/icons/cardapio.png'
import LinkSettings from "../components/LinkSettings";

export default function GeralSettings() {
    return <>
    <div className="settings-page-container">
        <Header/>
        <main>
            <div className="main-settings-container">
                <div id="title-geral">
                <GeralTitle title={`Ajustes`}/>
                </div>
                <div id="geral-configs-container">
                    <div id="title-geral-configs">
                        <h1>
                            Geral
                        </h1>
                    </div>
                <div id="main-links-container">
                <LinkSettings path="/clientes" srcImg={clientIcon} altImg="client" title="Clientes"/>
                <LinkSettings path="/profile" srcImg={editPerfilIcon} altImg="perfil" title="Perfil"/>
                <LinkSettings path="/products" srcImg={productsIcon} altImg="products" title="Produtos"/>
                <LinkSettings path="/categories" srcImg={categoriesIcon} altImg="categories" title="Categorias"/>
                <LinkSettings path="/acrescimo" srcImg={taxaIcon} altImg="acrescimo" title="Acrescimos"/>
                </div>
                </div>
            </div>
            <div className="main-settings-container">
                <div id="geral-configs-container">
                    <div id="title-geral-configs">
                        <h1>
                            Delivery
                        </h1>
                    </div>
                <div id="main-links-container">
                <LinkSettings path="/" srcImg={bairrosIcon} altImg="neithbohood" title="Bairros"/>
                <LinkSettings path="/" srcImg={cuponsIcon} altImg="cupons" title="Cupons"/>
                <LinkSettings path="/" srcImg={cardapioIcon} altImg="Cardapio" title="Cardapio delivery"/>
                <LinkSettings path="/" srcImg={paysIcon} altImg="payForm" title="Formas de pagamentos"/>
                </div>
                </div>
            </div>
        </main>
    </div>
    </>
}