import "./reports.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import Navbar from "../../components/navbar/Navbar"
import ReportTable from "../../components/reportTable/ReportTable"



const Reports = () => {
  return (
    <div className="reports">
      <Navbar/>
      <div className="main">
        <Sidebar/>
        <div className="reportContainer">
          <div className="listTitle">Zgłoszenia</div>
          <ReportTable/>
        </div>
      </div>
    </div>
  );
};

export default Reports