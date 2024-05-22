import "./list.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import Navbar from "../../components/navbar/Navbar"

const List = () => {
  return (
    <div className="list">
      <Navbar/>
      <div className="main">
        <Sidebar/>
        <div className="listContainer">
        </div>
      </div>
    </div>
  )
}

export default List