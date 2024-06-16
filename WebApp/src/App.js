import Login from "./pages/login/Login";
import Home from "./pages/home/Home";
import Users from "./pages/users/Users";
import Events from "./pages/events/Events";
import Reports from "./pages/reports/Reports";
import { BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "./context/AuthContext";

function App() {
  
  const {currentUser} = useContext(AuthContext)

  const RequireAuth = ({ children }) => {
    return currentUser ? children : <Navigate to="/login" />;
  };

  return (
      <BrowserRouter>
        <Routes>
          <Route path="/">
            <Route path="login" element={<Login />} />
              <Route
                index
                element={
                  <RequireAuth>
                    <Home />
                  </RequireAuth>
                }
              />
            <Route path="users">
            <Route
              index
              element={
                <RequireAuth>
                  <Users />
                </RequireAuth>
              }
            />
            </Route>
            <Route path="events">
            <Route
              index
              element={
                <RequireAuth>
                  <Events />
                </RequireAuth>
              }
            />
            </Route>
            <Route path="reports">
            <Route
              index
              element={
                <RequireAuth>
                  <Reports />
                </RequireAuth>
              }
            />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
  );
}

export default App;
