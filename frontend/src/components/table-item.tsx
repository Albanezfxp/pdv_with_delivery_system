import { useEffect, useState } from "react"


interface TableItemProps {
  table: string;
  status: string;
  onClick?: () => void; // função de clique
}

export default function TableItem({table, status, onClick}: TableItemProps) {

  const [enumStatus, setEnumStatus] = useState<String>();
  const [stringStatus, setStringStatus] = useState<String>()

  useEffect(() => {
    if (status == "FREE") {
      setEnumStatus("available")
      setStringStatus("Disponivel")
    } else if (status == "OCCUPIED") {
      setEnumStatus("occupied")
      setStringStatus("Ocupado")
    }
  }, [])

    return (
    <>
       <div className={`table-item ${enumStatus}`} onClick={onClick}>
              <p>{`${table}`}</p>
              <span className= {`status`}>{stringStatus}</span>
            </div>
    </>)
}