import GeralTitle from "../components/GeralTitle";
import "../styles/Profile.css"
import logo from "../assets/logo/logoSamdLyn.jpg";

import Header from "../components/Header";

export default function Profile() {
    return <>
    <div id="profile-app-container">
        <Header/>
        <div className="profile-main-container">
            <main>
            <GeralTitle title="Perfil" />
            <div className="data-main-container">
            <div className="img-profile-container">
            <img src={logo} alt="logo_img" /></div>
            <div className="name-profile-container">
            <p>SAMD´LYN PIZZARIA</p>
            </div>
            <div className="email-profile-container">
            <p>emailteste@teste.com</p>
            </div>
            <div className="button-edit-profile-container">
                <button>Editar perfil</button>
            </div>
            </div>
            </main>
        </div>
    </div>
    </>
}