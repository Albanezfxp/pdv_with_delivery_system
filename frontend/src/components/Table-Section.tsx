import { useEffect, useState } from "react";
import TableItem from "./table-item";
import axios from "axios";
import { Table,  } from "../types/interfaces/table.interface";
import { useNavigate } from "react-router-dom";
import { TableStatus } from "../types/enums/TableStatus.enum";
interface TableItemProps {
  table: string;
  status: TableStatus;
  classStatus?: TableStatus;
  onClick?: () => void; 
}

export default function TableSection() {
  const navigate = useNavigate();
  const [tables, setTables] = useState<Table[]>([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/table")
      .then((res) => setTables(res.data))
      .catch((err) => console.error("Erro ao tentar buscar as mesas"));
  }, []);

  const handleAddTable = () => {
    const response = {
      name: `Mesa ${tables.length + 1}`,
      status: TableStatus.FREE,
    };
    axios
      .post("http://localhost:8080/table", response)
      .then(() =>
        axios
          .get("http://localhost:8080/table")
          .then((res) => setTables(res.data))
      )
      .catch((err) => console.log(err));
  };

  return (
    <>
      <div className="tables-title">
        <h1>Mesas ({tables.length})</h1>
        <hr />
      </div>
      <section className="tables-flex">
        {tables.map((tables) => (
          <TableItem
            key={tables.id}
            status={tables.status}
            table={tables.name}
            onClick={() => navigate(`/table/${tables.id}`)} // aqui passamos o id correto
          />
        ))}
        <div className="table-item add" onClick={handleAddTable}>
          <p>+</p>
        </div>
      </section>
    </>
  );
}
