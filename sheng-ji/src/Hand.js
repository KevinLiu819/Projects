import React from 'react';

function Hand(props) {

    if (!props.value) {
        return null;
    }

    let images = props.value.slice();
    for (let i = 0; i < images.length; i++) {
        images[i] = 'images/' + images[i] + '.png';
    }
    let clicked = props.clicked.slice();
    return (<div>{renderHand()}</div>)

    function renderHand() {
        const items = [];
        for (let i = 0; i < images.length; i++) {
            items.push(renderCard(i));
        }
        return items;
    }

    function renderCard(i) {
        if (images[i] === null) {
            return null;
        } else {
            const style = {
                position: 'absolute',
                width: '70px',
                height: '100px',
                zIndex: i,
                top: 700 - (clicked[i] ? 30 : 0) + 'px',
                left: 20 * i + 0 + 'px',
            };
            return <img key={i} style={style} src={images[i]} onClick={() => handleClick(i)} />
        }
    }

    function handleClick(i) {
        props.change(i);
    }

}

export default Hand;