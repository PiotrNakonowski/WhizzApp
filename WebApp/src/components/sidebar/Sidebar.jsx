import "./sidebar.scss";
import DashboardIcon from "@mui/icons-material/Dashboard";
import GroupIcon from '@mui/icons-material/Group';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import ReportIcon from '@mui/icons-material/Report';
import { Link } from "react-router-dom";

const Sidebar = () => {
  return (
    <div className="sidebar">
      <ul>
        <Link to="/" style={{ textDecoration: "none" }}>
          <li>
            <DashboardIcon className="icon" />
            <span>Panel Główny</span>
          </li>
        </Link>
        <Link to="/users" style={{ textDecoration: "none" }}>
          <li>
            <GroupIcon className="icon" />
            <span>Użytkownicy</span>
          </li>
        </Link>
        <Link to="/#" style={{ textDecoration: "none" }}>
          <li>
            <EventAvailableIcon className="icon" />
            <span>Wydarzenia</span>
          </li>
        </Link>
        <Link to="/#" style={{ textDecoration: "none" }}>
          <li>
            <ReportIcon className="icon" />
            <span>Zgłoszenia</span>
          </li>
        </Link>
      </ul>
    </div>
  );
};

export default Sidebar;
