async function getData() {
    const response = await fetch('/data');
    const messages = await response.text();
    JSON.parse(messages).forEach(message => {
        document.getElementById('data-container').appendChild(createListElement(message));
    })
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
}

let i = 0;
let text = "Learn to Code in HTML, CSS, JavaScript, jQuery, Node, Python, and Java";
let speed = 100;

function type() {
	if (i < text.length) {
		document.getElementById("text").innerHTML += text.charAt(i);
		i++;
		setTimeout(type, speed);
	}
	else {
		document.getElementById("text").innerHTML = "";
		i = 0;
		type();
	}
}

$(document).ready(function() {
    type();
})