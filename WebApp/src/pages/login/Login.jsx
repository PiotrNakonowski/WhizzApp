import { useContext, useState } from "react";
import "./login.scss";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../firebase";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import { doc, getDoc } from "firebase/firestore";

const Login = () => {
  const [error, setError] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();
  const { dispatch } = useContext(AuthContext);

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      const user = userCredential.user;

      console.log("User signed in:", user.uid);

      // Pobierz dokument użytkownika z Firestore używając UID
      const userDoc = await getDoc(doc(db, "users", user.uid));

      if (userDoc.exists()) {
        console.log("User document:", userDoc.data());

        if (userDoc.data().isAdmin) {
          dispatch({ type: "LOGIN", payload: user });
          navigate("/");
        } else {
          console.log("User is not an admin");
          // Wyloguj użytkownika, jeśli nie jest adminem
          await auth.signOut();
          setError(true);
        }
      } else {
        console.log("User document does not exist");
        setError(true);
      }
    } catch (error) {
      console.log("Error during login:", error);
      setError(true);
    }
  };

  return (
    <div className="login-container">
      <div className="logo">
        <img className="logo-image" src="https://i.imgur.com/ROTGOx9.png" alt="logo" />
      </div>
      <div className="form-container">
        <div className="formTitle">Logowanie</div>
        <form onSubmit={handleLogin}>
          <input
            type="email"
            placeholder="Adres e-mail"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input-field"
          />
          <input
            type="password"
            placeholder="Hasło"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input-field"
          />
          <button type="submit" className="login-button">
            Zaloguj
          </button>
          {error && <span>Nieprawidłowy email, hasło lub brak uprawnień administratora!</span>}
        </form>
      </div>
    </div>
  );
};

export default Login;
