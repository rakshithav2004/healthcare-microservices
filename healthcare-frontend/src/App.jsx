import { useState } from "react";
import "./App.css";

function App() {
  const [showLogin, setShowLogin] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function handleLoginClick() {
    setShowLogin(!showLogin);
  }

  function handleSubmit(event) {
    event.preventDefault();

    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }

    alert(`Welcome, ${email}!`);
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

          <form onSubmit={handleSubmit}>
            <input
              type="text"
              placeholder="Email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />

            <br />
            <br />

            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />

            <br />
            <br />

            <button type="submit">Submit</button>
          </form>

          {email && <p>Welcome, {email}!</p>}
        </section>
      )}
  </div>
  );
}

export default App;