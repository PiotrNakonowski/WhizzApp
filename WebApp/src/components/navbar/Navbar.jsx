import { useContext } from "react";
import "./navbar.scss";
import LogoutIcon from '@mui/icons-material/Logout';
import { AuthContext } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const { dispatch } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch({ type: "LOGOUT" });
    navigate("/login");
  };

  return (
    <div className="navbar">
      <div className="wrapper">
        <img className="logo-image" src="https://i.imgur.com/3U6VsFH.png" alt="logo"/>
        <div className="items">
          <div className="item" onClick={handleLogout}>
            <LogoutIcon className="icon"/>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Navbar;
