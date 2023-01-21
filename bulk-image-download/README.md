# Bulk Images Downloading

## <u>Modules used</u>

### BeautifulSoup
<ul>
    <li> BeautifulSoup is a python module useful for web-scrapping. The module is used here to extract image urls from the html code of the given web page url
</ul>

### requests
<ul>
    <li> requests is an HTML library return in python. It is has various method implementations to iteract with webpages through python. Its <i> get </i> method is used to download the images.
</ul>

## <u>Functions Implemented</u>

### fetchImages(url)
<ul>
    <li> The function uses <i> requests.get </i> function call to get the html text of the given url page.
    <li> It than uses beautiful soup data structure to parse the html data to extract all <u>data-src</u> and <u>src</u> attributes from the image tags.
    <li> It returns a list of image urls formatted using regex module to be treated as absolute url path.
</ul>

### extractImageName(image_url)
<ul>
    <li> The function extracts name and extension of the image file and returns the file-name. If no file exists, it returns <b>None</b>.
</ul>

### downloadImages(images)
<ul>
    <li> Using <i>get</i> function call from <i>requests</i> library, the function downloads the image data.
    <li> Image name is extracted using the <i><u>extractImageName</u></i> function.
    <li> It stores the image with the same name inside <b> images </b> directory and also shows log message regarding wether a downloading is successful with respect an image or not and stores it inside <b>logFile.txt</b>.
</ul>