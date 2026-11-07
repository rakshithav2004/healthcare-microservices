
function PatientDashboard({ user }) {
  return (
    <div>
      <h1>Patient Dashboard</h1>

      <h2>Welcome, Patient!</h2>

      <p>Email: {user.email}</p>
      <p>Role: {user.role}</p>

      <hr />

      <h2>My Healthcare Services</h2>

      <p>View My Profile</p>
      <p>View My Appointments</p>
      <p>View My Medical Records</p>
    </div>
  );
}

export default PatientDashboard;
