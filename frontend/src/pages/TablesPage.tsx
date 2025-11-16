import Header from "../components/Header";
import "../styles/TablesPage.css";
import TableSection from "../components/Table-Section";
import MainContent from "../components/MainContent";

export default function TablesPage() {
  return (
    <div id="tables-page-container">
      <Header />
      <MainContent>
          <TableSection />
      </MainContent>
    </div>
  );
}
