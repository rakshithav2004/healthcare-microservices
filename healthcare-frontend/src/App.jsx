import { useState } from "react";
import "./App.css";

function App() {
  const [showLogin, setShowLogin] = useState(false);

  function handleLoginClick() {
    setShowLogin(!showLogin);
  }

  return (
    <div>
      <h1>My Healthcare Application</h1>

      <p>
        Welcome to our Healthcare Management System.
        Manage patients, doctors, and appointments in one place.
      </p>

      <section>
        <h2>Our Services</h2>
        <p>Patient Management</p>
        <p>Doctor Management</p>
        <p>Appointment Scheduling</p>
      </section>

      <button onClick={handleLoginClick}>
        {showLogin ? "Close Login" : "Login"}
      </button>

      {showLogin && (
        <section>
          <h2>Login</h2>

          <input type="text" placeholder="Username" />

          <br />
          <br />

          <input type="password" placeholder="Password" />
        </section>
      )}
    </div>
  );
}

export default App;