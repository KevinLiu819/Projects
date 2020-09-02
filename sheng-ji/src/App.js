import React from 'react';
import Home from './Home';
import Game from './Game';
import './App.css';
import { BrowserRouter as Router, Route, Switch} from 'react-router-dom';

function App() {
  // localStorage.removeItem('username');
  // localStorage.removeItem('room');
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <Switch>
            <Route path ="/" exact component={Home}></Route>
            <Route path="/game" component={Game} />
          </Switch>
        </header>
      </div>
    </Router>
  );
}

export default App;
