import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

import TableItem from "./table-item";
import Loading from "./Loading";
import GeralTitle from "./GeralTitle";

import { Table } from "../types/interfaces/table.interface";
import { TableStatus } from "../types/enums/TableStatus.enum";
import Header from "./Header";
import MainContent from "./MainContent";

export default function TableSection() {
  const navigate = useNavigate();
  const [tables, setTables] = useState<Table[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get("http://localhost:8080/table")
      .then((res) => setTables(res.data))
      .finally(() => setLoading(false));
  }, []);

  const handleAddTable = () => {
    setLoading(true);

    axios
      .post("http://localhost:8080/table", {
        name: `Mesa ${tables.length + 1}`,
        status: TableStatus.FREE,
      })
      .then(() => axios.get("http://localhost:8080/table"))
      .then((res) => setTables(res.data))
      .finally(() => setLoading(false));
  };

if (loading) {  
  return (
    <div className="loading-wrapper">
      <Loading text="Carregando..." />
    </div>
  );
}


  return (
    <>
      <GeralTitle title={`Mesas (${tables.length})`} />

      <section className="tables-flex">
        {tables.map((table) => (
          <TableItem
            key={table.id}
            status={table.status}
            table={table.name}
            onClick={() => navigate(`/table/${table.id}`)}
          />
        ))}

        <div className="table-item add" onClick={handleAddTable}>
          <p>+</p>
          <span>Adicionar Mesa</span>
        </div>
      </section>
    </>
  );
}
