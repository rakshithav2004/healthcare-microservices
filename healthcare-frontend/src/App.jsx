import { useState } from "react";
import "./App.css";

function App() {
  const [showLogin, setShowLogin] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  function handleLoginClick() {
    setShowLogin(!showLogin);
    setMessage("");
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("Logging in...");

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            email: email,
            password: password,
          }),
        }
      );

      const data = await response.json();

      if (!response.ok) {
        setMessage(data.message || "Login failed. Check your credentials.");
        return;
      }

      setMessage("Login successful!");
      console.log("Login response:", data);
    } catch (error) {
      setMessage("Unable to connect to the server. Check your backend.");
      console.error("Login error:", error);
    }
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
              type="email"
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

          {email && <p>Email entered: {email}</p>}

          {message && <p>{message}</p>}
        </section>
      )}
    </div>
  );
}

export default App;