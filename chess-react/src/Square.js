import React from 'react';

function Square(props) {
    return (
        <button className={props.className} onClick={props.onClick}>
        </button>
    );
}

export default Square;