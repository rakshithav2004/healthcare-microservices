import "./App.css";

function App() {
  function handleLoginClick() {
    alert("Login functionality coming soon!");
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

      <button onClick={handleLoginClick}>Login</button>
    </div>
  );
}

export default App;