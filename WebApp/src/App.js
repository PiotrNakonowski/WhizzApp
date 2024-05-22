import Login from "./pages/login/Login";
import Home from "./pages/home/Home";
import { BrowserRouter, Routes, Route} from "react-router-dom";

function App() {
  
  return (
      <BrowserRouter>
        <Routes>
        <Route path="/">
          <Route index element={<Home />} />
          <Route path="login" element={<Login />} />
        </Route>
        </Routes>
      </BrowserRouter>
  );
}

export default App;
