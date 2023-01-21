# small-projects
The repo consists of various kinds of small scale projects spanning a single file to a few files.

## List of Projects
<ul>
    <li> Morse Code Translator
    <li> Market Index Fetch
    <li> Bulk Image Download
</ul>

# Project Introduction

## Morse Code Translator
- Basic morse code encoder and decoder functions

## Market Index Fetch
- Small script which contains two functions, one which returns well-formatted dictionary of strings containing rate of conversions of given currencies to INR. The second function returns the closing prices and the volume for the given share tickers alongside saving them in an html page as a table.

## Bulk Image Download
- Web scrapping script that can download any accessible images/thumbnails present in an html web-page inside img tag.
### Corrections Needed
- The script targets the image location shown in the webpage and not the image link in the <i>href</i> property of the anchor tag inside which image is placed. So when such a case occurs (many times when in need of bulk download of image), the script downloads the image rendered on the webpage which may the thumbnail and not the actual image itself.
- This needs to be rectified.