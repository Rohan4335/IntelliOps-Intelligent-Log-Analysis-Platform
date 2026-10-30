import { useEffect, useState } from "react";
import axios from "axios";
import "./App.css";

const API_URL = "http://localhost:8080";

function App() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedIncident, setSelectedIncident] = useState(null);

  const fetchIncidents = async () => {
    try {
      const response = await axios.get(`${API_URL}/incidents`);
      setIncidents(response.data);
    } catch (error) {
      console.error("Failed to fetch incidents:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchIncidents();
  }, []);

  const total = incidents.length;

  const critical = incidents.filter(
    (incident) => incident.severity === "CRITICAL"
  ).length;

  const open = incidents.filter(
    (incident) => incident.status === "OPEN"
  ).length;

  const resolved = incidents.filter(
    (incident) => incident.status === "RESOLVED"
  ).length;

  return (
    <div className="app">

      {/* Sidebar */}
      <aside className="sidebar">
        <div className="logo">
          <div className="logo-icon">I</div>
          <div>
            <h2>IntelliOps</h2>
            <span>Incident Intelligence</span>
          </div>
        </div>

        <nav>
          <div className="nav-item active">
            <span>▣</span>
            Dashboard
          </div>

          <div className="nav-item">
            <span>⚠</span>
            Incidents
          </div>

          <div className="nav-item">
            <span>⌕</span>
            Log Search
          </div>

          <div className="nav-item">
            <span>◉</span>
            AI Analysis
          </div>
        </nav>

        <div className="sidebar-bottom">
          <div className="system-status">
            <span className="status-dot"></span>
            System Operational
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="main">

        <header className="header">
          <div>
            <h1>Incident Dashboard</h1>
            <p>AI-powered monitoring and root cause analysis</p>
          </div>

          <button className="refresh-btn" onClick={fetchIncidents}>
            ↻ Refresh
          </button>
        </header>

        {/* KPI Cards */}
        <section className="stats">

          <div className="stat-card">
            <div className="stat-icon">◉</div>
            <div>
              <p>Total Incidents</p>
              <h2>{total}</h2>
            </div>
          </div>

          <div className="stat-card critical-card">
            <div className="stat-icon">!</div>
            <div>
              <p>Critical</p>
              <h2>{critical}</h2>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">◷</div>
            <div>
              <p>Open</p>
              <h2>{open}</h2>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon">✓</div>
            <div>
              <p>Resolved</p>
              <h2>{resolved}</h2>
            </div>
          </div>

        </section>

        {selectedIncident && (
          <div className="incident-details">

            <div className="details-header">
              <div>
                <h2>{selectedIncident.title}</h2>
                <p>AI-generated incident analysis</p>
              </div>

              <button
                className="close-btn"
                onClick={() => setSelectedIncident(null)}
              >
                ✕ Close
              </button>
            </div>

            <div className="details-badges">

              <span
                className={`badge severity-${selectedIncident.severity.toLowerCase()}`}
              >
                {selectedIncident.severity}
              </span>

              <span
                className={`badge status-${selectedIncident.status.toLowerCase()}`}
              >
                {selectedIncident.status}
              </span>

              <span className="confidence-badge">
                Confidence: {selectedIncident.confidenceLevel || "N/A"}
              </span>

            </div>

            <div className="detail-section">
              <h3>Description</h3>
              <p>{selectedIncident.description}</p>
            </div>

            <div className="detail-grid">

              <div className="detail-card">
                <h3>Root Cause</h3>
                <p>
                  {selectedIncident.rootCause || "No root cause available."}
                </p>
              </div>

              <div className="detail-card">
                <h3>Impact</h3>
                <p>
                  {selectedIncident.impact || "No impact information available."}
                </p>
              </div>

            </div>

            <div className="detail-card">
              <h3>Recommended Actions</h3>
              <p>
                {selectedIncident.recommendedActions ||
                  "No recommended actions available."}
              </p>
            </div>

            <div className="detail-card">
              <h3>Evidence Logs</h3>

              <pre className="evidence">
                {selectedIncident.sourceQuery || "No evidence available."}
              </pre>
            </div>

          </div>
        )}

        {/* Incidents */}
        <section className="panel">

          <div className="panel-header">
            <div>
              <h2>Recent Incidents</h2>
              <p>Automatically detected application incidents</p>
            </div>

            <span className="incident-count">
              {total} incidents
            </span>
          </div>

          {loading ? (
            <div className="loading">
              Loading incidents...
            </div>
          ) : incidents.length === 0 ? (
            <div className="empty">
              No incidents found.
            </div>
          ) : (
            <div className="incident-table">

              <div className="table-header">
                <span>Title</span>
                <span>Severity</span>
                <span>Status</span>
                <span>Confidence</span>
                <span>Created</span>
              </div>

              {incidents.map((incident) => (
                <div
                  className="table-row clickable-row"
                  key={incident.id}
                  onClick={() => setSelectedIncident(incident)}
                >

                  <div>
                    <strong>{incident.title}</strong>
                    <small>
                      {incident.description}
                    </small>
                  </div>

                  <span
                    className={`badge severity-${incident.severity.toLowerCase()}`}
                  >
                    {incident.severity}
                  </span>

                  <span
                    className={`badge status-${incident.status.toLowerCase()}`}
                  >
                    {incident.status}
                  </span>

                  <span className="confidence">
                    {incident.confidenceLevel || "N/A"}
                  </span>

                  <span className="date">
                    {incident.createdAt
                      ? new Date(incident.createdAt).toLocaleString()
                      : "N/A"}
                  </span>

                </div>
              ))}

            </div>
          )}

        </section>

      </main>
    </div>
  );
}

export default App;