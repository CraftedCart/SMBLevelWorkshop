$("pre code").each(function(){ //Remove leading whitespace in <pre><code> ... </code></pre> blocks
    var html = $(this).html();
    var pattern = html.match(/\s*\n[\t\s]*/);
    $(this).html(html.replace(new RegExp(pattern, "g"),'\n'));
});

hljs.initHighlightingOnLoad(); //Initialize highlight.js

function scrollDown() {
	$('html, body').animate({
        scrollTop: $('body').position().top = $(window).height()
    }, 1000);
}
