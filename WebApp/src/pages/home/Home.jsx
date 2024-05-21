import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import "./home.scss";

const Home = () => {
  return (
    <div className="home">
      <Navbar />
      <div className="main">
        <Sidebar />
        <div className="homeContainer">
          <div className="listContainer">
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
