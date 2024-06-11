import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import "./home.scss";
import HomeUserTable from "../../components/homeUserTable/HomeUserTable";
import HomeEventTable from "../../components/homeEventTable/HomeEventTable";
import HomeReportTable from "../../components/homeReportTable/HomeReportTable";

const Home = () => {
  return (
    <div className="home">
      <Navbar />
      <div className="main">
        <Sidebar />
        <div className="homeContainer">
          <div className="listTitle">Nowi użytkownicy</div>
          <HomeUserTable />
          <div className="listTitle">Nowe wydarzenia</div>
          <HomeEventTable />
          <div className="listTitle">Nowe zgłoszenia</div>
          <HomeReportTable />
        </div>
      </div>
    </div>
  );
};

export default Home;
