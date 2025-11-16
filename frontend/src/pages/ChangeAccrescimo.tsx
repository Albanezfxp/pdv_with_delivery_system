import { useState } from "react";
import GeralTitle from "../components/GeralTitle";
import Header from "../components/Header";
import { useAccrescimo } from "../context/AccrescimoProvider";
import "../styles/ChangeAccrescimo.css"
import toast from "react-hot-toast";

export default function ChangeAccrescimo() {
const { accrescimo, setAccrescimo } = useAccrescimo();    
    const [inputValue, setInputValue] = useState((accrescimo * 100).toString());

    const handleSave = () => {
        const newValue = parseFloat(inputValue) / 100;
        if (!isNaN(newValue)) {
            setAccrescimo(newValue);
            toast.success("Acréscimo atualizado com sucesso!");
        }
    };

    return (
        <div id="accrescimo-app-container">
            <Header />
            <div className="acrescimo-main-container">
        <GeralTitle title="Taxa de Acrescimo"/>
   
           <div className="data-main-container">
    <div className="acrescimo-input-group">
        <label>Taxa de Acréscimo (%)</label>
        <input 
            type="number" 
            value={inputValue} 
            onChange={(e) => setInputValue(e.target.value)}
        />
    </div>

    <div className="button-edit-acrescimo-container">
        <button onClick={handleSave}>Salvar Alterações</button>
    </div>
    
    <p className="current-value-info">
        Valor atual configurado: {(accrescimo * 100).toFixed(0)}%
    </p>
</div>
</div>
        </div>
    );
}