import React from 'react';
import Square from './Square';

class Board extends React.Component {
    
    constructor(props) {
        super(props);
        this.state = {
            squares: ['br', 'bn', 'bb', 'bq', 'bk', 'bb', 'bn', 'br',
                      'bp', 'bp', 'bp', 'bp', 'bp', 'bp', 'bp', 'bp',
                      null, null, null, null, null, null, null, null,
                      null, null, null, null, null, null, null, null,
                      null, null, null, null, null, null, null, null,
                      null, null, null, null, null, null, null, null,
                      'wp', 'wp', 'wp', 'wp', 'wp', 'wp', 'wp', 'wp',
                      'wr', 'wn', 'wb', 'wq', 'wk', 'wb', 'wn', 'wr'],
            wIsNext: true,
            winner: null,
            from: null,
        };
    }

    render() {
        return (
            <div>
                {this.renderRow(0)}
                {this.renderRow(1)}
                {this.renderRow(2)}
                {this.renderRow(3)}
                {this.renderRow(4)}
                {this.renderRow(5)}
                {this.renderRow(6)}
                {this.renderRow(7)}
            </div>
        );
    }

    renderRow(row) {
        return (
            <div className="board-row">
                {this.renderSquare(row * 8)}
                {this.renderSquare(row * 8 + 1)}
                {this.renderSquare(row * 8 + 2)}
                {this.renderSquare(row * 8 + 3)}
                {this.renderSquare(row * 8 + 4)}
                {this.renderSquare(row * 8 + 5)}
                {this.renderSquare(row * 8 + 6)}
                {this.renderSquare(row * 8 + 7)}
            </div>
        );
    }

    handleClick(i) {
        if (this.state.from == null) {
            this.setState({
                squares: this.state.squares,
                wIsNext: this.state.wIsNext,
                winner: this.state.winner,
                from: i,
            });
        } else {
            const squares = this.state.squares.slice();
            squares[i] = squares[this.state.from];
            squares[this.state.from] = null;
            this.setState({
                squares: squares,
                wIsNext: !this.state.wIsNext,
                winner: this.state.winner,
                from: null,
            });
        }
    }

    renderSquare(i) {
        let className = 'square';
        const row = Math.floor(i / 8);
        const col = i % 8;
        if ((row + col) % 2 == 0) {
            className += ' light';
        } else {
            className += ' dark';
        }
        if (this.state.squares[i]) {
            className += ' ' + this.state.squares[i];
        }
        if (this.state.from == i) {
            className += ' clicked';
        }
        return <Square className={className} onClick={() => this.handleClick(i)} />;
    }

}

export default Board;