import "./login.scss";

const Login = () => {
  return (
    <div className="login-container">
      <div className="logo">
        <img className="logo-image" src="https://i.imgur.com/ROTGOx9.png" alt="logo"/>
      </div>
      <div className="form-container">
        <div className="formTitle">Zaloguj się</div>
        <form>
          <input
            type="email"
            placeholder="Adres e-mail"
            className="input-field"
          />
          <input
            type="password"
            placeholder="Hasło"
            className="input-field"
          />
          <button type="submit" className="login-button">
            Login
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
