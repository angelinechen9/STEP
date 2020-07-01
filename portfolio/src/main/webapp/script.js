let i = 0;
const text = "Learn to Code in HTML, CSS, JavaScript, jQuery, Node, Python, and Java";
const speed = 100;

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