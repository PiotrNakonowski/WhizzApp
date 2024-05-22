import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import "./home.scss";
import Table from "../../components/table/Table";

const Home = () => {
  return (
    <div className="home">
      <Navbar />
      <div className="main">
        <Sidebar />
        <div className="homeContainer">
          <div className="listContainer">
            <div className="listTitle">Nowi użytkownicy</div>
            <Table />
            <div className="listTitle">Nowe wydarzenia</div>
            <Table />
            <div className="listTitle">Nowe zgłoszenia</div>
            <Table />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
