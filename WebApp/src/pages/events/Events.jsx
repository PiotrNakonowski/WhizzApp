import "./events.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import Navbar from "../../components/navbar/Navbar"
import EventTable from "../../components/eventTable/EventTable"
import ApproveEventTable from "../../components/approveEventTable/ApproveEventTable"


const Events = () => {
  return (
    <div className="events">
      <Navbar/>
      <div className="main">
        <Sidebar/>
        <div className="eventContainer">
          <div className="listTitle">Wydarzenia do zatwierdzenia</div>
          <ApproveEventTable />
          <div className="listTitle">Wydarzenia</div>
          <EventTable/>
        </div>
      </div>
    </div>
  );
};

export default Events