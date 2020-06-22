async function getData() {
  const response = await fetch('/data');
  const data = await response.text();
  document.getElementById('data-container').innerHTML = data;
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