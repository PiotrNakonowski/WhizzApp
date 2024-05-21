import "./navbar.scss";
import LogoutIcon from '@mui/icons-material/Logout';

const Navbar = () => {

  return (
    <div className="navbar">
      <div className="wrapper">
        <img className="logo-image" src="https://i.imgur.com/3U6VsFH.png" alt="logo"/>
        <div className="items">
          <div className="item">
            <LogoutIcon className="icon" />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Navbar;
