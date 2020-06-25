async function getData() {
    const response = await fetch('/data');
    const comments = await response.text();
    if (JSON.parse(comments).length == 0) {
        document.getElementById('data-container').previousElementSibling.style.display = "none";
    }
    else {
        JSON.parse(comments).forEach(comment => {
            document.getElementById('data-container').appendChild(createListElement(comment.comment));
        })   
    }
}

function createListElement(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    liElement.classList.add('list-group-item', 'list-group-item-light');
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