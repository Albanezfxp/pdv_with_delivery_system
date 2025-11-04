import Header from "../components/Header";
import "../styles/TablesPage.css";
import TableSection from "../components/Table-Section";

export default function TablesPage() {
  return (
    <div id="tables-page-container">
      <Header />

      <main className="tables-main">
        <div className="tables-content">
          <TableSection />
        </div>
      </main>
    </div>
  );
}
