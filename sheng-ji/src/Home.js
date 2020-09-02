import React from 'react';
import { Redirect } from 'react-router-dom';

class Home extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            value: '',
            room: '',
            name: '',
            redirect: localStorage.getItem('username') != null,
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleRoom = this.handleRoom.bind(this);
        this.handleUser = this.handleUser.bind(this);
        this.handleJoin = this.handleJoin.bind(this);
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit = async event => {
        event.preventDefault();
        const id = Math.floor(Math.random() * 1000000);
        const req = JSON.stringify({
            turn: this.state.value,
            trumpnum: 2,
            trumpsuit: '',
            bottomcards: [],
            owner: this.state.value,
            dealer: this.state.value,
            player1name: this.state.value,
            player2name: '',
            player3name: '',
            player4name: '',
            player1cards: [],
            player2cards: [],
            player3cards: [],
            player4cards: [],
            player1played: [],
            player2played: [],
            player3played: [],
            player4played: [],
            team13points: 0,
            team24points: 0,
            team13rank: 2,
            team24rank: 2,
        });
        const query = `INSERT INTO game VALUES (${id},'${req}')`;
        localStorage.setItem('username', this.state.value);
        localStorage.setItem('room', id);
        await fetch('http://localhost:5000/post', {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({
                query: query
            })
        });
        this.setState({
            redirect: true,
        });
    }

    handleRoom(event) {
        this.setState({ room: event.target.value });
    }

    handleUser(event) {
        this.setState({ user: event.target.value });
    }

    handleJoin = async event => {
        event.preventDefault();
        const id = this.state.room;
        let query = `SELECT state FROM game WHERE id=${id}`;
        let req;
        await fetch('http://localhost:5000/post', {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({
                query: query
            })
        })
        .then(response => response.json())
        .then(response => req = this.updateState(response))
        req = JSON.stringify(req);
        query = `UPDATE game SET state='${req}' WHERE id=${id}`;
        await fetch('http://localhost:5000/post', {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({
                query: query
            })
        });
        this.setState({
            redirect: true,
        });
    }

    updateState(res) {
        res = res.map(response => response.state);
        localStorage.setItem('username', this.state.user);
        localStorage.setItem('room', this.state.room);
        const response = JSON.parse(res);
        if (response.player2name === '') {
            response.player2name = this.state.user;
        } else if (response.player3name === '') {
            response.player3name = this.state.user;
        } else if (response.player4name === '') {
            response.player4name = this.state.user;
        } else {
            alert('Game is full.');
            localStorage.removeItem('username');
            localStorage.removeItem('room');
        }
        return response;
    }

    render() {
        if (this.state.redirect) {
            return <Redirect to="/game"></Redirect>
        }
        return (
            <div>
                <h1>Join Room</h1>
                <form onSubmit={this.handleJoin}>
                    Room ID:
                    <input type="text" onChange={this.handleRoom} />
                    Username:
                    <input type="text" onChange={this.handleUser} />
                    <input type="submit" value="Join Game" />
                </form>
                <h1>Create Room</h1>
                <form onSubmit={this.handleSubmit}>
                    Username: 
                    <input type="text" onChange={this.handleChange} />
                    <input type="submit" value="Create Game" />
                </form>
            </div>
        );
    }
}

export default Home;