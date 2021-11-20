import { useState, useEffect } from 'react';
import { Container, Form, Button, Col, Row, Tooltip, OverlayTrigger } from "react-bootstrap";
import { useSortableData } from './useSortableData';
import { CONCURRENT_USERS_PER_INSTANCE, MASTER_API_PATH, RESULT_API_PATH } from './constants';
import './App.css';

import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
  const [results, setResults] = useState([]);
  const [slaves, setSlaves] = useState(0);
  const [sortedResults, requestSort, sortConfig] = useSortableData(results, { key: 'key', direction: 'descending' });

  useEffect(() => {
    const interval = setInterval(() => {
      fetchResults();
      fetchSlaves();
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const fetchResults = async () => {
    fetch(RESULT_API_PATH + "/last?count=20")
    //fetch(RESULT_API_PATH + "/all")
      .then(response => response.json())
      .then(results => setResults(results));
  }

  const fetchSlaves = async () => {
    fetch(MASTER_API_PATH + "/slaves")
      .then(response => response.json())
      .then(results => setSlaves(results));
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    const request = {
      concurrentUsers: event.target.concurrentUsers.value,
      loopCount: event.target.loopCount.value
    }

    fetch(MASTER_API_PATH + "/load", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request)
    })
  }

  const renderResults = (results) => {
    return results.map((result) => {
      const tooltip =
        result.errors !== undefined ? 
        <Tooltip id={result.key}>
          {result.errors.map(err => (
            <><div>{err}</div> <br /></>
          ))}
        </Tooltip> :
        null;

      return (
        <tr key={result.key} className={result.state == 'running' ? "table-warning" : ""}>
          <td>{new Date(1 * result.key).toLocaleString()}</td>
          <td>{result.concurrentUsers}</td>
          <td>{(result.averageResponseTime / 1000000).toFixed(2)}</td>
          <td>{(result.maxResponseTime / 1000000).toFixed(2)}</td>
          <td>
            {result.errors !== undefined ?
              <OverlayTrigger overlay={tooltip}>
                <div>{result.state}</div>
              </OverlayTrigger>
              :
              <div>{result.state}</div>
            }
          </td>
        </tr >
      )
    })
  }

  return (
    <>
      <Container className="py-4">
        <div style={{ textAlign: 'center' }}>
          <h1>Load Generator</h1>
        </div>
      </Container>

      <Container>
        <Row>
          <Col>
            <Container>
              <Form onSubmit={handleSubmit}>
                <Form.Group controlId="concurrentUsers" className="mb-3">
                  <Form.Label>Concurrent users</Form.Label>
                  <Form.Control type="number" defaultValue={CONCURRENT_USERS_PER_INSTANCE} step={CONCURRENT_USERS_PER_INSTANCE} min={CONCURRENT_USERS_PER_INSTANCE} required={true} />
                </Form.Group>

                <Form.Group controlId="loopCount" className="mb-3">
                  <Form.Label>Loop count</Form.Label>
                  <Form.Control type="number" defaultValue="10" min={1} required={true} />
                </Form.Group>

                <Button variant="primary" type="submit">
                  Generate load
                </Button>
              </Form>
            </Container>
          </Col>

          <Col>
            Concurrent users available: {CONCURRENT_USERS_PER_INSTANCE * slaves}
          </Col>
        </Row>
      </Container>

      <Container className="py-5">
        <table className="table table-striped">
          <thead>
            <tr>
              <th scope="col" onClick={() => requestSort('key')}>Date</th>
              <th scope="col" onClick={() => requestSort('concurrentUsers')}>Concurrent users</th>
              <th scope="col" onClick={() => requestSort('averageResponseTime')}>Average response time (ms)</th>
              <th scope="col" onClick={() => requestSort('maxResponseTime')}>Max response time (ms)</th>
              <th scope="col" onClick={() => requestSort('state')}>State</th>
            </tr>
          </thead>
          <tbody>
            {renderResults(sortedResults)}
          </tbody>
        </table>
      </Container>
    </>
  );
}

export default App;
