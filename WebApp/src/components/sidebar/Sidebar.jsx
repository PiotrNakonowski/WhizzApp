import "./sidebar.scss";
import DashboardIcon from "@mui/icons-material/Dashboard";
import GroupIcon from '@mui/icons-material/Group';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import ReportIcon from '@mui/icons-material/Report';
import { NavLink } from "react-router-dom";

const Sidebar = () => {
  return (
    <div className="sidebar">
      <ul>
        <li>
          <NavLink exact to="/" style={{ textDecoration: "none" }}>
            <DashboardIcon className="icon" />
            Panel Główny
          </NavLink>
        </li>
        <li>
          <NavLink to="/users" style={{ textDecoration: "none" }}>
            <GroupIcon className="icon" />
            Użytkownicy
          </NavLink>
        </li>
        <li>
          <NavLink to="/events" style={{ textDecoration: "none" }}>
            <EventAvailableIcon className="icon" />
            Wydarzenia
          </NavLink>
        </li>
        <li>
          <NavLink to="/reports" style={{ textDecoration: "none" }}>
            <ReportIcon className="icon" />
            Zgłoszenia
          </NavLink>
        </li>
      </ul>
    </div>
  );
};

export default Sidebar;
