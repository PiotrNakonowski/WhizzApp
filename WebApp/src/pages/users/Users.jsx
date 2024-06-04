import "./users.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import Navbar from "../../components/navbar/Navbar"
import UserTable from "../../components/userTable/UserTable"


const Users = () => {
  return (
    <div className="users">
      <Navbar/>
      <div className="main">
        <Sidebar/>
        <div className="usersContainer">
        <div className="listTitle">Użytkownicy</div>
            <UserTable />
        </div>
      </div>
    </div>
  );
};

export default Users